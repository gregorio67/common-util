import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import framework.base.BaseConstants;
import framework.exception.BizException;
import framework.spring.ReloadPropertiesUtil;
importframework.util.FileUtil;
import framework.util.NullUtil;
import framework.util.StringUtil;

public class Uploadnterceptor extends HandlerInterceptorAdapter implements InitializingBean{

	private static final Logger LOGGER = LoggerFactory.getLogger(Uploadnterceptor.class);
	
	private static final long BASE_FILE_SIZE = 1024; 
	
	private static final long DEFAULT_FILE_MAX_SIZE = 20480;
	
	private static final long DEFAULT_TOTAL_FILE_MAX_SIZE = 204800;
	
	/** File upload deny Pattern **/
	private String[] denyPatterns;
	
	/** File upload allow patterns **/
	private String[] allowPatterns;
	
	/** Sub directory parameter name **/
	private String subDirParamName;
	
	/** Save file parameter name **/
	private String saveFileParamName;
	
	/** Allow file max size for each file **/
	private long maxFileSize;
	
	/** Allow total file max size **/
	private long totalMaxFileSize;
	
		
	public void setDenyPatterns(String[] denyPatterns) {
		this.denyPatterns = denyPatterns;
	}

	public void setAllowPatterns(String[] allowPatterns) {
		this.allowPatterns = allowPatterns;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public void setTotalMaxFileSize(long totalMaxFileSize) {
		this.totalMaxFileSize = totalMaxFileSize;
	}

	
	public void setSubDirParamName(String subDirParamName) {
		this.subDirParamName = subDirParamName;
	}

	
	public String getSaveFileParamName() {
		return saveFileParamName;
	}

	public void setSaveFileParamName(String saveFileParamName) {
		this.saveFileParamName = saveFileParamName;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request instanceof MultipartHttpServletRequest) {
			
			/** File Upload Root Directory **/
			String rootDir = ReloadPropertiesUtil.getString("file.upload.root.dir") != null ? StringUtil.deleteLastChar(ReloadPropertiesUtil.getString("file.upload.root.dir"), '/') : null;
			
			/** File upload directory is null, set default upload directory **/
			if (NullUtil.isNull(rootDir)) {
				String osName = System.getProperty("os.name");
				if (osName.contains("win")) {
					rootDir = "D:/temp/file";
				}
				else {
					rootDir = "/temp";
				}
			}
			/**  If file upload directory is not exist, create directory **/
			FileUtil.makeDir(rootDir);
			
			/**  Get FileNames from HttpServletRequest **/
			Map<String, MultipartFile> mFiles = ((MultipartHttpServletRequest) request).getFileMap();
			Iterator<String> mFileItr = mFiles.keySet().iterator();
			MultipartFile mFile = null;
			
			LinkedList<Map<String, Object>> files = new LinkedList<Map<String, Object>>();
			
			/**  Sub directory **/
			String subDir =request.getParameter(subDirParamName) != null ?  StringUtil.deleteLastChar(request.getParameter(subDirParamName), '/') : "";
			String fileName = request.getParameter(saveFileParamName) != null ?  request.getParameter(saveFileParamName) : "";
			
			String uploadFile = null;
			if (!NullUtil.isNull(subDir)) {
				rootDir = rootDir + File.separator + subDir + File.separator;
				FileUtil.makeDir(rootDir);
			}
			int ltotalMaxFileSize = 0;
			
			
			while(mFileItr.hasNext()) {
				String key = mFileItr.next();
				mFile = mFiles.get(key);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Uploading File :: ", mFile.getOriginalFilename());
				}
				
				/** Check max file size **/
				if (mFile.getSize() > (maxFileSize * BASE_FILE_SIZE) ) {
					LOGGER.error("Upload File Size {} > Allow Max File Size {} ", mFile.getSize(), maxFileSize);
					
					throw  new BizException("sys.err.frame.013", new String[] { " File size is bigger than allow upload file size"});
				}
				
				String ext = FileUtil.getFileExtension(mFile.getOriginalFilename());
				
				/** Check allow pattern **/
				if (!NullUtil.isNull(allowPatterns)) {
					boolean isAllow = false;
					for (String allowPattern : allowPatterns) {
						if (ext.equals(allowPattern)) {
							isAllow = true;
							break;
						}
					}
					if (!isAllow) {
						throw  new BizException("sys.err.frame.013", new String[] { "This file pattern is not allow to upload"}); 						
					}
					
				}
				/** Check deny pattern **/
				if (!NullUtil.isNull(denyPatterns)) {
					for (String denyPattern : denyPatterns) {
						if (ext.equals(denyPattern)) {
							throw  new BizException("sys.err.frame.013", new String[] { "This file pattern is not allow to upload"}); 
						}
					}					
				}
				
				
				Map<String, Object> fMap = FileUtil.getMultiPartInfo(mFile);
			
				if (NullUtil.isNull(fileName)) {
					uploadFile  = rootDir + File.separator + mFile.getOriginalFilename();
					fMap.put("uploadFile", uploadFile);
					fMap.put("saveFileName", mFile.getOriginalFilename());
				}
				else {
					uploadFile  = rootDir + File.separator + fileName;
					fMap.put("saveFileName", fileName);
					fMap.put("uploadFile", uploadFile);
				}
				
				
				
				files.add(fMap);
			}
			
			/** Check total upload max file size **/
			if ((ltotalMaxFileSize * BASE_FILE_SIZE) > totalMaxFileSize) {
				throw  new BizException("sys.err.frame.013", new String[] {"The uploaded otal file size is bigger than allowed total file size"});
			}
			

			/** Save File **/
			for (Map<String, Object> map : files) {
				String saveFile = map.get("uploadFile") != null ? String.valueOf(map.get("uploadFile")) : "";
				if (!NullUtil.isNone(saveFile)) {
					FileCopyUtils.copy(mFile.getBytes(), new FileOutputStream(saveFile));					
				}
			}
			/** Set File information to HttpServletRequest **/

			request.setAttribute(BaseConstants.DEFAULT_FILE_UPLOAD_KEY_NAME, files);
			
		}
		
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request,	HttpServletResponse response, Object handler, ModelAndView mav) throws Exception {

	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (NullUtil.isNull(this.allowPatterns) && NullUtil.isNull(this.denyPatterns)) {
			throw new BizException("Set allowPatterns or denyPatterns for file interceptor");
		}
		
		if (maxFileSize <= 0) {
			this.maxFileSize = DEFAULT_FILE_MAX_SIZE;
		}

		if (totalMaxFileSize <= 0) {
			this.totalMaxFileSize = DEFAULT_TOTAL_FILE_MAX_SIZE;
		}
	}

}

/**
    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">
        <property name="interceptors">
            <list>
                <ref bean="localeChangeInterceptor" />
                <ref bean="sessionInteceptor" />
                <ref bean="uploadInterceptor" />
            </list>
        </property>
    </bean>

	<bean id="uploadInterceptor" class="stis.framework.spring.interceptor.Uploadnterceptor">
		<property name="denyPatterns">
			<list>
				<value>cmd</value>
				<value>jsp</value>
				<value>sh</value>
				<value>java</value>
			</list>
		</property>
		<!-- property name="allowPatterns">
			<list>
				<value>pdf</value>
				<value>xls</value>
				<value>ppt</value>
				<value>txt</value>
			</list>
		</property-->
		<!-- K Bytes -->
		<property name="maxFileSize" value="20480" />
		<property name="totalMaxFileSize" value="204800" />
		<property name="saveFileParamName" value="saveFileName" />
		<property name="subDirParamName" value="subDir" />
	</bean>
**/
