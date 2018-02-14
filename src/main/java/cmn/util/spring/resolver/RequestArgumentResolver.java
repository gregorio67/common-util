import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import taxris.framework.collection.ParameterMap;
import taxris.framework.util.SecurityUtil;



public class RequestArgumentResolver implements HandlerMethodArgumentResolver  {
  
	private static final Logger LOGGER = LogManager.getLogger(RequestArgumentResolver.class);
		
	/**
	 * This is for extracting all parameter for http request, and then put the key and value to CommandMap 
	 * @param MethodParameter parameter 
	 * @param ModelAndViewContainer arg1mavContainer
	 * @param NativeWebRequest webRequest
	 * @param NWebDataBinderFactory binderFactory
	 * @return CommandMap
	 * @see com.uzbek.ips.common.RequestArgumentResolver#resolveArgument() This method create the CommanMap value object 
	 *      which is stored the all request parameters.
	 */
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		
		RequestrMap requestMap = new RequestrMap();
        
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        
		/**
		 * Get the all parameter from the request
		 */
		Enumeration<?> enumeration = request.getParameterNames(); 
        
        String key = null;
        String[] values = null;        
        /**
         * Parsing the parameters and store the parameters into the RequestMap
         */
        while(enumeration.hasMoreElements()){ 
            key = (String) enumeration.nextElement();

            values = request.getParameterValues(key);
            /**
             * Check whether value is null or not, If value is null, set the value null 
             */
            if(values.length > 0) {
            	if (values[0].isEmpty()) {
            		requestMap.put(key,null);
            	}
            	else {
            		if (values.length > 1) {
            			String[] targetValue = new String[values.length];
            			int idx = 0;
            			for (String tempValue : values) {
            				targetValue[idx++] =  SecurityUtil.replaceForSecurity(tempValue);
            			}
               			requestMap.put(key, targetValue);            				
            		}
            		else {
               			requestMap.put(key, SecurityUtil.replaceForSecurity(values[0]));            				
            		}
//            		requestMap.put(key, (values.length > 1) ? values:values[0] );
            	}
            }
            else {
            	requestMap.put(key,null);
            }            	
        }
        
        return requestMap;
	}
	
	/**
	 * Default Generated
	 */
	
	public boolean supportsParameter(MethodParameter parameter) {
		return ParameterMap.class.isAssignableFrom(parameter.getParameterType());
	}
} 
