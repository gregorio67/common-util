import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.ResourceUtils;

import stis.framework.exception.BizException;
import stis.framework.spring.service.FileService;
import stis.framework.spring.service.impl.PropertieFileServiceImpl;

public class BaseFileObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFileObserver.class);
	
	private List<String> directories;
	
	/** File Check interval (unit : milliseconds**/
	private int interval = 500;
	
	/** Service when file or directory is modified **/
	private FileService fileService;
	
	@Required
	public void setDirectories(List<String> directories) {
		this.directories = directories;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	@Required
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public BaseFileObserver() {
		
	}

	public BaseFileObserver(FileService fileService, List<String> directories) {
		this.fileService = fileService;
		this.directories = directories;
	}

	public void init() throws Exception {
		for (String dir : directories) {
			if (dir.startsWith( ResourceUtils.CLASSPATH_URL_PREFIX)) {
				dir = ResourceUtils.getFile(dir).getAbsolutePath();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("File Observe path :: {}", dir);
			}
			FileAlterationObserver observer = new FileAlterationObserver(dir);
			observer.addListener(new FileAlterationListenerAdaptor() {
				@Override
				public void onDirectoryCreate(File file) {
					LOGGER.info("Directory is created : {}", file.getAbsolutePath());
					System.out.println("Directory is created : " + file.getAbsolutePath());
					try {
						fileService.onDirectoryCreate(file);
					}
					catch(Exception ex) {
						throw new BizException(ex.getMessage());
					}
				}
				@Override
				public void onDirectoryChange(File file) {
					LOGGER.info("Directory is changed : {}", file.getAbsolutePath());
					System.out.println("Directory is changed : " +  file.getAbsolutePath());
					
					try {
						fileService.onDirectoryChange(file);
					}
					catch(Exception ex) {
						throw new BizException(ex.getMessage());
					}
				}
				@Override
				public void onDirectoryDelete(File file) {
					LOGGER.info("Directory is deleted : {}", file.getAbsolutePath());
					System.out.println("Directory is deleted : " +  file.getAbsolutePath());
					try {
						fileService.onDirectoryDelete(file);
					}
					catch(Exception ex) {
						throw new BizException(ex.getMessage());
					}
				}
				
				@Override
				public void onFileCreate(File file) {
					LOGGER.info("File is created : {}", file.getAbsolutePath());
					System.out.println("File is created : " + file.getAbsolutePath());
					try {
						fileService.onFileCreate(file);
					}
					catch(Exception ex) {
						throw new BizException(ex.getMessage());
					}
				}
				
				@Override
				public void onFileChange(File file) {
					LOGGER.info("File is modified : {}", file.getAbsolutePath());
					System.out.println("File is modified : " + file.getAbsolutePath());
					try {
						fileService.onFileChange(file);
					}
					catch(Exception ex) {
						throw new BizException(ex.getMessage());
					}
				}
				
				@Override
				public void onFileDelete(File file) {
					LOGGER.info("File is deleted : {}", file.getAbsolutePath());					
					System.out.println("File is deleted : " +  file.getAbsolutePath());					
					try {
						fileService.onFileDelete(file);
					}
					catch(Exception ex) {
						throw new BizException(ex.getMessage());
					}
				}
			});
			
			/** Create monitor to check file is changed **/
			FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
			
			try {
				monitor.start();
			}
			catch(InterruptedException ie) {
				monitor.stop();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> dirs = new ArrayList<String>();
		dirs.add("classpath:properties");
		dirs.add("D:/logagent");
		new BaseFileObserver(new PropertieFileServiceImpl(), dirs).init();
	}
}
