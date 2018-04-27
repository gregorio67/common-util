/**
 * @Project :  스마트톨링정보시스템 구축
 * @Class : TcpMain.java
 * @Description : 
 *
 * @Author : LGCNS
 * @Since : 2017. 4. 20.
 *
 * @Copyright (c) 2018 EX All rights reserved.
 *-------------------------------------------------------------
 *              Modification Information
 *-------------------------------------------------------------
 * 날짜            수정자             변경사유 
 *-------------------------------------------------------------
 * 2018. 4. 27.        LGCNS             최초작성
 *-------------------------------------------------------------
 */

package stis.framework.tcp.client;

public class TcpMain {
	
	public static void main(String[] args) throws Exception {
		TcpClientProtocol clientProtocol = new TcpClientProtocol();
//		 clientProtocol.open();
//		 clientProtocol.connect("172.6.14.212", 9090);
		 TcpMessage msg = new TcpMessage();
		 msg.setSendMessage("TEST1224242");
		 ResponseFuture future = clientProtocol.send("172.6.14.212", 9090,  msg);
		 msg = future.get();
		 System.out.println(msg.getSendMessage());

		 msg.setSendMessage("TEST123333333");
		 future = clientProtocol.send("172.6.14.212", 9090,  msg);
		 msg = future.get();
		 System.out.println(msg.getSendMessage());

		 
	}

}
