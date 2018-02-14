
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;


public class FileDownloadView extends AbstractView implements InitializingBean{

	private static final Logger LOGGER = LogManager.getLogger(FileDownloadView.class);
	
	/** Set Default Download file Name */
	private static final String DEFAULT_DOWNLOAD_FILES = "downloadFiles";
	
	/** Set Default target file name **/
	private static final String DEFAULT_TARGET_FILE = "targetFileName";	
	
	/** Set Default Encoding Character **/
	private String encoding = null;
	
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}


	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String osType = System.getProperty("os.name");

		/** Get Filename from model  **/
		if (model.get(DEFAULT_DOWNLOAD_FILES) == null) {
			throw new LRuntimeException("Download file is null");
		}		
		
		String targetFileName = null;
		if (model.get(DEFAULT_TARGET_FILE) == null) {
			throw new LRuntimeException("Download target file name is null");
		}
		else {
			targetFileName = String.valueOf(model.get(DEFAULT_TARGET_FILE));
		}
	
		/** File Check */
		File file = new File(targetFileName);

		String userAgent = request.getHeader("User-Agent");
		String fileName = null;

		/** File name according to browser **/
		boolean ie = userAgent.indexOf("MSIE") > -1;
		if (ie) {
			fileName = URLEncoder.encode(file.getName(), encoding);
		} else {
			fileName = new String(file.getName().getBytes(encoding));
		}

		/**
		 * Set Response properties
		 */
		response.setContentType(getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");

					
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Download File :: {}/{}", file.getParent(), file.getName());
		}

		if (model.get(DEFAULT_DOWNLOAD_FILES) instanceof String) {
			String srcFileName = String.valueOf(model.get(DEFAULT_DOWNLOAD_FILES));
			downloadSingleFile(srcFileName, response);
		}
		
		else if (model.get(DEFAULT_DOWNLOAD_FILES) instanceof List){

			List<String> fileList =(List<String>) model.get(DEFAULT_DOWNLOAD_FILES);
			String[] fileNameList = new String[fileList.size()];
			int idx = 0;
			for (String tempFile : fileList) {
				fileNameList[idx++] = tempFile;
			}
			downloadMultiFile(targetFileName, fileNameList, response);
		}
		else if (model.get(DEFAULT_DOWNLOAD_FILES) instanceof String[]) {
			downloadMultiFile(targetFileName, (String[])model.get(DEFAULT_DOWNLOAD_FILES), response);			
		}

		LOGGER.info("File download fininshed :: {}",targetFileName);
	}

	/**
	 * Download single file
	 *<pre>
	 *
	 *</pre>
	 * @param fileName
	 * @throws Exception
	 */
	private void downloadSingleFile(String srcFileName, HttpServletResponse response) throws Exception {
		
		File file = new File(srcFileName);
		
		response.setContentLengthLong(file.length());
		response.setBufferSize((int)file.length());
		OutputStream out = response.getOutputStream();
		FileInputStream fis = null;
		
		/** File download **/
		try {
			fis = new FileInputStream(srcFileName);
			FileCopyUtils.copy(fis, out);
			out.flush();
		} catch (Exception ex) {
			throw new LRuntimeException("File download error(copy) :: {}", srcFileName);
		} 
		finally {
			if (fis != null) {
				fis.close();
			}
			if (out != null) {
				out.close();				
			}
		}
	}
	
	
	/**
	 * Download Multiple File with ZIP format
	 *<pre>
	 *
	 *</pre>
	 * @param fileList
	 * @throws Exception
	 */
	private void downloadMultiFile(String targetFileName, String[] fileList, HttpServletResponse response) throws Exception {
		
		OutputStream out = response.getOutputStream();
		
		try {
			byte[] downBytes = FileUtil.getZipConentes(fileList);
			response.setContentLength(downBytes.length);
			response.setBufferSize(downBytes.length);

			out.write(downBytes);
			out.flush();
		} catch (Exception ex) {
			throw new LRuntimeException("File download error(copy) :: {}", targetFileName);
		} 
		finally {
			if (out != null) {
				out.close();				
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (encoding == null) {
			encoding = DefaultConstants.DEFAULT_ENCODING;
		}
	}
}
