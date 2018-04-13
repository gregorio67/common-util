import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import stis.framework.exception.UtilException;

public class NetUtil {

	/**LOGGER SET **/
	private static final Logger LOGGER = LoggerFactory.getLogger(NetUtil.class);

	/** Inet Address **/
	private static InetAddress inetAddr = null;

	/**
	 * 
	 * <pre>
	 * Get Client IP
	 * </pre>
	 * @return String
	 */
	public static String getClinetIP() {

		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		String clientIP = null;

		try {
			clientIP =request.getHeader("X-Forwarded-For");

			if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
				clientIP = request.getHeader("Proxy-Client-clientIP");
			}

			if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
				clientIP = request.getHeader("WL-Proxy-Client-clientIP");
			}

			if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
				clientIP = request.getHeader("HTTP_CLIENT_clientIP");
			}

			if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
				clientIP = request.getHeader("HTTP_X_FORWARDED_FOR");
			}

			if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
				clientIP = request.getRemoteAddr();
			}

		} catch (Exception ex) {
			clientIP = null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Client IP Address :: {}", clientIP);
		}
		return clientIP != null ? clientIP : "UNKNOWN";
	}


	/**
	 * This is for get the Network Interface which is used in the system.
	 * @return
	 */
	public static InetAddress getNetworkInterface() throws Exception {
		InetAddress ia = null;
		try {
			Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();
			while (nienum.hasMoreElements()) {
				NetworkInterface ni = nienum.nextElement();

				Enumeration<InetAddress> kk= ni.getInetAddresses();

				while (kk.hasMoreElements()) {

					InetAddress inetAddress = kk.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
						ia = inetAddress;
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new UtilException(e.getMessage(), e);
		}

		if (ia == null) {
			ia = InetAddress.getLocalHost();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inet Address is used getLocalHost");
			}
		}
		return ia;
	}

	/**
	 * Host name
	 * <pre>
	 *
	 * </pre>
	 * @return String
	 * @throws Exception
	 */
	public static  String getHostName() throws Exception {
		if (inetAddr == null) {
			inetAddr = getNetworkInterface();
		}
		return inetAddr.getHostName() != null ? inetAddr.getHostName() : "UNKNOWN";
	}

	/**
	 * Host IP Address
	 * <pre>
	 *
	 * </pre>
	 * @return String
	 * @throws Exception
	 */
	public static  String getHostAddr() throws Exception {
		if (inetAddr == null) {
			inetAddr = getNetworkInterface();
		}
		return inetAddr.getHostAddress() != null ? inetAddr.getHostAddress() : "UNKNOWN";

	}

}
