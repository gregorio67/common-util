package kics.framework.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kics.framework.constant.KicsDefaultConstants;
import kics.framework.exception.KicsRuntimeException;
import kics.framework.util.Util;
import xecure.crypto.Cipher;
import xecure.servlet.XecureConfig;
import xecure.servlet.XecureSession;



public class EncRequestWrapper extends HttpServletRequestWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(KicsEncRequestWrapper.class);
	
	/* Request Body Contents */
	private String requestBody = null;
	
	/* Default Character Set */
	private static final String DEFAULT_ENCODING = KicsDefaultConstants.DEFAULT_ENCODING;
	
	/** Encoding Character **/
	private String encoding = null;

	public KicsEncRequestWrapper(HttpServletRequest request, HttpServletResponse response) throws IOException {
		super(request);

		this.encoding = DEFAULT_ENCODING;
		init(request, response);
	}
	

	public KicsEncRequestWrapper(HttpServletRequest request, HttpServletResponse response, String encoding) throws IOException {
		
		super(request);
		this.encoding = encoding;
		init(request, response);
	}
	
	/**
	 * Initialize construction
	 * <pre>
	 *
	 * </pre>
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */

	private void init(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String readBody = IOUtils.toString(request.getInputStream());		
		
		/** SSO LOGIN **/
		if (readBody.contains(KicsDefaultConstants.SSO_TOKEN_NAME)) {
			String[] tempArrs = readBody.split("&");
			for (String temp : tempArrs) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SSO Parameter :: {}", temp);
				}
				if (temp.contains(KicsDefaultConstants.SSO_TOKEN_NAME)) {
					String[] temptokens = temp.split("=");
					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("SSO TOKEN :: {}", temptokens[1]);
					}
					request.setAttribute(KicsDefaultConstants.SSO_TOKEN_NAME, temptokens[1] != null ?  temptokens[1] : "");
				}
			}
		}
		else {
			/** Split Request body
			 *  q is encrypt session, p is encrypted data
			 *  Client 에서 전달 받은 q값(SID)/ P값(Plan암호화 데이터) 저장
			 *  Format : q=xxxx&p=yyyy&charset=UTF-8
			 */
			if (readBody.contains(KicsDefaultConstants.DEFAULT_ANYSIGN_ENCDATA) && readBody.contains(KicsDefaultConstants.DEFAULT_ANYSIGN_SSID)) {		
				String[] requestData = readBody.split("&");
							
				/** Create XecureWeb Session with q value **/
				XecureConfig xConfig = new XecureConfig();
				XecureSession xSession = null;
				Cipher xCipher = null;
				String encData = null;;
				
				for (String requestDatum : requestData) {			
					/** Create Session **/
					if (requestDatum.contains(KicsDefaultConstants.DEFAULT_ANYSIGN_SSID)) {
						String[] tempSid = requestDatum.split("=");
						if (!Util.isNull(tempSid[1])) {
							xSession = new XecureSession(xConfig, tempSid[1], "/",  request.getHeader("User-Agent"));
							
							/** Set XecureWeb session to request, this session will be used in response when response data is encrypted **/
							request.setAttribute("xSession", xSession);						
						}
						else {
							throw new KicsRuntimeException("sys.err.frame.051");
						}
					}
					/** Read Encrypted Data **/
					else if (requestDatum.contains(KicsDefaultConstants.DEFAULT_ANYSIGN_ENCDATA)){
						String[] tempData = requestDatum.split("=");
						if (!Util.isNull(tempData[1])) {
		 					encData = URLDecoder.decode(tempData[1], encoding);											
						}
						else {
							throw new KicsRuntimeException("sys.err.frame.052");						
						}
					}
					/** Read Encoding Character Set **/
					else if (requestDatum.contains("charset=")) {
						String[] tempCharset = requestDatum.split("=");
						if (Util.isNull(tempCharset[1])) {
							encoding = DEFAULT_ENCODING;
						}
						else {
							encoding = tempCharset[1];
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Input Encoding :: {}", tempCharset[1]);
							}
						}
					}
				}
				
				/** Create Cipher Object **/
				xCipher = new Cipher(xConfig, encoding);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Encoding Charset :: {}", encoding);
				}

				/** Decrypt request message **/
				requestBody = xCipher.BlockDecrypt(xSession, encData);			
			}
			/**The request is not encrypted **/
			else {
				requestBody = readBody;
			}			
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request URI :: {}, Request Body :: {}:{}", request.getRequestURI(), readBody, requestBody);
		}	
		
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream bios = new ByteArrayInputStream(requestBody.getBytes(encoding));
		return new ServletInputStream() {
			public int read() throws IOException {
				return bios.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener listner) {				
			}
		};
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
	
	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		
		if (values == null) {
			return null;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parameter Values :: {}={}", parameter, Arrays.asList(values));			
		}
		return values;
	}

	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parameter Value :: {}={}", parameter, value);			
		}
		return value;

	}

}
