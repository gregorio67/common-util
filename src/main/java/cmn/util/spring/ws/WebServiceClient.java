package cmn.util.spring.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
//import java.util.Properties;

//import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageException;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
//import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.support.MarshallingUtils;

import cmn.util.base.BaseConstants.InterfaceLogStatus;
import cmn.util.common.NullUtil;
import cmn.util.common.SoapMessagDump;
import cmn.util.exception.BizException;
import cmn.util.exception.UtilException;
import cmn.util.spring.BeanUtil;



public class WebServiceClient implements InitializingBean {


	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceClient.class);
	
	private static final String NON_PROCESS_TIME = "-1";

	public static final String PROCESS_TIME = "X-IF-Process-Time";


 	/**
	 * WebService Template
	 */
//	@Resource( name = "webServiceTemplate" )
	private WebServiceTemplate webServiceTemplate;


	public <V, T> T requestWsIf( final String intfGroupId, String interfaceId, final V requestVo, Class<T> responseType, final String command) {
		
		Assert.hasLength( intfGroupId );
		Assert.hasLength( interfaceId );

		// Response Entity
		ResponseEntity<T> responseEntity = null;

		Exception resultEx = null;

		try {
			/** Get Interface Specification  **/
			Map<String, String> interfaceMap = BeanUtil.getBean(intfGroupId);
			String interfaceSpec = interfaceMap != null ? interfaceMap.get( interfaceId ) : null;
			Assert.hasLength( interfaceSpec, "Invalid WebService Client Argument: interfaceSpec" );

			/** Check Interface host **/
			Assert.isTrue( interfaceMap.containsKey( "host" ), "Invalid WebService Client Argument: host" );

			String host = interfaceMap.get( "host" );
			Assert.hasLength( host, "Invalid WebService Client Argument: host" );

			/** Request Body Check  **/
			Assert.notNull( requestVo, "Invalid WebService Client Argument: requestVo" );

			/** Make URL & Method  **/
			String url = new StringBuilder( host ).append( interfaceSpec ).toString();

			/** Exchange request/response  **/
			responseEntity = webServiceTemplate.sendAndReceive( url, new WebServiceMessageCallback() {
				public void doWithMessage( WebServiceMessage message ) throws IOException, TransformerException {

					Marshaller marshaller = webServiceTemplate.getMarshaller();
					if ( marshaller == null ) {
						throw new IllegalStateException( "No marshaller registered. Check configuration of WebServiceTemplate." );
					}

					MarshallingUtils.marshal( marshaller, requestVo, message );
     			
	       			/** SOAP Message Dump **/
					try {
						SoapMessagDump.dumpSoapMessage((SaajSoapMessage)message);                	   
					}
					catch(Exception ex) {
                	   LOGGER.error("Send Soap Message Dump Error :: {}", ex.getMessage());
					}
				}

			}, new WebServiceMessageExtractor<ResponseEntity<T>>() {

				/* (non-Javadoc)
				 * @see org.springframework.ws.client.core.WebServiceMessageExtractor#extractData(org.springframework.ws.WebServiceMessage)
				 */
				@SuppressWarnings( "unchecked" )
				public ResponseEntity<T> extractData( WebServiceMessage message ) throws IOException, TransformerException {
					MimeHeaders header = ((SaajSoapMessage)message).getSaajMessage().getMimeHeaders();

					HttpHeaders responseHeader = new HttpHeaders();
					String[] timestampHeader = header.getHeader( PROCESS_TIME );
					if ( timestampHeader != null && timestampHeader.length > 0 ) {
						responseHeader.set( PROCESS_TIME, timestampHeader[0] );
					}

					Unmarshaller unmarshaller = webServiceTemplate.getUnmarshaller();
					if ( unmarshaller == null ) {
						throw new IllegalStateException( "No unmarshaller registered. Check configuration of WebServiceTemplate." );
					}

					/** Logging received message from target **/
                    SaajSoapMessage respMessage = (SaajSoapMessage) message;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    respMessage.writeTo(out);
      
                    String msg = new String(out.toByteArray());
                    
                    LOGGER.info("Response Message :: {}", msg);

	                T responseVo = (T)MarshallingUtils.unmarshal( unmarshaller, (SaajSoapMessage)message );

					return new ResponseEntity<T>( responseVo, responseHeader, HttpStatus.OK );
				}
			} );

			/** Return Response Body  **/
			return responseEntity.getBody();
		} catch ( Exception exception ) {
			resultEx = exception;
			throw new BizException(exception.getMessage() );
		} finally {
			logWsIfHistory( intfGroupId, interfaceId, responseEntity, resultEx );
		}
	}

	/**
	 * Logging REST I/F History
	 *
	 * @param intfGroupId Interface Group Id (context-interface-ws.xml 'util:map' bean의 id)
	 * @param interfaceId Interface Code (context-interface-ws.xml 'intfGroupCode' bean에 해당하는 map의 key)
	 * @param responseEntity Response Entity 객체 (200 OK Only, 정상응답이면 null을 부여할 수 없다)
	 * @param resultEx 연동 후 발생한 예외 클래스
	 * @param <T> Response Body Class Type
	 */
	private <T> void logWsIfHistory( String intfGroupId, String interfaceId, ResponseEntity<T> responseEntity, Exception resultEx ) {
		InterfaceLogStatus resultCode = null;
		Iterator <?> itr = responseEntity.getHeaders().entrySet().iterator();
		while(itr.hasNext()) {
			String key = (String)itr.next();
			String value = String.valueOf(responseEntity.getHeaders().get(key));
			LOGGER.debug("{}::{}", key, value);
		}
		
		String processTime = responseEntity != null && responseEntity.getHeaders().containsKey( PROCESS_TIME ) ? responseEntity.getHeaders().getFirst( PROCESS_TIME )
				: NON_PROCESS_TIME;
		String exMsg = resultEx != null ? resultEx.getMessage() : null;

		// 결과 Exception 유/뮤 및 예외 타입에 따른 로깅 정보 설정
		if ( resultEx == null ) {
			resultCode = InterfaceLogStatus.SUCCESS;
		} else if ( resultEx instanceof WebServiceClientException ) {
			if ( resultEx instanceof WebServiceTransportException ) {
				// Exception thrown when an HTTP 4xx is received.
				resultCode = InterfaceLogStatus.CLIENT_ERROR;
			} else if ( resultEx instanceof WebServiceIOException ) {
				// Exception thrown when an I/O error occurs.
				resultCode = InterfaceLogStatus.IO_ERROR;
			} else {
				resultCode = InterfaceLogStatus.CLIENT_ERROR;
			}
		} else if ( resultEx instanceof WebServiceMessageException ) {
			// Exception thrown when an HTTP 5xx is received.
			resultCode = InterfaceLogStatus.SERVER_ERROR;
		} else if ( resultEx instanceof WebServiceException ) {
			// Base class for exceptions thrown by WebServiceTemplate
			resultCode = InterfaceLogStatus.ETC_ERROR;
		} else {
			// Unknown Error
			resultCode = InterfaceLogStatus.UNKNOWN;
		}

		LOGGER.info( "WS,{},{},{},{},{},{},{}", intfGroupId, interfaceId, resultCode, processTime, "", StringUtils.defaultString( exMsg ), "" );
	}


	public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (NullUtil.isNull(webServiceTemplate)) {
			throw new UtilException("You shoud not set webServiceTemplate. Check your confinguration");
		}
		
	}
	
}
