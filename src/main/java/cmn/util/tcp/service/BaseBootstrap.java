import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import stis.framework.spring.PropertiesUtil;

public class BaseBootstrap {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseBootstrap.class);
	
	private static final int SOCKET_READ_TIMEOUT = 3000;
	
	public BaseBootstrap() {
		
	}
	


	/** 
	 * 
	 *<pre>
	 * 1.Description: Generate netty bootstrap for string
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param group netty event loop group
	 * @param clientHandler String client Handler
	 * @return
	 */
	public static Bootstrap stringBootstrap(final EventLoopGroup group, final StringClientHandler clientHandler) {
		Bootstrap bootstrap = new Bootstrap();
		final int soTimeout = PropertiesUtil.getInt("tcp.read.timeout") > 0 ? PropertiesUtil.getInt("tcp.read.timeout") : SOCKET_READ_TIMEOUT;			

		bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				ChannelPipeline pipeline = sc.pipeline();
				pipeline.addLast( new StringDecoder() );
				pipeline.addLast( new StringEncoder() );
				pipeline.addLast( new IdleStateHandler(20, 10, 0));
				pipeline.addLast( new ReadTimeoutHandler(soTimeout));					
				pipeline.addLast(clientHandler);
			}
		})
				// TCP Channel Option
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, PropertiesUtil.getInt("tcp.connection.timeout"));

		return bootstrap;
	}

	/** 
	 * 
	 *<pre>
	 * 1.Description: Generate netty bootstrap for byte array
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param group netty event loop group
	 * @param clientHandler String client Handler
	 * @return
	 */

	public static Bootstrap byteBootstrap(final EventLoopGroup group, final StringClientHandler clientHandler) {
		Bootstrap bootstrap = new Bootstrap();
		
		final int soTimeout = PropertiesUtil.getInt("tcp.read.timeout") > 0 ? PropertiesUtil.getInt("tcp.read.timeout") : SOCKET_READ_TIMEOUT;			

		bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				ChannelPipeline pipeline = sc.pipeline();
				pipeline.addLast( new ByteArrayDecoder() );
				pipeline.addLast( new ByteArrayEncoder() );
				pipeline.addLast( new IdleStateHandler(20, 10, 0));
				pipeline.addLast(new ReadTimeoutHandler(soTimeout));					
				pipeline.addLast(clientHandler);
				
			}
		})
				// TCP Channel Option
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, PropertiesUtil.getInt("tcp.connection.timeout"));

		return bootstrap;
	}
	

}
