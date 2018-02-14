package cmn.util.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CrossDomainFilter  implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		  HttpServletResponse servletResponse = (HttpServletResponse) response;

//		  servletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		servletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET");
		servletResponse.setHeader("Access-Control-Max-Age", "3600");
		servletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		/** "*" means to apply all domains **/
		/** To apply each domain, set each domain **/
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
//		servletResponse.addHeader("Access-Control-Allow-Origin", "http://S1.ABC.co.kr");
//	    servletResponse.addHeader("Access-Control-Allow-Origin", "http://S2.ABC.co.kr");
//	    servletResponse.addHeader("Access-Control-Allow-Origin", "http://S3.ABC.co.kr");
	    chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
	
	/** Set web.xml
	<filter>
	    <filter-name>cors</filter-name>
	    <filter-class>cmn.util.filter.CrossDomainFilter</filter-class>
	</filter>

	<filter-mapping>
	    <filter-name>cors</filter-name>
	    <url-pattern>/*</url-pattern>
	</filter-mapping>  **/
}
