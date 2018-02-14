package cmn.util.net.http.client;

import org.apache.http.impl.client.HttpClientBuilder;

public class CustomHttpClientBuilder extends HttpClientBuilder {

	/**
	 * <PRE>
	 * HttpClientBuilder.create() 의 커스텀
	 * CustomHttpClientBuilder 클래스를 Builder로 유도하기 위한 함수
	 *
	 * 추가 Interceptor: HTTP 요청/응답 처리 시간 측정 Interceptor
	 * TimestampHttpRequestInterceptor,
	 * TimestampHttpResponseInterceptor
	 * (요청 이후 HTTP 응답 헤더의 'X-IF-Process-Time' 값에 ms 단위 Timestamp 저장)
	 * </PRE>
	 *
	 * @return Builder (this)
	 */
	public static CustomHttpClientBuilder create() {
		return (CustomHttpClientBuilder)new CustomHttpClientBuilder().addInterceptorLast( new TimestampHttpRequestInterceptor() ).addInterceptorFirst( new TimestampHttpResponseInterceptor() );
	}
}
