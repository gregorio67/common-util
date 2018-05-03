import java.io.File;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;



@Service("sftpService")
public class SftpServiceImpl extends BaseService implements SftpService {

	@Override
	public boolean upload(String intfGroupId, String remoteDir, String localDir, String remoteFileName,
			String localFileName) throws Exception {

		if (intfGroupId == null) {
			throw new BizException("SFTP Configuration bean is not set");
		}
		if (remoteFileName == null) {
			remoteFileName = localFileName; 
		}
		
		Map<String, Object> sftpMap = BeanUtil.getBean(intfGroupId);
		String host = sftpMap.get("host") != null ? String.valueOf(sftpMap.get("host")) : null;
		int port = sftpMap.get("port") != null ? Integer.parseInt(String.valueOf(sftpMap.get("port"))) : 0;
		String username = sftpMap.get("username") != null ? String.valueOf(sftpMap.get("username")) : null;
		String password = sftpMap.get("password") != null ? String.valueOf(sftpMap.get("password")) : null;
		
		if (host == null || port == 0 || username == null || password == null) {
			throw new BizException("The SFTP Configuration is not set correctly, check the configuration file");
		}
		SftpUtil sftpUtil = BeanUtil.getBean("sftpUtil");
		
		ChannelSftp channel = sftpUtil.conneect(host, port, username, password);
		return sftpUtil.upload(channel, remoteDir, localDir + File.separator + localFileName, remoteFileName);
		
	}

	@Override
	public boolean download(String intfGroupId, String remoteDir, String localDir, String remoteFileName,
			String localFileName) throws Exception {
		if (intfGroupId == null) {
			throw new BizException("SFTP Configuration bean is not set");
		}
		if (localFileName == null) {
			localFileName = remoteFileName;
		}
		
		Map<String, Object> sftpMap = BeanUtil.getBean(intfGroupId);
		String host = sftpMap.get("host") != null ? String.valueOf(sftpMap.get("host")) : null;
		int port = sftpMap.get("port") != null ? Integer.parseInt(String.valueOf(sftpMap.get("port"))) : 0;
		String username = sftpMap.get("username") != null ? String.valueOf(sftpMap.get("username")) : null;
		String password = sftpMap.get("password") != null ? String.valueOf(sftpMap.get("password")) : null;
		
		if (host == null || port == 0 || username == null || password == null) {
			throw new BizException("The SFTP Configuration is not set correctly, check the configuration file");
		}
		SftpUtil sftpUtil = BeanUtil.getBean("sftpUtil");
		
		ChannelSftp channel = sftpUtil.conneect(host, port, username, password);
		
		return sftpUtil.download(channel, remoteDir, remoteFileName, localDir, localFileName);
	}

	@Override
	public byte[] readFile(String intfGroupId, String remoteDir, String remoteFileName) throws Exception {
		if (intfGroupId == null) {
			throw new BizException("SFTP Configuration bean is not set");
		}
		
		Map<String, Object> sftpMap = BeanUtil.getBean(intfGroupId);
		String host = sftpMap.get("host") != null ? String.valueOf(sftpMap.get("host")) : null;
		int port = sftpMap.get("port") != null ? Integer.parseInt(String.valueOf(sftpMap.get("port"))) : 0;
		String username = sftpMap.get("username") != null ? String.valueOf(sftpMap.get("username")) : null;
		String password = sftpMap.get("password") != null ? String.valueOf(sftpMap.get("password")) : null;
		
		if (host == null || port == 0 || username == null || password == null) {
			throw new BizException("The SFTP Configuration is not set correctly, check the configuration file");
		}
		SftpUtil sftpUtil = BeanUtil.getBean("sftpUtil");
		
		ChannelSftp channel = sftpUtil.conneect(host, port, username, password);
		
		return sftpUtil.readFile(channel, remoteDir, remoteFileName);
	}


}
