import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;



public class SftpUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SftpUtil.class);
	
	private static Map<String, ChannelSftp> sftpChannels = new LinkedHashMap<String, ChannelSftp>();
		


    public boolean upload(ChannelSftp channel, String remoteDir, String localPath, String remoteFileName) throws Exception {
        boolean result = true;
        FileInputStream in = null;
        try {
            File file = new File(localPath);
            if (!file.exists()) {
            	throw new BizException(String.format("Local File Not Found :: %s", localPath));
            }
            
            in = new FileInputStream(file);

            channel.cd(remoteDir);
            
            /** Move file from local to remote **/
            channel.put(in, remoteFileName);
            LOGGER.info("{} is uploaded from {} {}", localPath, remoteDir, remoteFileName);
            
        } catch (Exception e) {
            result = false;
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        
        return result;
    }

 


    public boolean download(ChannelSftp channel, String remoteDir, String remoteFileName, String localPath, String localFileName) throws Exception {
        InputStream in = null;
        FileOutputStream out = null;
        
        /** Check local directory  **/
        FileUtil.makeDir(localPath);
        
        try {

        	/** Move to remote directory **/
        	channel.cd(remoteDir);
        	/** Read remote file **/
            in = channel.get(remoteFileName);
            if (in == null) {
            	throw new BizException(String.format("File Not Found :: [%s]", remoteFileName));
            }
        } catch (SftpException e) {
            LOGGER.error(e.getMessage());
        	throw new BizException(e.getMessage());
        }
 
        try {
        	
        	String downloadFileName = localPath + File.separator + localFileName;
        	FileUtil.makeDir(downloadFileName);
            
        	out = new FileOutputStream(new File(downloadFileName));
        	/** Write remote file to local file **/
        	IOUtils.copy(in, out);
        	
            LOGGER.info("{} is downloaded to {}", remoteDir, localPath );
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        	throw new BizException(e.getMessage());
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (IOException e) {
                throw new BizException(e.getMessage());
            }
        }
        
        return true;
    }      

    

    public byte[] readFile(ChannelSftp channel, String remoteDir, String fileName) throws Exception {
        InputStream in = null;
        byte[] readBytes = null;
        try {

        	channel.cd(remoteDir);
            in = channel.get(fileName);

            readBytes = FileUtil.getContents(in);
        } catch (SftpException e) {
            LOGGER.error(e.getMessage());
        	throw new BizException(e.getMessage());
        }
        finally {
        	if (in != null) in.close();
        }
        
        return readBytes;
     }        
    

    
	public ChannelSftp conneect(String host, int port, String username, String password) throws Exception {
		
		String sftpId = host + ":" + port + ":" + username + ":" + password;
		synchronized(this) {
			if (sftpChannels.get(sftpId) != null) {
				ChannelSftp channel = sftpChannels.get(sftpId);
				if (channel.isConnected()) {
					return sftpChannels.get(sftpId);					
				}
			}
			
			Channel channel = null;
			
			JSch jsch = new JSch();
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SFTP Connection Info :: {}, {}, {}, {}", host, port, username, password);
			}
			try {
			    Session session = null;
			    session = jsch.getSession(username, host, port);    
		        session.setPassword(password);
			    
		        java.util.Properties config = new java.util.Properties();
		        config.put("StrictHostKeyChecking", "no");
		        session.setConfig(config);
		        session.connect();
		        
		        channel = session.openChannel("sftp");
		        channel.connect();			
			}
			catch(Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
	        
	        if (LOGGER.isDebugEnabled()) {
	        	LOGGER.debug("SFTP Channel is created :: {}, {}, {}, {}", host, port, username, password);
	        }
	        
	        sftpChannels.put(sftpId, (ChannelSftp)channel);
	        
			return channel != null ? (ChannelSftp)channel : null;
			
		}
	}

	public boolean disconnect() throws Exception {
		Iterator<String> itrChannel = sftpChannels.keySet().iterator();
		while(itrChannel.hasNext()) {
			String key = itrChannel.next();
			ChannelSftp channel = sftpChannels.get(key);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} is disconnected", key);
			}
			if (channel.isConnected()) {
				channel.disconnect();
			}
		}
		return true;
	}
}

