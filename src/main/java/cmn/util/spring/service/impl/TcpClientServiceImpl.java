import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import stis.framework.exception.BizException;
import stis.framework.spring.BeanUtil;
import stis.framework.spring.service.TcpClientService;
import stis.framework.tcp.BaseBootstrap;
import stis.framework.tcp.StringClientHandler;

@Service("tcpClientService")
public class TcpClientServiceImpl implements TcpClientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TcpClientServiceImpl.class);
	@Override
	public String sendMessage(String intfGroupId, String requestPacket, boolean rcvWait) throws Exception {
		
		/** Check input parameter **/
		if (intfGroupId == null || requestPacket == null) {
			throw new BizException(" Server Information or send message is null");
		}
		
		String host = null;
		int port = 0;
		long startTime = System.currentTimeMillis();
		
		/** Socket Channel **/
		Channel channel = null;

		final CountDownLatch latch = new CountDownLatch(1);

		/** String Handler **/
		StringClientHandler clientHandler = null;

		/** Netty Event Loop Group **/
		final EventLoopGroup group = new NioEventLoopGroup();

		try {

			/**  Get Server Information from context-tcpclient.xml file **/
			Map<String, Object> interfaceMap = BeanUtil.getBean(intfGroupId);

			/** Create Client Handler **/
			clientHandler = new StringClientHandler(requestPacket, rcvWait, latch);

			/** Create Client Bootstrap **/
			Bootstrap bootstrap = BaseBootstrap.stringBootstrap(group, clientHandler);

			/** Host IP **/
			host = interfaceMap.get("host") != null ? String.valueOf(interfaceMap.get("host")) : null; 
			if (host == null) {
				throw new BizException("Server IP address is not set, Check configuratio file");
			}
			/** Port **/
			port = Integer.parseInt((String) (interfaceMap.get("port") != null ? String.valueOf(interfaceMap.get("port")) : 0));
			if (port == 0) {
				throw new BizException("Server Port is not set, Check configuratio file");				
			}
			
			int maxRetryCnt = Integer.parseInt((String) (interfaceMap.get("maxRetryCnt") != null ? String.valueOf(interfaceMap.get("maxRetryCnt")) : 1));
			
			int rcvTimeout = Integer.parseInt((String) (interfaceMap.get("rcvTimeout") != null ? String.valueOf(interfaceMap.get("rcvTimeout")) : 3000));

			/** If connect is failed, retry **/
			for (int retryCnt = 0; retryCnt < maxRetryCnt; retryCnt++) {
				try {
					channel = bootstrap.connect(host, port).sync().channel();
					break;
				} catch (Exception connectionError) {
					if (retryCnt == maxRetryCnt) {
						// if.tcp.client.maxretry
						throw connectionError;
					} else {
						LOGGER.error("[Client][ERROR] {}:{}Connection error occured. retry again {}/{}", host, port,
								retryCnt + 2);
					}
				}
			}

			if (!latch.await(rcvTimeout, TimeUnit.MILLISECONDS)) {
				channel.disconnect().sync();
				throw new TimeoutException("[Client][MSG RCV TIMEOUT] TCP 수신 대기 시간을 넘었습니다");
			}

		} catch (Exception ex) {
			throw new BizException("");
		} finally {
			if (channel != null) {
				try {
					channel.closeFuture().sync();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Socket Channel is closed");
					}
				} catch (InterruptedException interruptedEx) {
					LOGGER.error("Interrupted: {}", interruptedEx.getMessage());
				}
			}

			group.shutdownGracefully();

		}
		LOGGER.info("Service is successfully done :: {}, {}", host, port, System.currentTimeMillis() - startTime);	
		return clientHandler != null ? clientHandler.getResponse() : null;
	}

	@Override
	public byte[] sendMessage(String intfGroupId, byte[] requestPacket, boolean rcvWait) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}


/**
context-tcpcient.xml

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:util="http://www.springframework.org/schema/util"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
						http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
						http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">


	<!-- Drug Safety Control Restful service List -->
	<util:map id="sampleServer">
		<entry key="host" value="172.6.14.212" />
		<entry key="port" value="9090" />
		<entry key="maxRetryCnt" value="5" />
		<entry key="rcvTimeout"	value="3000" />	 
	</util:map>
</beans>

	@RequestMapping(value = "/sample/tcpclient.do")
	public ModelAndView tcpClient(NexacroMapDTO dto, String strMessage) throws Exception {
		ModelAndView modelAndView = NexacroUtil.getModelAndView(dto);

		String sendMessage = "TEST11123234234";
		String response = tcpClientService.sendMessage("sampleServer", sendMessage, true);
		LOGGER.info("response :: {}", response);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("response", response);
		modelAndView.setViewName("jsonView");
		modelAndView.addObject("tcp result", map);
		return modelAndView;
	}

**/
