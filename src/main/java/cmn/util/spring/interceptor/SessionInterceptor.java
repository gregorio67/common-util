
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class SessionInterceptor extends HandlerInterceptorAdapter implements InitializingBean{
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionInterceptor.class);
	
//	private static final String sApiKey = "B69FAF368B184727E89A";
	
	/** Login Check Skip URIs **/
	private List<String> skipUris;
	
	/** LOGIN URL **/
	private String loginURL;
	

	/** Skip Pattern **/
	private static Pattern skipPattern[];
	
	
	public void setSkipUris(List<String> skipUris) {
		this.skipUris = skipUris;
	}

	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
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

		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", Long.valueOf(startTime));
		request.setAttribute("requestURI", requestURI);
		request.setAttribute("clientIP", NetUtil.getClinetIP());

		/** SSO CHECK **/
		String sToken = request.getAttribute(KicsDefaultConstants.SSO_TOKEN_NAME) != null ? request.getAttribute(KicsDefaultConstants.SSO_TOKEN_NAME).toString() : "";
		
		if( !Util.isNull(sToken)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SSO Login start with token :: {}", sToken);
			}			
			
			String apiKey = PropertiesUtil.getString("sso.api.key");
			SSO sso = new SSO(apiKey);	
							
			int nResult = sso.verifyToken(sToken, request.getRemoteAddr());

			/** SSO LOG **/
			LOGGER.info("SSO Result[{}], with token[{}] and api key[{}]", nResult, sToken, apiKey);				
			
			String userId = null;
			if(nResult >= 0){
				userId 	= sso.getValueUserID();	
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SSO User ID :: {}", userId);
				}
			}
						
			if (Util.isNull(userId)) {
				throw new KicsLoginException("com.err.auth.001");
			}
			else {
				/** Create New Session Value Object **/
			  SessionVo sessionVo = new SessionVo();
				sessionVo.setCivpnId(userId);
				SessionUtil.setSession(KicsDefaultConstants.DEFAULT_SESSION_NAME, sessionVo);				
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SSO Login called successfuly");
			}
			return super.preHandle(request, response, handler);
		}
		
		
		/** Session Vo Check if null create new session Vo		 */
		KicsSessionVo sessionVo = SessionUtil.getSession(KicsDefaultConstants.DEFAULT_SESSION_NAME);
		if (sessionVo == null) {
			sessionVo = new KicsSessionVo();
		}
		
		/**
		 * Login SKIP URI Check
		 */
		if (skipUris.size() > 0) {
			if (!isSkipMatch(requestURI)) {
				
				if (Util.isNull(sessionVo.getCivpnId())) {
					LOGGER.info("{} is called before user is not logined", requestURI);
					
					/** If loginURL is set, redirect to set URL **/
					if (!Util.isNull(loginURL)) {
						LOGGER.info("Send Redirect because user is not logined :: Request URI :: {}, sendURL :: {}", requestURI, loginURL);
						response.sendRedirect(loginURL);	
						return super.preHandle(request, response, handler);						
					}
					throw new KicsLoginException("com.err.auth.001");
				}
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

		String requestURI = request.getAttribute("requestURI").toString();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Session Interceptor is called : {}", requestURI);
		}
	
		/** If request URI is login or logout, set host name, host address  to session information 
		 *  When logout, session information is invalidated
		 * */
		if (response.getStatus() == 200) {
			if (requestURI.contains(DefaultConstants.DEFAULT_LOGIN_URI)) {
				SessionVo sessionVo = SessionUtil.getSession(DefaultConstants.DEFAULT_SESSION_NAME);
			
				sessionVo.setHostName(NetUtil.getHostName());
				sessionVo.setHostAddr(NetUtil.getHostAddr());
				sessionVo.setClientIp(NetUtil.getClinetIP());
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Host Name :: {}, Host Address :: {} , Client IP :: {}", sessionVo.getHostName(), sessionVo.getHostAddr(), sessionVo.getClientIp());
				}
				SessionUtil.setSession(DefaultConstants.DEFAULT_SESSION_NAME, sessionVo);

			}
			else if (requestURI.contains(DefaultConstants.DEFAULT_LOGOUT_URI)) {
				SessionVo sessionVo = SessionUtil.getSession(DefaultConstants.DEFAULT_SESSION_NAME);
				if (sessionVo != null) {
					SessionUtil.invalidSession();
				}
			}			
		}

		long startTime = (Long) request.getAttribute("startTime");
		long endTime = System.currentTimeMillis();
		LOGGER.info("Request URL::{} Request ClinetIP::{} Response Code::{} Start Time::{} End Time::{}  Elapse Time::{}(ms)",
				requestURI, request.getAttribute("clientIP"), response.getStatus(), startTime, endTime, (endTime - startTime));		
	}

	/**
	 * Check URI is match the skipUris
	 * <pre>
	 *
	 * </pre>
	 * @param source String
	 * @return boolean
	 * @throws Exception
	 */
	private boolean isSkipMatch(String requestUri) throws Exception {
		
		boolean isMatch = false;
		for (Pattern pattern : skipPattern) {
			if (pattern.matcher(requestUri).find()) {
				isMatch = true;
				break;
			}
		}
		return isMatch;
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

	@Override
	public void afterPropertiesSet() throws Exception {
		if (skipUris == null || skipUris.size() == 0) {
			return;
		}
		skipPattern = new Pattern[skipUris.size()];
		for (int i = 0; i < skipUris.size(); i++) {
			skipPattern[i] = Pattern.compile(patternString(skipUris.get(i)));
		}		
	}
}
