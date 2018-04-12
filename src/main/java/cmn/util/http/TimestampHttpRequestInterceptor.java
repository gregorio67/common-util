import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;


public class TimestampHttpRequestInterceptor implements HttpRequestInterceptor {

	public static final String TIMESTAMP_ATTR_KEY = "START_TIME";

	/* (non-Javadoc)
	 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest, org.apache.http.protocol.HttpContext)
	 */
	public void process( HttpRequest httpRequest, HttpContext httpContext ) throws HttpException, IOException {
		// 현재 Timestamp(ms)를 Context에 저장
		httpContext.setAttribute( TIMESTAMP_ATTR_KEY, System.currentTimeMillis() );
		httpContext.setAttribute( "URL",  httpRequest.getRequestLine());
		httpRequest.addHeader(TIMESTAMP_ATTR_KEY, String.valueOf(System.currentTimeMillis()));;
	}
}
