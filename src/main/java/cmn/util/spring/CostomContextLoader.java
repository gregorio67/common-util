package cmn.util.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cmn.util.converter.MapUtil;

public class CostomContextLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);

	private static final Object synObject = new Object();


	/**
	 * 
	 *<pre>
	 * This is load context with xml configuration file
	 *</pre>
	 * @param contextLocation String Spring configuration file
	 * @return ClassPathXmlApplicationContext
	 * @throws Exception
	 */
	public static ClassPathXmlApplicationContext getAppContext(String contextLocation) throws Exception {
		try {
			synchronized(synObject) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("ContextLoader.getAppContext()");
				}
				return new ClassPathXmlApplicationContext(contextLocation);
			}
		} catch (Exception e) {
			throw new Exception("Appliocation context create Error");
		}
	}

}

