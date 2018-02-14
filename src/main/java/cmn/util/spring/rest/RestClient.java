package cmn.util.spring.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Splitter;

import cmn.util.exception.BizException;
import cmn.util.spring.ApplicationContextProvider;
import cmn.util.spring.BeanUtil;

public class RestClient implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

	@Resource(name="restHttpClient")
	private CloseableHttpClient restHttpClient;

	private MappingJackson2HttpMessageConverter messageConverter;
	
	
	private int connectTimeout;
	private int readTimeout;

	private static final Splitter IF_SPEC_SPLITTER = Splitter.on( '|' );

	private static RestTemplate restTemplate;

	/**
	 * 
	 *<pre>
	 * Create RestTemplate when context is loaded
	 *</pre>
	 * @throws Exception
	 */
	public void init() throws Exception {

		LOGGER.info("RestClient building satrt");
		
		HttpComponentsClientHttpRequestFactory requestFactory =  new HttpComponentsClientHttpRequestFactory();

		requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        
        restTemplate = new RestTemplate(requestFactory);
        
        ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setHttpClient(restHttpClient);

        restTemplate.setMessageConverters(getMessageConverters());

        LOGGER.info("RestClient building end");
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
	public <V, T> T exchange(String interfaceGroupId, String interfaceId, Map<String, String> extraHeaders, MediaType contentType, Map<String, Object> parameter, V requestBody, Class<T> responseType) throws Exception {

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
		String uri = IF_SPEC_SPLITTER.splitToList(beanMap.get(interfaceId)).get(0);
		String url = host + uri;
		HttpMethod method = HttpMethod.valueOf(IF_SPEC_SPLITTER.splitToList(beanMap.get(interfaceId)).get(1));

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (parameter != null && !parameter.isEmpty()) {
			parameters.putAll(parameter);
		}

		responseEntity = restTemplate.exchange(url,  method, entity, responseType, parameters);

		T responseBody = responseEntity.getBody();

		return responseBody;
	}

	/**
	 * RestTemplate exchange
	 * @param interfaceGroupId String Interface Group
	 * @param interfaceId String Interface ID
	 * @param extraHeaders Map<String, String> HTTP Request Header
	 * @param contentType MediaType 
	 * @param parameter Map<String, String>  Request Parameter
	 * @param requestBody Map<String, Object> Request Payload
	 * @return
	 */
	public Map<String, Object> exchange(String interfaceGroupId, String interfaceId, Map<String, String> extraHeaders, MediaType contentType, Map<String, String> parameter, Map<String, Object> requestBody) throws Exception {

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
		Map<String, String> beanMap = (Map<String, String>) BeanUtil.getBean(interfaceGroupId);
		
		String host = beanMap.get("host");
		
		String uri = IF_SPEC_SPLITTER.splitToList(beanMap.get(interfaceId)).get(0);
		String url = host + uri;
		
		String method = IF_SPEC_SPLITTER.splitToList(beanMap.get(interfaceId)).get(1);
		HttpMethod httpMethod = HttpMethod.valueOf(method);

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (parameter != null && !parameter.isEmpty()) {
			parameters.putAll(parameter);
		}

		/** Setting for Response with Map **/
		ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<HashMap<String, Object>>() {};

		try {
			responseEntity = restTemplate.exchange(url,  httpMethod, entity, responseType, parameters);			
		}
		catch(Exception restex) {
			LOGGER.error("RestClient :: {} call error", url);
			String[] msgParam = new String[1];
			msgParam[0] = url;
			throw new BizException("err.inf.service", restex.getMessage(), msgParam );
		}

		/** Check response Code **/
//		if (!responseEntity.getStatusCode().is2xxSuccessful()) {
//			 != HttpStatus.SC_OK
//		}
		
		Map<String, Object> responseBody = responseEntity != null ? responseEntity.getBody() : null;
		
		/** If Response Body is null throw exception **/
//		if (NullUtil.isNull(responseBody)) {
//			LOGGER.error("ResponseBody is null :: {}", url);
//			String[] msgParam = new String[1];
//			msgParam[0] = url;
//			throw new BizException("err.inf.service", "There is no result", msgParam );
//		}

		return responseBody;

	}

	/**
	 * Message Converter
	 * @return
	 */
    private List<HttpMessageConverter<?>> getMessageConverters() {
    	 List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
         
    	 /**Add the Jackson Message converter **/
//    	 MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    	 
		/** Note: here we are making this converter to process any kind of response, not only application/*json, which is the default behaviour **/
//    	 messageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL));

		messageConverters.add(messageConverter);

		return messageConverters;
	}

    
    
	public void setMessageConverter(MappingJackson2HttpMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public void setConnectTimeout(int connectTimeout) {
		if (connectTimeout == 0) {
			this.connectTimeout = 5000;
		}
		else {
			this.connectTimeout = connectTimeout;
		}
		LOGGER.info("Connection Timeout is {}", this.connectTimeout);

	}

	public void setReadTimeout(int readTimeout) {
		if (readTimeout == 0) {
			this.readTimeout = 5000;
		}
		else {
			this.readTimeout = readTimeout;
		}
		LOGGER.info("Read Timeout is {}", this.readTimeout);
	}

	
	public void afterPropertiesSet() throws Exception {
	}
}
