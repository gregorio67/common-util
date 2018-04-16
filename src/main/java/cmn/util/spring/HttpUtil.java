import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cmn.util.base.BaseConstants;
import cmn.util.common.NullUtil;
import cmn.util.exception.UtilException;


public class HttpUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
	
	/**
	 * 
	 *<pre>
	 * Set key, value to HttpServletRequest 
	 *</pre>
	 * @param key String
	 * @param value String
	 * @throws Exception
	 */
	public static <E> void setRequestData(String key, E value) throws Exception {
		
		if (NullUtil.isNull(key) || NullUtil.isNull(value)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"key and value should be not null");			
		}
		
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		
		HttpServletRequest request = attributes.getRequest();
		
		if (NullUtil.isNull(request)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"HttpRequest is null");
		}
		request.setAttribute(key, value);
	}
	
	/**
	 * 
	 *<pre>
	 * Retrieve data from HttpServletRequest
	 *</pre>
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRequestData(String key) throws Exception {

		if (NullUtil.isNull(key)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"key and value should be not null");			
		}

		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		
		HttpServletRequest request = attributes.getRequest();
		if (NullUtil.isNull(request)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"HttpRequest is null");
		}
		return (T) request.getAttribute(key);
	}
	
	
	/**
	 * 
	 *<pre>
	 * Set response header
	 *</pre>
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public static <E> void setResponsetHeader(String key, String value) throws Exception {
		
		if (NullUtil.isNull(key) || NullUtil.isNull(value)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"key and value should be not null");			
		}
		
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		
		HttpServletResponse response = attributes.getResponse();
		
		if (NullUtil.isNull(response)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"HttpRequest is null");
		}
		
		response.setHeader(key, value);
	}
	
	/**
	 * 
	 *<pre>
	 * Get Response Header value
	 *</pre>
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getResponsetHeader(String key) throws Exception {
		
		if (NullUtil.isNull(key)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"key and value should be not null");			
		}
		
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		
		HttpServletResponse response = attributes.getResponse();
		
		if (NullUtil.isNull(response)) {
			LOGGER.error("HttpServletRequest is null");
			throw new UtilException(BaseConstants.DEFAULT_EXCEPTION_URIL_CODE,"HttpRequest is null");
		}
		
		return response.getHeader(key);
	}
  
  
