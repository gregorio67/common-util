import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class HttpRequestConfig implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestConfig.class);
	
	private int connectionRequestTimeout = 5000;
	
	private int connectTimeout = 5000;
	
	private int socketTimeout = 5000;
	
	private int maxRedirects = 5;
	

	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setMaxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}

	public HttpRequestConfig() throws Exception {
	}
	
	public RequestConfig init() throws Exception {
        RequestConfig reqConfig = RequestConfig.custom()
        		.setConnectionRequestTimeout(connectionRequestTimeout)
        		.setConnectTimeout(connectTimeout)
        		.setSocketTimeout(socketTimeout)
        		.setMaxRedirects(maxRedirects)
        		.build();
        
        return reqConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("connectionRequestTimeout :: {}", connectionRequestTimeout);
		LOGGER.info("connectTimeout :: {}", connectTimeout);
		LOGGER.info("socketTimeout :: {}", socketTimeout);
		LOGGER.info("maxRedirects :: {}", maxRedirects);		
		
	}
}
