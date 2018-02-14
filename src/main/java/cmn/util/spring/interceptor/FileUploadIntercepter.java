
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;	
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import taxris.framework.collection.FileUploadVo;
import taxris.framework.constants.DefaultConstants;
import taxris.framework.exception.LRuntimeException;
import taxris.framework.util.ApplicationContextProvider;
import taxris.framework.util.FileUploadSpec;
import taxris.framework.util.FileUtil;
import taxris.framework.util.NullUtil;


public class FileUploadIntercepter extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LogManager.getLogger(FileUploadIntercepter.class);
	
	private FileUploadSpec uploadSpec = null;
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		if ( request instanceof MultipartHttpServletRequest ) {
			MultipartHttpServletRequest multiRequest = ((MultipartHttpServletRequest)request);
			
			RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(multiRequest));

			/** Extract MultipartFile **/
			List<MultipartFile> multipartFiles = new ArrayList<MultipartFile>();
			Iterator<String> fileNames = multiRequest.getFileNames();
			while( fileNames.hasNext() ){
				multipartFiles.addAll( multiRequest.getFiles( fileNames.next()));
			}

			/** Upload Spec **/
			String policy = request.getParameter("ploicy");
			if (NullUtil.isNull(policy)) {
				policy = DefaultConstants.DEFAULT_FILE_UPLOAD_POLICY;
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Object> filePolicy = ApplicationContextProvider.getApplicationContext().getBean(policy, Map.class); 
						
			if ( NullUtil.isNone(filePolicy) ) {
				LOGGER.error("File upload policy is not exists.");
				throw new LRuntimeException("File upload policy is not exists");
			}

	    	List<FileUploadVo> fileInfoList = new ArrayList<FileUploadVo>();
	    	
	    	// 오류 발생시 upload된 파일을 삭제하기 위해.
	    	ArrayList<File> cleanupBuffer = new ArrayList<File>();
	    	
	    	uploadSpec = new FileUploadSpec();
	    	uploadSpec.init(filePolicy);
	    	
	    	try {
	    		
	    		// 파일 카운트
	    		int filecnt = 1;
		    	
	    		String uploadFileName;
	    		Iterator<MultipartFile> iterator = multipartFiles.iterator();
	    		
	    		long totalFileSize = 0;
	    		while( iterator.hasNext() ){

	    			MultipartFile multipartFile = iterator.next();
	
		    		if ( !"".equals( multipartFile.getOriginalFilename() ) ) {
		    			
		    			FileUploadVo uploadVo = new FileUploadVo();
		    			
		    			String fileName = multipartFile.getOriginalFilename();
		    			uploadVo.setOrgFileName(fileName);
		    			
		    			if (!uploadSpec.isAllowed(fileName)) {
		    				throw new LRuntimeException("This file is not allowed");
		    			}
		    			
		    			if (uploadSpec.isDenied(fileName)) {
		    				throw new LRuntimeException("This file is not allowed");
		    			}
		    			
		    			long fileSize = multipartFile.getSize();
		    			if (uploadSpec.isFileSizeExceed(fileSize)) {
		    				throw new LRuntimeException("This file is not allowed because file size exceeded :[" + fileSize + "]");		    				
		    			}
		    			uploadVo.setFileSize(fileSize);
		    			
		    			totalFileSize +=  fileSize;
		    			
		    			String uploadDir = uploadSpec.getUploadDir();
		    			uploadVo.setTargetPath(uploadDir);
		    			
		    			File file = new File(uploadDir + fileName);
		    			cleanupBuffer.add(file);
		    			if (file.exists()) {
		    				if (uploadSpec.isDupFileDelete()) {
		    					file.delete();
				    			FileUtil.copyFile(multipartFile.getInputStream(), uploadDir + fileName);
		    				}
		    				else {
		    					String extType = uploadSpec.getDuplicatedFilePostfix();
		    					if (!NullUtil.isNull(extType)) {
			    					String strFileName = FileUtil.getFileWithPostFix(fileName, extType);
					    			FileUtil.copyFile(multipartFile.getInputStream(), uploadDir + fileName);
		    					}
		    					else {
					    			FileUtil.copyFile(multipartFile.getInputStream(), uploadDir + fileName);		    						
		    					}
		    				}
		    			}
			    		fileInfoList.add(uploadVo);
		    		}		                
		    		filecnt++;
		    	}
			} 
	    	catch ( Exception ex ) {
	            Iterator<File> iter = cleanupBuffer.iterator();
	            while ( iter.hasNext() ) {
	                File tfile = (File)iter.next();
	                boolean result = tfile.delete();
	                if ( result ) {
//                		LOGGER.error("FileUploadVo:" + ToStringBuilder.reflectionToString(uploadVo, ToStringStyle.MULTI_LINE_STYLE));
                		LOGGER.error("{} file is deleted because file upload is failed. [" + tfile.getAbsolutePath() + "]");
	                } 
	                else {
//	                	LOGGER.error("kcs4gDefaultPolicyVo:" + ToStringBuilder.reflectionToString(uploadVo, ToStringStyle.MULTI_LINE_STYLE));
	                	LOGGER.error("{} file is deleted because file upload is failed.  [" + tfile.getAbsolutePath() + "]");
	                }
	            }
	            // 파일 경로 노출 보안 조치
				if ( ex instanceof FileNotFoundException ) {
					throw new LRuntimeException("File doesn't exist");
				}
				throw new LRuntimeException(ex.getMessage());
				
			}	    	
	    	request.setAttribute( "fileInfo", fileInfoList );
		}
		
		return super.preHandle(request, response, handler);
	}
	
	/**
	 * Return void
	 * 
	 * @param HttpServletRequest
	 * @param HttpServletResponse
	 * @paeam Object handler
	 * @return void
	 * @see
	 */
	@Override
	public void postHandle(HttpServletRequest request,	HttpServletResponse response, Object handler, ModelAndView mav) throws Exception {

	}

	/**
	 * 
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

}
