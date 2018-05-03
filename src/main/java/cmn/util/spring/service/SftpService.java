
public interface SftpService {
	
	public boolean upload(String intfGroupId, String remoteDir, String localDir, String remoteFileName, String localFileName) throws Exception;

	public boolean download(String intfGroupId, String remoteDir, String localDir, String remoteFileName, String localFileName) throws Exception;
	
	public byte[] readFile(String intfGroupId, String remoteDir, String remoteFileName) throws Exception;

}
