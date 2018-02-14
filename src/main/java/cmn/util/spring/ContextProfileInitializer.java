
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

public class ContextProfileInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	private static final Logger LOGGER = LogManager.getLogger(ContextProfileInitializer.class);

	@Override
	public int getOrder() {
		return org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		try {
			InputStream sysconfSream = applicationContext.getResource("classpath:properties/system.properties").getInputStream();
			Properties sysProperty = new Properties();
			sysProperty.load(sysconfSream);
			final String mainProfile = sysProperty.getProperty( "spring.profiles.active", "local" );
			
			// Check: 설정 값 유효여부
			Assert.isTrue( "local,dev,qa,prod".contains( mainProfile ), "Spring Profile is one of local,dev,qa,prod" );

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Current Spring Activ Profile :: {}", mainProfile);
			}
			ConfigurableEnvironment environment = applicationContext.getEnvironment();

			// Spring Profile 추가
			environment.addActiveProfile( mainProfile );
		}
		catch(IOException propertyLoadError) {
			LOGGER.error( "Didn't find system.properties in classpath so not loading it in the ContextProfileInitializer" );
		}
	}

}

