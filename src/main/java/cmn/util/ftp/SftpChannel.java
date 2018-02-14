
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;





public class SftpChannel implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(SftpChannel.class);
	
	private String host;
	
	private String username;
	
	private String password;
	
	private int port;
	
	private ChannelSftp channelSftp = null; 
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}

	public void setChannelSftp(ChannelSftp channelSftp) {
		this.channelSftp = channelSftp;
	}

	SftpChannel() {
//		try {
//		}
//		catch(Exception ex) {
//			LOGGER.error("Initialize failed..");
//		}
	}

	/**
	 * Create Channel when bean is initiate
	 * <pre>
	 *
	 * </pre>
	 * @throws Exception
	 */
	private void init() throws Exception {
		
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
        
        channelSftp = (ChannelSftp) channel;
        
        if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("SFTP Channel is created :: {}, {}, {}, {}", host, port, username, password);
        }
	}
    
    
	@Override
	public void afterPropertiesSet() throws Exception {
		if (Util.isNull(host) || Util.isNull(port) || Util.isNull(username) || Util.isNone(password)) {
			throw new RuntimeException("Parameter is not set. check parameters");
		}
	}
	
    /**
     * Disconnect sftp connection
     * <pre>
     *
     * </pre>
     */
    
    public void disconnection() {
    	channelSftp.quit();
    	LOGGER.info("SFTP Channel is cloesed");
    }
    
}
