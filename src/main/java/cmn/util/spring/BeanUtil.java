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
}
