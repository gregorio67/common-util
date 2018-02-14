package cmn.util.common;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import cmn.util.exception.BaseException;

public class SoapMessagDump {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SoapMessagDump.class);

	/**
	 * 
	 *<pre>
	 * SOAP Message Dump
	 *</pre>
	 * @param saajSoapMessage SaajSoapMessage
	 * @throws Exception
	 */
	public static void dumpSoapMessage(SaajSoapMessage saajSoapMessage) throws Exception {
		dumpSoapMessage(saajSoapMessage.getSaajMessage());
	}
	/**
	 * 
	 *<pre>
	 * SOAP Message Dump
	 *</pre>
	 * @param soapMessage SOAPMessage
	 * @throws Exception
	 */
	public static void dumpSoapMessage(SOAPMessage soapMessage) throws Exception {
		if (NullUtil.isNull(soapMessage)) {
			return;
		}
		
		LOGGER.info("------------------------------------------");
		LOGGER.info("DUMP OF SOAP MESSAGE");
		LOGGER.info("------------------------------------------");

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			soapMessage.writeTo(baos);
            LOGGER.info("Request Message  :: {}", baos.toString(getMessageEncoding(soapMessage)));
            
            String values = soapMessage.getSOAPBody().getTextContent();
            LOGGER.info("Included Value :: {}" , values);
		}
		catch(Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new BaseException("Soap Message Convert Error");
		}
	}

	
	/**
	 * 
	 *<pre>
	 * SOAP Message Encoding
	 *</pre>
	 * @param msg
	 * @return
	 * @throws SOAPException
	 */
	private static String getMessageEncoding(SOAPMessage msg) throws SOAPException {
		String encoding = "utf-8";
		if (msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING) != null) {
		encoding = msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING).toString();
		}
		return encoding;
	}
}
