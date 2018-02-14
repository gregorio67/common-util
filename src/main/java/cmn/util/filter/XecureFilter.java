package kics.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kics.framework.constant.KicsDefaultConstants;
import kics.framework.util.Util;
import xecure.crypto.Cipher;
import xecure.servlet.XecureConfig;
import xecure.servlet.XecureSession;




public class XecureFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(KicsXecureFilter.class);
	private String encoding = KicsDefaultConstants.DEFAULT_ENCODING;
	
	@Override
	public void destroy() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Filter is destoyed..");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)	throws IOException, ServletException {
		
		KicsEncRequestWrapper kicsReqWrapper = new KicsEncRequestWrapper((HttpServletRequest)request, (HttpServletResponse) response);		
		
		KicsEncResponseWrapper kicsResWrapper = new KicsEncResponseWrapper((HttpServletResponse) response);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Wrapper and Response Wrapper is created..");
		}

		chain.doFilter(kicsReqWrapper, kicsResWrapper);
	
		/** Read Response Data from Response Wrapper **/
		String resContents = new String(kicsResWrapper.getDataStream(), encoding);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Response Contents with {} :: {}", encoding, resContents);
		}
		
		/** Check Content Length If contents length are greater than 0, encrypt response data **/
		if (resContents != null && !"".equals(resContents)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Before Encrypt :: {}", resContents);
			}
			
			/**  XecureWeb 적용 --> 암호화  **/
			byte[] responseToSend =  null;
			
			/** HttpRequest에 xSession이 있는 경우 암호화됨 **/
			XecureSession xSession = (XecureSession)request.getAttribute("xSession");
			if (xSession != null) {
				XecureConfig xConfig = new XecureConfig();
				Cipher xCipher = new Cipher(xConfig);
				
				responseToSend =  xCipher.BlockEncrypt(xSession, resContents).getBytes();				
			}
			/**
			 * 암호화 적용하지 않음
			 */
			else {
//				responseToSend = resContents.getBytes();
				responseToSend = kicsResWrapper.getDataStream();
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Before Encrypt :: {}, After Encrypt :: {}", resContents, new String(responseToSend));
			}

			/** Set Encoding Parameter */
			response.setCharacterEncoding(encoding);

			response.getOutputStream().write(responseToSend);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		/** Read init encoding from web.xml */ 
		String encodingParam = filterConfig.getInitParameter("encoding");
		
		if (!Util.isNull(encodingParam)) {
			encoding = encodingParam;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Filter is initialized with encoding parameter :: {}", encoding);
		}
	}
}
