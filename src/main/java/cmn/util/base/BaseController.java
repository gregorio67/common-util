package cmn.util.base;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import cmn.util.spring.PropertiesUtil;

public class BaseController {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name = "propertiesUtil")
	private PropertiesUtil propertiesUtil;
	
    @Resource(name ="messageSource")
    protected MessageSource messageSource;
    

    
    /**
     * 
     *<pre>
     * Return Request Header
     *</pre>
     * @param param Map<String, Object>
     * @return T
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	protected <T> T getRequestHeader(Map<String, Object> param) throws Exception {
       	return (T)param.get(BaseConstants.REQUEST_PARAM_HEADER);    		
    }
    
    /**
     * 
     *<pre>
     * Return Json Request Data
     *</pre>
     * @param param Map<String, Object>
     * @return T
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	protected <T> T getRequestData(Map<String, Object> param) throws Exception {
       	return (T)param.get(BaseConstants.REQUEST_PARAM_BODY);    		
    }
    
}
