
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtil {
	
	/**LOGGER SET **/
	private static final Logger LOGGER = LogManager.getLogger(SessionUtil.class);
	/**
     * 세션정보에서 attribute set
     * @param Stringattribute key name
     * @return void
     */
    public static void setSession(String name, Object object) throws Exception, Exception{
    	RequestContextHolder.getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
    }    
    
    /**
     * 세션정보에서 attribute get
     * @param Stringattribute key name
     * @return Objectattribute Obj
     */
	@SuppressWarnings("unchecked")
	public static <T> T getSession(String name) throws Exception, Exception{
    	return (T) RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }
    
    /**
     * 세션정보에서 attribute delete
     * @param Stringattribute key name
     * @return void
     */
    public static void removeSession(String name) throws Exception, Exception{
    	RequestContextHolder.getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }
    
    /**
     * 세션삭제
     * @param request HttpServletRequest
     * @return void
     */
    public static void invalidSession() throws Exception, Exception{
    	ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
    	attributes.getRequest().getSession().invalidate();
    }
    
        
    /**
     * Get User ID from session
     *<pre>
     *
     *</pre>
     * @return
     * @throws Exception
     */
    public static String getUserId() throws Exception {
    	SessionVo sessionVo = getSession(DefaultConstants.DEFAULT_SESSION_NAME);
    	if (sessionVo == null) {
    		throw new LRuntimeException(DefaultConstants.LOGIN_REQUIRE_CODE);
    	}
    	if (NullUtil.isNull(sessionVo.getUserId())) {
    		throw new LRuntimeException(DefaultConstants.LOGIN_REQUIRE_CODE);    		
    	}
    	return sessionVo.getUserId();
    }
    /**
     * Return Language from session information
     *<pre>
     *
     *</pre>
     * @return String
     * @throws Exception
     */
    public static String getCultLang() throws Exception {

    	/** Session was not created, set default language **/
    	if (getSession(DefaultConstants.DEFAULT_SESSION_NAME) == null) {
    		return "la";
    	}

    	SessionVo sessionVo = getSession(DefaultConstants.DEFAULT_SESSION_NAME);

    	return sessionVo.getLanguage() != null ? sessionVo.getLanguage() : PropertiesUtil.getString("taxris.default.locale");
    }
    
    /**
     * Check current language is LAOS or not, if session is null, return true(default is LAOS)
     *<pre>
     *
     *</pre>
     * @return
     * @throws Exception
     */
    public static boolean isLao() throws Exception {
    	SessionVo sessionVo = getSession(DefaultConstants.DEFAULT_SESSION_NAME);
    	if (sessionVo == null) {
    		return true;
    	}
    	if (!"en".equalsIgnoreCase(sessionVo.getLanguage())) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    /**
     * Check current language is not LAOS
     *<pre>
     *
     *</pre>
     * @return
     * @throws Exception
     */
    public static boolean isNotLao() throws Exception {
    	return !isLao();
    }
}
