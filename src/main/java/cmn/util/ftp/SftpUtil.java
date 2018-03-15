

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.SftpException;


public class SftpUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SftpUtil.class);
	
	
	/** SFTP Channel **/
	private SftpChannel sftpChannel = null; 
	
	/** Encoding Character **/
	private String encoding = null;

	public void setftpChannel(KicsSftpChannel sftpChannel) {
		this.sftpChannel = sftpChannel;
	}


	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}



	/**
	 * Upload File
	 * <pre>
	 *
	 * </pre>
	 * @param remoteDir String
	 * @param localPath String
	 * @return boolean
	 * @throws Exception
	 */
    public boolean upload(String remoteDir, String localPath) throws Exception {
        boolean result = true;
        FileInputStream in = null;
        try {
            File file = new File(localPath);
            String fileName = file.getName();
            
            in = new FileInputStream(file);
            sftpChannel.getChannelSftp().cd(remoteDir);
            sftpChannel.getChannelSftp().put(in, fileName);
            LOGGER.info("{} is uploaded from {}", localPath, remoteDir);
            
        } catch (Exception e) {
            result = false;
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        
        return result;
    }
	

    /**
     * File download
     * <pre>
     *
     * </pre>
     * @param remoteDir String
     * @param fileName String
     * @param localPath String
     */
    public void download(String remoteDir, String fileName, String localPath) {
        InputStream in = null;
        FileOutputStream out = null;
        try {

        	sftpChannel.getChannelSftp().cd(remoteDir);
            in = sftpChannel.getChannelSftp().get(fileName);
        } catch (SftpException e) {
            LOGGER.error(e.getMessage());
        	throw new RuntimeException(e.getMessage());
        }
 
        try {
            out = new FileOutputStream(new File(localPath));
        	IOUtils.copy(in, out);
            LOGGER.info("{} is downloaded to {}", localPath, remoteDir);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        	throw new RuntimeException(e.getMessage());
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }    
    
}


/**
* Configuration
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">
	
	<!-- SFTP UTIL -->
    <bean id="sftpUtil" class="kics.framework.ftp.KicsSftpUtil">
        <property name="kicsSftpChannel" ref="kicsSftpChannel" />
    </bean>

    <!-- SFTP Channel -->              
    <bean id="kicsSftpChannel" class="kics.framework.ftp.KicsSftpChannel" init-method="init" destroy-method="disconnection">
        <property name="host" value="200.100.105.12"/>
        <property name="port" value="22"/>
        <property name="username" value="jeus" />
        <property name="password" value="jeus" />
    </bean>   
</beans>
**/
