import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpClientProtocol {

    private Bootstrap bootstrap;
    
    private Map<String, Channel> channelMap = new HashMap<>();
    
    private ChannelDuplexHandler clientIdelHandler;
    
     
    public TcpClientProtocol() {
    	open();
    }

    public void open() {
        EventLoopGroup eventLoop = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoop);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3 * 1000);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
            	ChannelPipeline pipeline = ch.pipeline();
               
            	pipeline.addLast("logging",new LoggingHandler(LogLevel.INFO));
            	pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader()))); // in 1
            	if (clientIdelHandler != null) {
                	pipeline.addLast("handler", new ClientIdleHandler()); // in 2
            		
            	}
            	pipeline.addLast("encoder", new ObjectEncoder());// out 3
            	pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 1, 0));
            	pipeline.addLast(new ClientReadHandler());

            }
        });
    }


    public void connect(String host, int port) throws Exception {
        ChannelFuture future = bootstrap.connect(host, port).sync();
        channelMap.put(host+ ":" + port, future.channel());
    }


    public void close() {
    	Iterator<String> channelItr =  channelMap.keySet().iterator();
    	while(channelItr.hasNext()) {
    		String key = channelItr.next();
    		Channel channel = channelMap.get(key);
    		if (channel != null && channel.isActive()) {
    			channel.close();
    		}
    	}
    }


    public ResponseFuture send(String host, int port, TcpMessage data) throws Exception {
    	String channelKey = host + ":" + port;
        if (! channelMap.containsKey(channelKey)) {
            connect(host, port);
        }

        Channel channel = channelMap.get(channelKey);
        if (!channel.isActive() || channel == null) {
            connect(host, port);
            channel = channelMap.get(channelKey);
        }
        
//        ByteBuf sendBuf = Unpooled.buffer();
//        sendBuf.writeBytes(data.getSendMessage().getBytes());
        
        channel.writeAndFlush(data);
        return FutureHolder.createFuture(data);
    }
}
