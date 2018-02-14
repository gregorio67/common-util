package cmn.util.net.http.client;

import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import cmn.util.exception.UtilException;


public class CostomClosableHttpClient implements FactoryBean<CloseableHttpClient>, InitializingBean{

	private static final Logger LOGGER = LoggerFactory.getLogger(CostomClosableHttpClient.class);

	private static final String[] _sslProtocols = {"TLSv1.1", "TLSv1.2"};

	private String[] sslProtocols;
	private boolean ignoreCertValidate;
	private String keyStoreLocation;
	private String trustStoreLocation;
	private String password;
	private int maxPool;
	private int connectTimeout;
	private int readTimeout;

	@Override
	public CloseableHttpClient getObject() throws Exception {

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		
		/** Add Interceptor **/
//		clientBuilder.addInterceptorLast( new TimestampHttpRequestInterceptor()).addInterceptorFirst( new TimestampHttpResponseInterceptor()).
		/** If not set, occurs HTTP I/O error **/
		clientBuilder.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Add Interceptor end");
		}

		SSLContext sslContext = null;
		SSLConnectionSocketFactory sslSocketFactory = null;

		/** Not validate certifiaction **/
		if (ignoreCertValidate) {
			/** This is not validate certificate **/
			sslContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
			sslSocketFactory = new SSLConnectionSocketFactory(
	        		sslContext,
	        		sslProtocols,
	                null,
	                NoopHostnameVerifier.INSTANCE);
		}
		/** Validate Certifiaction **/
		else {
	        sslContext = SSLContextBuilder
	                .create()
	                .loadKeyMaterial(ResourceUtils.getFile(keyStoreLocation), password.toCharArray(), password.toCharArray())
	                .loadTrustMaterial(ResourceUtils.getFile(trustStoreLocation), password.toCharArray())
	                .build();

	        sslSocketFactory = new SSLConnectionSocketFactory(
	        		sslContext,
	        		sslProtocols,
	                null,
	                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		}


        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", sslSocketFactory)
	            .build();

        HttpComponentsClientHttpRequestFactory requestFactory =  new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        connMgr.setMaxTotal(maxPool);
        clientBuilder.setConnectionManager(connMgr);
        CloseableHttpClient httpClient = clientBuilder.build();

        return httpClient;
	}

	@Override
	public Class<?> getObjectType() {
		return CloseableHttpClient.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * 
	 *<pre>
	 * Create HTTP Client without certification
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	public CloseableHttpClient noCertValidate() throws Exception {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		/** Add Interceptor **/

		clientBuilder.addInterceptorLast( new TimestampHttpRequestInterceptor()).addInterceptorFirst( new TimestampHttpResponseInterceptor() );
//		clientBuilder.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor());


		/** This is not validate certificate **/
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
        		sslContext,
        		sslProtocols,
                null,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", sslSocketFactory)
	            .build();

        HttpComponentsClientHttpRequestFactory requestFactory =  new HttpComponentsClientHttpRequestFactory();
//      requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        connMgr.setMaxTotal(maxPool);
//        connMgr.setDefaultConnectionConfig(requestFactory);
        clientBuilder.setConnectionManager(connMgr);
        CloseableHttpClient httpClient = clientBuilder.build();

        return httpClient;
	}

	/**
	 * 
	 *<pre>
	 * Create HTTP client with certification 
	 *</pre>
	 * @return CloseableHttpClient
	 * @throws Exception
	 */
	public CloseableHttpClient certValidate() throws Exception {
		

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();

		clientBuilder.addInterceptorLast( new TimestampHttpRequestInterceptor()).addInterceptorFirst( new TimestampHttpResponseInterceptor() );

        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(ResourceUtils.getFile(keyStoreLocation), password.toCharArray(), password.toCharArray())
                .loadTrustMaterial(ResourceUtils.getFile(trustStoreLocation), password.toCharArray())
                .build();

		/** This is not validate certificate **/
//		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//		keyStore.load(new FileInputStream(new File(keyStoreLoc)), password.toCharArray());
//      SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy())
//                .loadKeyMaterial(keyStore, "password".toCharArray()).build();

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
        		sslContext,
        		sslProtocols,
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", sslSocketFactory)
	            .build();

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        connMgr.setMaxTotal(maxPool);

        clientBuilder.setConnectionManager(connMgr);
        CloseableHttpClient httpClient = clientBuilder.build();
        return httpClient;

	}

	public void setSslProtocols(String[] sslProtocols) {
		this.sslProtocols = sslProtocols;
		if (sslProtocols == null) {
			this.sslProtocols = _sslProtocols;
		}
	}

	public void setIgnoreCertValidate(boolean ignoreCertValidate) {
		this.ignoreCertValidate = ignoreCertValidate;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public void setMaxPool(int maxPool) {
		if (maxPool == 0) {
			this.maxPool = 100;
		}
		else {
			this.maxPool = maxPool;
		}
	}

//	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
//		if (connectionRequestTimeout == 0) {
//			this.connectionRequestTimeout= 5000;
//		}
//		else {
//			this.connectionRequestTimeout = connectionRequestTimeout;
//		}
//	}

	public void setKeyStoreLocation(String keyStoreLocation) {
		this.keyStoreLocation = keyStoreLocation;
	}

	public void setTrustStoreLocation(String trustStoreLocation) {
		this.trustStoreLocation = trustStoreLocation;
	}

	public void setConnectTimeout(int connectTimeout) {
		if (connectTimeout == 0) {
			this.connectTimeout = 5000;
		}
		else {
			this.connectTimeout = connectTimeout;
		}
	}

	public void setReadTimeout(int readTimeout) {
		if (readTimeout == 0) {
			this.readTimeout = 5000;
		}
		else {
			this.readTimeout = readTimeout;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!this.ignoreCertValidate) {
			if (this.keyStoreLocation == null || this.trustStoreLocation == null || this.password == null) {
				throw new UtilException("Check KeyStore, TrustStore and Password");
			}
		}

	}
}
