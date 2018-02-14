package cmn.util.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import cmn.util.base.BaseConstants;
import cmn.util.base.BaseConstants.TransactionStatus;
import cmn.util.common.NullUtil;
import cmn.util.common.ResMessage;
import cmn.util.converter.JsonUtil;
import cmn.util.exception.BaseException;
import cmn.util.exception.BizException;
import cmn.util.exception.UtilException;
import cmn.util.spring.HttpUtil;
import cmn.util.spring.TranLogService;

public class JsonExceptionResolver extends SimpleMappingExceptionResolver implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonExceptionResolver.class);

	@Autowired
	Environment environment;
	
	/** Message Source **/
    @Resource(name ="messageSource")
    protected MessageSource messageSource;
    
    @Resource(name = "tranLogService")
    private TranLogService tranLogService;
    
    private final  String exceptionCodeAttribute = "exception";
    
    private static final int EXCEPTION_LOG_SIZE = 2500;
    
    private boolean dbLog;

    @Override
    protected ModelAndView getModelAndView(String viewName, Exception ex) {

    	ModelAndView mav = super.getModelAndView(viewName,ex);
		Map<String, Object> model = mav.getModel();
		
		String code = model.get(this.exceptionCodeAttribute)!=null ? (String)model.get(this.exceptionCodeAttribute):BaseConstants.DEFAULT_EXCEPTION_ERROR_CODE;

		/** Print out exception **/
		ex.printStackTrace();
		
        String message = ex.getMessage();
        Map<String, Object>errMessage = null;
        try {
            errMessage = ResMessage.makeResponseWithMessage(code, message, true);        	
        }
        catch(Exception msgex) {
        	LOGGER.error("Message Generation error :: {}", msgex.getMessage());
        }
        
        model.put(BaseConstants.RESPONSE_BODY_MESSAGE, errMessage);

		return mav;
    }
 
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    	String msgCode = null;
    	String msgValue = null;
    	Object[] msgParam = null;
    	
    	List<String> profiles = Arrays.asList(environment.getActiveProfiles());
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Current Profiles :: {}", profiles.toArray());
    	}
    	
    	/** Exception printout **/
		ex.printStackTrace();
		
        int tranSeq = 0;
        try {
        	
        	tranSeq = HttpUtil.getRequestData("tranSeq");
		} catch (Exception e) {
			LOGGER.error("Transaction sequence dosen't find in the http request");
		}

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(bos));
        String exceptionMsg = new String(bos.toByteArray());
        
//        LOGGER.error("{} :: {}", ex.getClass().getName(), exceptionMsg);

        if (exceptionMsg.length() > EXCEPTION_LOG_SIZE) {
        	exceptionMsg = exceptionMsg.substring(0, EXCEPTION_LOG_SIZE);
        }
        
    	if (ex instanceof BizException) {
    		msgCode =  ((BaseException) ex).getCode();
    		msgParam = ((BaseException) ex).getParam();
    		if (profiles.contains("local") || profiles.contains("dev")) {
        		msgValue = ex.getMessage();	
    		}
    	}
    	else if (ex instanceof UtilException) {
    		msgCode = BaseConstants.DEFAULT_EXCEPTION_URIL_CODE;
    		msgParam = ((BaseException) ex).getParam();
 
    		/** If profile is operation or quality assurance, then set message user friendly **/ 
    		if (profiles.contains("local") || profiles.contains("dev")) {
        		msgValue = ex.getMessage();    			
    		}
    	}
    	else if (ex instanceof MyBatisSystemException || ex instanceof PersistenceException || ex instanceof DataAccessException ) {
    		msgCode = BaseConstants.DEFAULT_EXCEPTION_DB_CODE;

    		/** If profile is operation or quality assurance, then set message user friendly **/ 
    		if (profiles.contains("local") || profiles.contains("dev")) {
        		msgValue = ex.getMessage();    			
    		}
    	}
    	else {
    		/** Set Default Message Code **/
    		msgCode = BaseConstants.DEFAULT_EXCEPTION_ERROR_CODE;
    		/** If profile is operation or quality assurance, then set message user friendly **/ 
    		if (profiles.contains("local") || profiles.contains("dev")) {
        		msgValue = ex.getMessage();	
    		}    		
    	}
    	
    	/** Set error code and message for client **/
        Map<String, Object> errMessage = null;
        try {
        	if (NullUtil.isNull(msgValue)) {
                errMessage = ResMessage.makeMessage(msgCode, msgParam, true);
        	}
        	else {
                errMessage = ResMessage.makeMessage(msgCode, msgValue, true);
        	}
        }
        catch(Exception msgex) {
        	/** When Exception occurs, processing next step **/ 
        	LOGGER.error("MessageUtil getMessage Error :: {}", msgex.getMessage());
        	
        }

       	LOGGER.info("Error Message to clinet :: {}", errMessage.toString());
       	
       	
       	
       	/** Transaction Log update **/
       	/** If occurred error, process next step **/
       	if (dbLog) {
           	Map<String, Object> transLog = new HashMap<String, Object>();
           	try {
           		transLog.put("tranSeq", tranSeq);
           		transLog.put("callStartTime", new Date());
           		transLog.put("callEndTime", new Date());
           		transLog.put("endTime", new Date());
           		transLog.put("status", TransactionStatus.FAILED);
           		transLog.put("exitCode", "99");
           		transLog.put("exitMessage", exceptionMsg);
    	       	transLog.put("responseData", JsonUtil.map2Json(errMessage));
    	       	
    	       	/** Transaction Log Sequence doesn't find, insert transaction **/
    	       	if (tranSeq == 0) {
    	       		tranSeq = tranLogService.selectSequence();
    	       		transLog.put("tranSeq", tranSeq);
    	       		tranLogService.insertTransLog(transLog);
    	       	}
    	       	else {
    				tranLogService.updateTransLog(transLog);	       		
    	       	}
    			
    		} catch (Exception logex) {
    			LOGGER.error("Transaction Log update error {} :: {}", tranSeq, logex.getMessage());
    		}       		
       	}
       	    	
       	/** Set JSON View **/
       	ModelAndView mav = new ModelAndView(BaseConstants.DEFAULT_VIEW_NAME);
       	mav.addObject("resMessage",errMessage);

       	return mav;
    }

	public void setDbLog(boolean dbLog) {
		this.dbLog = dbLog;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (NullUtil.isNull(dbLog)) {
			dbLog = false;
		}
	}
}
