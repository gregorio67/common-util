import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class StringClientHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringClientHandler.class);

	private final String request;

	private final boolean rcvWait;

	private final CountDownLatch latch;

	private String response;	
	
	public StringClientHandler(String request, boolean rcvWait, CountDownLatch latch ) {
		this.request = request;
		this.rcvWait = rcvWait;
		this.latch = latch;
	}

	
	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		super.channelActive( ctx );

		// TCP 채널이 활성화 되면 요청 메시지를 전송한 후 플러쉬합니다.
		LOGGER.info( "[Client][MSG SEND] {}", this.request );
		ctx.writeAndFlush( request );

		// TCP 응답을 받을 필요 없다면 바로 연결을 종료
		if ( !rcvWait ) {
			ctx.close();
		}
	}
	
	@Override
	protected void channelRead0( ChannelHandlerContext ctx, String msg ) throws Exception {
		if ( rcvWait ) {
			/*
			 * 1. TCP 응답 메시지를 객체에 저장한다
			 * 2. Latch 감소
			 * 3. 연결 종료
			 */
			response = msg;
			latch.countDown();

			LOGGER.info( "[Client][MSG RCV] {}", msg );
			ctx.close();
		}
	}

	@Override
	public void channelReadComplete( ChannelHandlerContext ctx ) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
		LOGGER.error( "[Client][ERROR] ", cause );
		ctx.close();
	}
	
	public String getResponse() {
		return response;
	}
}
