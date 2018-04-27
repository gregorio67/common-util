public interface TcpClientService {
	
	public String sendMessage(String intfGroupId, final String requestPacket, final boolean rcvWait) throws Exception;

	public byte[] sendMessage(String intfGroupId, final byte[] requestPacket, final boolean rcvWait) throws Exception;

}
