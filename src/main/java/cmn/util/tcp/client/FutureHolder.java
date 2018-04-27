import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FutureHolder {

	private static Logger LOGGER = LoggerFactory.getLogger(FutureHolder.class);
	
    private static Map<String, ResponseFuture> holder = new ConcurrentHashMap<>();

    public static void receive(TcpMessage result) {
    	
    	String msgId = result.getMsgId();
    	LOGGER.info("Message Id and Send Message:: {}, {}", msgId, result.getSendMessage());
    	
    	ResponseFuture future = holder.get(msgId);
    	if (future != null) {
    		future.receive(result);
    	}
    	else {
    		LOGGER.error("There is no match messag id :: {}", msgId);
    	}
        
    }

    public static ResponseFuture createFuture(TcpMessage data) {
    	ResponseFuture future = new ResponseFuture();
        holder.put(data.getMsgId(), future);
        return future;
    }
}
