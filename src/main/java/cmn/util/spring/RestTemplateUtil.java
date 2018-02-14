package cmn.util.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
public class RestTemplateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateUtil.class);

	private static RestTemplate restTemplate;

	private static String[] sslProtocols = {"TLSv1.1", "TLSV1.2"};

	private static final String SEPERATOR = "\\|";
	private static CloseableHttpClient httpClient = null;
	private static int maxPool = 100;
	private static int connectTimeout = 2000;
	private static int readTimeout = 2000;
	private static String keyStoreLoc = "C:/";
	private static String password = "password";
	private static String trustStoreLoc = "C:/";



	private void getHttpClient(boolean isCertValidate) throws Exception {
		if (httpClient == null) {
			synchronized(this) {
				HttpClientBuilder clientBuilder = HttpClientBuilder.create();

				/** This is not validate certificate **/
		        SSLContext sslContext = null;
		        SSLConnectionSocketFactory sslSocketFactory = null;

		        if (isCertValidate) {
		        	sslContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
		            sslSocketFactory = new SSLConnectionSocketFactory(
		            		sslContext,
		            		sslProtocols,
		                    null,
		                    NoopHostnameVerifier.INSTANCE);
		        }
		        else {
		        	 sslContext = SSLContextBuilder
		                     .create()
		                     .loadKeyMaterial(ResourceUtils.getFile(keyStoreLoc), password.toCharArray(), password.toCharArray())
		                     .loadTrustMaterial(ResourceUtils.getFile(trustStoreLoc), password.toCharArray())
		                     .build();

		            // Allow TLSv1 protocol only
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

		        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
		        connMgr.setMaxTotal(maxPool);
		        clientBuilder.setConnectionManager(connMgr);
		        httpClient = clientBuilder.build();
		        LOGGER.info("PoolingHttpClient creation is sucessfully ended.");
			}
		}
	}

	public RestTemplate getRestTemplate(boolean isCertValidate) throws Exception {

        HttpComponentsClientHttpRequestFactory requestFactory =  new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        restTemplate = new RestTemplate(requestFactory);
        if (httpClient == null) {
        	getHttpClient(isCertValidate);
        }
        ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setHttpClient(httpClient);
        restTemplate.setMessageConverters(getMessageConverters());
        return restTemplate;
	}

	/**
	 *
	 * @param interfaceGroupId
	 * @param interfaceId
	 * @param extraHeaders
	 * @param contentType
	 * @param parameter
	 * @param requestBody
	 * @param responseType
	 * @return
	 * @throws Exception
	 */
	public <V, T> T exchange(String interfaceGroupId, String interfaceId, Map<String, String> extraHeaders, MediaType contentType, Map<String, String> parameter, V requestBody, Class<T> responseType) throws Exception {

		ResponseEntity<T> responseEntity = null;

		HttpHeaders headers = new HttpHeaders();
		if (extraHeaders != null) {
			headers.setAll(extraHeaders);
		}

		if (requestBody != null) {
			headers.setContentType(contentType == null ? MediaType.APPLICATION_JSON : contentType);
		}
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<V> entity = new HttpEntity<V>(requestBody, headers);

		@SuppressWarnings("unchecked")
		Map<String, String> beanMap = (Map<String, String>) ApplicationContextProvider.getApplicationContext().getBean(interfaceGroupId);

		String host = beanMap.get("host");
		String uri = beanMap.get(interfaceId).split(SEPERATOR)[0];
		String url = host + uri;
		HttpMethod method = HttpMethod.valueOf(beanMap.get(interfaceId).split(SEPERATOR)[1]);

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (parameter != null && !parameter.isEmpty()) {
			parameters.putAll(parameter);
		}

		responseEntity = restTemplate.exchange(url,  method, entity, responseType, parameters);

		T responseBody = responseEntity.getBody();

		LOGGER.info("Response Body :: {}", responseBody);
		return responseBody;
	}

	/**
	 * RestTemplate exchange
	 * @param interfaceGroupId
	 * @param interfaceId
	 * @param extraHeaders
	 * @param contentType
	 * @param parameter
	 * @param requestBody
	 * @return
	 */
	public Map<String, Object> exchange(String interfaceGroupId, String interfaceId, Map<String, String> extraHeaders, MediaType contentType, Map<String, String> parameter, Map<String, Object> requestBody) {

		ResponseEntity<HashMap<String, Object>> responseEntity = null;

		HttpHeaders headers = new HttpHeaders();
		if (extraHeaders != null) {
			headers.setAll(extraHeaders);
		}
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		if (requestBody != null) {
			headers.setContentType(contentType == null ? MediaType.APPLICATION_JSON : contentType);
		}
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(requestBody, headers);

		@SuppressWarnings("unchecked")
		Map<String, String> beanMap = (Map<String, String>) ApplicationContextProvider.getApplicationContext().getBean(interfaceGroupId);

		String host = beanMap.get("host");
		String tempUri = beanMap.get(interfaceId);
		String uri = tempUri.split(SEPERATOR)[0];
		String url = host + uri;
		HttpMethod method = HttpMethod.valueOf(tempUri.split(SEPERATOR)[1]);

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (parameter != null && !parameter.isEmpty()) {
			parameters.putAll(parameter);
		}

		ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<HashMap<String, Object>>() {};

		responseEntity = restTemplate.exchange(url,  method, entity, responseType, parameters);

		Map<String, Object> responseBody = responseEntity.getBody();
		
		LOGGER.info("Response Body :: {}", responseBody.toString());
		return responseBody;

	}

	/**
	 * Message Converter
	 * @return
	 */
    private static List<HttpMessageConverter<?>> getMessageConverters() {
    	 List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
         //Add the Jackson Message converter
    	 MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		// Note: here we are making this converter to process any kind of response,
		// not only application/*json, which is the default behaviour
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL));
		messageConverters.add(converter);
		return messageConverters;
	}
}
