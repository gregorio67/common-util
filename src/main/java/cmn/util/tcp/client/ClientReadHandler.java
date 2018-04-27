import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientReadHandler extends SimpleChannelInboundHandler<TcpMessage> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientReadHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpMessage msg) throws Exception {
    	LOGGER.info("Message Received");
        FutureHolder.receive(msg);
    }
}
