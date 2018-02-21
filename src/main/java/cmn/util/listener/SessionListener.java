package cmn.util.listener;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionListener implements HttpSessionListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);
	
	private static Hashtable<String, HttpSession> loginSessionMonitor = null;
	public static SessionListener sessionManager = null;
	
	public SessionListener() {
		if (loginSessionMonitor == null)
			loginSessionMonitor = new Hashtable<String, HttpSession>();
		sessionManager = this;
	}

	public static synchronized SessionListener getInstance() {
		if (sessionManager == null)
			sessionManager = new SessionListener();
		return sessionManager;
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		synchronized(loginSessionMonitor) {
			loginSessionMonitor.remove(session);
		}
	}

	public static void setLoginSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		synchronized (loginSessionMonitor) {
			loginSessionMonitor.put(session.getId(), session);
			LOGGER.info(" ############################################################################### ");
			LOGGER.info(" # Current Login User : {}", loginSessionMonitor.size());
			LOGGER.info(" ############################################################################### ");
		}
	}

	/** 로그아웃한 Session Remove */
	public void setLogoutSession(HttpSession session) {
		synchronized (loginSessionMonitor) {
			loginSessionMonitor.remove(session.getId());
		}
	}
	
	public static boolean checkDuplicationLogin(String sessionId, String userId) {
		boolean ret = false;
		Enumeration<HttpSession> eNum = loginSessionMonitor.elements();
		while (eNum.hasMoreElements()) {
			HttpSession sh_session = null;
			try {
				sh_session = eNum.nextElement();
			} catch (Exception e) {
				continue;
			}
		}
		return ret;
	}
}
