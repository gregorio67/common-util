import java.io.Serializable;
import java.util.UUID;

public class TcpMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 416358989024166030L;

	/** Message ID, it is generated automatically **/
	private String msgId;
	
	/** Send Message **/
	private String sendMessage;
	
	/** Receive Message **/
	private String recvMessage;
	
	public TcpMessage() {
		msgId = UUID.randomUUID().toString().replaceAll("-", "");
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(String sendMessage) {
		this.sendMessage = sendMessage;
	}

	public String getRecvMessage() {
		return recvMessage;
	}

	public void setRecvMessage(String recvMessage) {
		this.recvMessage = recvMessage;
	}

}
