
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import taxris.framework.exception.LServiceBlockException;
import taxris.framework.util.NullUtil;

public class ServiceIntercepter extends HandlerInterceptorAdapter implements InitializingBean{
	/**LOGGER SET **/
	private static final Logger LOGGER = LogManager.getLogger(ServiceIntercepter.class);
	
	/** Service Block URIs  **/
	private List<String> blockUris;
	
	/** When block uris is match, Block URL  **/
	private String blockURL;
	
	
	/** Block Pattern **/
	private Pattern blockPattern[];
	

	public void setBlockUris(List<String> blockUris) {
		this.blockUris = blockUris;
	}

	public void setBlockURL(String blockURL) {
		this.blockURL = blockURL;
	}

	/**
	 * Return boolean
	 * 
	 * @param HttpServletRequest
	 * @param HttpServletResponse
	 * @paeam Object handler
	 * @return boolean
	 * @see 
	 */

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String requestURI = request.getRequestURI();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Service Interceptor is called : {}", requestURI);
		}
		/**
		 * Block URI Check
		 */
		if (blockUris.size() > 0) {
			if (isBlockMatch(requestURI)) {
				LOGGER.info("{} is currently blocked url :: {} ", requestURI);
				
				/** Block URL is set, redirect to set URL */
				if (!NullUtil.isNull(blockURL)) {
					LOGGER.info("Send Redirect because the uri is blocked :: Request URI :: {}, sendURL :: {}", requestURI, blockURL);
					response.sendRedirect(blockURL);
					return super.preHandle(request, response, handler);
				}

				throw new LServiceBlockException("com.err.auth.004");			
			}
			
		}				
		return super.preHandle(request, response, handler);
	}

	/**
	 * Return void
	 * 
	 * @param HttpServletRequest
	 * @param HttpServletResponse
	 * @paeam Object handler
	 * @return void
	 * @see
	 */
	@Override
	public void postHandle(HttpServletRequest request,	HttpServletResponse response, Object handler, ModelAndView mav) throws Exception {

	}

	/**
	 * 
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

	/**
	 * Replace pattern String
	 * <pre>
	 *
	 * </pre>
	 * @param source String
	 * @return String
	 */
	private String patternString(String source) {
		
		char[] tempChars = source.toCharArray();
		StringBuilder sb = new StringBuilder();
		sb.append("^");
		for (char c : tempChars) {
			if (c == '/') {
				sb.append("\\").append(c);
			}
			else if (c == '*') {
				sb.append("*(.+)");
			}
			else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}	
	/**
	 * Check Block URL
	 * <pre>
	 *
	 * </pre>
	 * @param source String
	 * @return boolean
	 * @throws Exception
	 */
	private boolean isBlockMatch(String requestUri) throws Exception {
		
		boolean isMatch = false;
		for (Pattern pattern : blockPattern) {
			if (pattern.matcher(requestUri).find()) {
				isMatch = true;
				break;
			}
		}
		return isMatch;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (blockUris == null || blockUris.size() == 0) {
			return;
		}
		
		blockPattern = new Pattern[blockUris.size()];
		for (int i = 0; i < blockUris.size(); i++) {
			blockPattern[i] = Pattern.compile(patternString(blockUris.get(i)));
		}
		
	}
	
}
