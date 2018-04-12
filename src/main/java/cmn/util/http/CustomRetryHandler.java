import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRetryHandler implements HttpRequestRetryHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomRetryHandler.class);
	/** Retry Count **/
	private int retryCount = 5;
	
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}


	@Override
	public boolean retryRequest(IOException ioe, int numRetry, HttpContext context) {

        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        
        LOGGER.info("{} is retried {} times ", request.getRequestLine(), numRetry);

		/** Don't retry if retry count is over **/
        if (numRetry >= retryCount) {
            return false;
        }
        
        /** Don't retry if Timeout Unknow host or ssl error **/
        if (ioe instanceof ConnectTimeoutException || ioe instanceof SocketTimeoutException || ioe instanceof HttpHostConnectException) {
        	return true;
        }
        if (ioe instanceof InterruptedIOException || ioe instanceof UnknownHostException || ioe instanceof SSLException) {
            // Timeout
            return false;
        }

        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
        
        if (idempotent) {
            // Retry if the request is considered idempotent
            return true;
        }
        return false;
	}
}
