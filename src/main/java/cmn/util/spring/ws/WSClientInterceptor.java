package cmn.util.spring.ws;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.AbstractSoapMessage;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import com.google.common.base.Splitter;

import cmn.util.base.BaseConstants;
import cmn.util.common.NullUtil;
import cmn.util.common.SoapMessagDump;
import cmn.util.exception.BizException;
import cmn.util.spring.BeanUtil;
import cmn.util.spring.HttpUtil;


public class WSClientInterceptor implements ClientInterceptor, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(WSClientInterceptor.class);
	
    private static final Splitter IF_SPEC_SPLITTER = Splitter.on( '|' );
		
	@SuppressWarnings("unchecked")
	public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
	
		/** Get Http Request Data **/
		String infId = null;
		String serviceName = null;
		String prefix = null;
		
		Map<String, Object> infMap = null;
		
		try {
			infId = HttpUtil.getRequestData("infId");
			serviceName = HttpUtil.getRequestData("serviceName");
			infMap = BeanUtil.getBean(infId);
		}
		catch(Exception ex) {
			LOGGER.error("HttpServiceRequest data get error");
			throw new BizException(BaseConstants.DEFAULT_EXCEPTION_MESSAGE);
		}
				
		/**
		 * Need to check In bound and out bound 
		 */
		WebServiceMessage requestMessage = messageContext.getRequest();
		AbstractSoapMessage abstractSaajMessage = (AbstractSoapMessage) requestMessage;
		SaajSoapMessage saajSoapMessage = (SaajSoapMessage) abstractSaajMessage;
		SoapVersion	soapVersion = saajSoapMessage.getVersion();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SOAP Version :: {}", soapVersion);
		}
		
		SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
		try {
			SOAPPart soapPart = soapMessage.getSOAPPart();
			SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
			SOAPHeader soapHeader = soapEnvelope.getHeader();
			SOAPBody soapBody = soapEnvelope.getBody();

			/** Add name space for soap Envelope **/

			prefix = (String) (infMap.get("messagePrefix") != null ? infMap.get("messagePrefix") : "");

			String removeNameSpace = (String) (infMap.get("removeEnvelopeNamespace") != null ? infMap.get("removeEnvelopeNamespace") : "");

			if ("true".equalsIgnoreCase(removeNameSpace)) {
				soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());				
			}
			
			/** Add name space for soap Envelope **/
			String addEnvelopeNamespace = (String) (infMap.get("addEnvelopeNamespace") != null ? infMap.get("addEnvelopeNamespace") : "");
			if (!NullUtil.isNull(addEnvelopeNamespace)) {
				
				String addName = IF_SPEC_SPLITTER.splitToList(addEnvelopeNamespace).get(0);
				String addUrl = IF_SPEC_SPLITTER.splitToList(addEnvelopeNamespace).get(1);
				if (!NullUtil.isNull(serviceName) && !NullUtil.isNull(addUrl)) {

					try {					
						soapEnvelope.addNamespaceDeclaration(addName, addUrl);

					} catch (Exception e) {
						LOGGER.error("Service name space add error");;
					}
				}
			}

	    	/** Add Soap Action **/
	    	if (soapVersion == SoapVersion.SOAP_11) {
	    		if (infMap.get("soapAction") == null) {
	    			throw new BizException(BaseConstants.DEFAULT_EXCEPTION_ERROR_CODE, "Soap Action shoud be set");
	    		}
			    String soapAction = (String) infMap.get("soapAction");
			    saajSoapMessage.setSoapAction(soapAction);
	    	}
			
			
			/** Add Security Header with user name token**/			
			if (!NullUtil.isNull(infMap.get("usernameSecurity"))) {
				Map<String, Object>userNameSecurity = (Map<String, Object>) (infMap.get("usernameSecurity"));				
				addUsernameTokenSecurityHeader(soapHeader, userNameSecurity);				
			}
			else {
				/** Header Remove **/
				soapHeader.detachNode();
			}
			
			prefix = (String) (infMap.get("messagePrefix") != null ? infMap.get("messagePrefix") : null);
			
			/** Change prefix **/
			if (!NullUtil.isNull(prefix)) {
				soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());
				soapEnvelope.setPrefix(prefix);

				soapHeader.removeNamespaceDeclaration(soapHeader.getPrefix());
				soapHeader.setPrefix(prefix);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("BODY Prefix :: {}", soapBody.getPrefix());
				}
				soapBody.removeNamespaceDeclaration(soapBody.getPrefix());
				soapBody.setPrefix(prefix);
			}
			
//			if (NullUtil.isNull(prefix)) {
				//				if (soapVersion == SoapVersion.SOAP_11) {
//					soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());
//					soapEnvelope.setPrefix(WsConstants.DEFAULT_SOAP11_PREFIX);
//					soapHeader.setPrefix(WsConstants.DEFAULT_SOAP11_PREFIX);
//					soapBody.setPrefix(WsConstants.DEFAULT_SOAP11_PREFIX);
//				}
//				else {
//					soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());
//					soapEnvelope.setPrefix(WsConstants.DEFAULT_SOAP12_PREFIX);
//					soapHeader.setPrefix(WsConstants.DEFAULT_SOAP12_PREFIX);
//					soapBody.setPrefix(WsConstants.DEFAULT_SOAP12_PREFIX);
//				}
//			}
//			else {
//			}
			
			/** Changed message save **/
			soapMessage.saveChanges();
			
			/** SOAP Message Dump **/
			SoapMessagDump.dumpSoapMessage(soapMessage);

		} catch (Exception e) {
			throw new BizException(e.getMessage());
		}
		
		return true;
	}
	
	/**
	 * 
	 *<pre>
	 * Add Security Header with UsernameToken
	 *</pre>
	 * @param soapMessage SOAPMessage
	 * @throws Exception
	 */
	protected void addUsernameTokenSecurityHeader(SOAPHeader soapHeader, Map<String, Object> userNameSecurity ) throws Exception {


		/** Create Security Header **/
		QName security = new QName(WsConstants.DEFAULT_WSSE_NAMESPACE, "Security", WsConstants.DEFAULT_WSSE_PREFIX);
		SOAPHeaderElement eSecurity = soapHeader.addHeaderElement(security);
		
		/** Set actor **/
		String actor = (String) (userNameSecurity.get("actor") != null ? userNameSecurity.get("actor") : "");
		if (!NullUtil.isNull(actor)) {
			eSecurity.setAttribute("actor", actor);
//			eSecurity.setActor(actor);			
		}
		
		/** Set must understand flag **/
		String mustUnderstand = (String) (userNameSecurity.get("mustUnderstand") != null ? userNameSecurity.get("mustUnderstand") : "");
		if (!NullUtil.isNull(mustUnderstand)) {
			eSecurity.setAttribute("mustUnderstand", mustUnderstand);
//			eSecurity.setMustUnderstand(mustUnderstand);
		}

		/** Create UsernameToken Queue Name **/
		QName usernameToken = new QName(WsConstants.DEFAULT_WSSE_NAMESPACE, WsConstants.DEFAULT_WSSE_USERNAMETOKEN_TAG, WsConstants.DEFAULT_WSSE_PREFIX);
		SOAPHeaderElement eUsemameToken = soapHeader.addHeaderElement(usernameToken);
		
		/** Create User name element **/
		QName qUserName = new QName(WsConstants.DEFAULT_WSSE_NAMESPACE, WsConstants.DEFAULT_WSSE_USERNAME_TAG, WsConstants.DEFAULT_WSSE_PREFIX);
		SOAPHeaderElement eUserName = soapHeader.addHeaderElement(qUserName);

		String wsuId = (String) (userNameSecurity.get("wsuId") != null ? userNameSecurity.get("wsuId") : "");
		String username = (String) (userNameSecurity.get("username") != null ? userNameSecurity.get("username") : "");
		
		if (!NullUtil.isNull(wsuId)) {
			/** Add name space to user name with wsu **/
			eUserName.addNamespaceDeclaration("wsu", WsConstants.DEFAULT_WSSE_WSU_NAMESPACE);
			eUserName.setAttribute("wsu:Id", wsuId);
			eUserName.addTextNode(username);
		}
		
		/** Create Password element **/
		String password = (String) (userNameSecurity.get("password") != null ? userNameSecurity.get("password") : "");
		QName qPassword = new QName(WsConstants.DEFAULT_WSSE_NAMESPACE, WsConstants.DEFAULT_WSSE_PASSWORD_TAG, WsConstants.DEFAULT_WSSE_PREFIX);
		SOAPHeaderElement ePassword = soapHeader.addHeaderElement(qPassword);
		ePassword.setAttribute("Type", WsConstants.DEFAULT_WSSE_PASSWORD_NAMESPACE);
		ePassword.addTextNode(password);


		/** Add Child element to UsernameToken element **/
		eUsemameToken.addChildElement(eUserName);
		eUsemameToken.addChildElement(ePassword);
		
		/** Add Child element to Security element **/
		eSecurity.addChildElement(eUsemameToken);
		
	}

	public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
		return false;
	}

	public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
		return false;
	}

	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
}
