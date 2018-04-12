package cmn.util.spring;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import cmn.util.base.BaseUtil;

public class BeanUtil extends BaseUtil{

	/**
	 * 
	 *<pre>
	 * Spring Bean return with bean name
	 *</pre>
	 * @param beanName String 
	 * @return
	 * @throws Exception
	 */
	public static <T> T getBean(String beanName) throws Exception {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

		@SuppressWarnings("unchecked")
		T bean = (T) context.getBean(beanName);

		return bean;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Return WebApplicationContext
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @return WebApplicationContext
	 * @throws Exception
	 */
	
	public static WebApplicationContext getContext() throws Exception {
		return ContextLoader.getCurrentWebApplicationContext();
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Return Current Active profiles
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @return String[]
	 * @throws Exception
	 */
	public static String[] getActiveProfile() throws Exception {
		return getContext().getEnvironment().getActiveProfiles();
	}
	

	public static InputStream getResoure(String resourceName) throws Exception {
		char[] charName = resourceName.toCharArray();
		if (charName[0] == File.pathSeparatorChar) {
			resourceName = resourceName.substring(1);
		}
		resourceName = ResourceUtils.CLASSPATH_URL_PREFIX + resourceName;
		return new FileInputStream(ResourceUtils.getFile(resourceName));
	}
	
}
