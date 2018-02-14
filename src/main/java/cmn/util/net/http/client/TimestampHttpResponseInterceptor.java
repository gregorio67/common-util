package cmn.util.net.http.client;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cmn.util.common.NullUtil;
import cmn.util.spring.HttpUtil;


public class TimestampHttpResponseInterceptor implements HttpResponseInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimestampHttpResponseInterceptor.class);

	/* (non-Javadoc)
	 * @see org.apache.http.HttpResponseInterceptor#process(org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	@Override
	public void process( HttpResponse paramHttpResponse, HttpContext paramHttpContext ) throws HttpException, IOException {
		
		if ( paramHttpContext.getAttribute( TimestampHttpRequestInterceptor.TIMESTAMP_ATTR_KEY ) != null ) {
			String startTime = null;
			long elapsedTime = 0L;
			
			if (!NullUtil.isNull(paramHttpContext.getAttribute( TimestampHttpRequestInterceptor.TIMESTAMP_ATTR_KEY))) {
				startTime = String.valueOf(paramHttpContext.getAttribute( TimestampHttpRequestInterceptor.TIMESTAMP_ATTR_KEY));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("start Time :: {}", startTime);
				}
				elapsedTime = System.currentTimeMillis() - Long.parseLong(startTime);
				paramHttpResponse.addHeader( "PROCESS_TIME", String.valueOf( elapsedTime ) );
			}
			
			LOGGER.info("HTTP Elapsed Time :: {}", elapsedTime);
		}
	}
}
