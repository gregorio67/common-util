package cmn.util.spring.message;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ContextRefresh implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
	}
	
	public void refreshContext() {
		((ConfigurableApplicationContext)applicationContext).refresh();
	}
	
	public void refreshBean(String beanName, String beanClassName) {
		
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext)applicationContext).getBeanFactory();

		BeanDefinition beanDefinition = defaultListableBeanFactory.getBeanDefinition(beanName);
		
		beanDefinition.setBeanClassName(beanClassName);
		
		defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
				
		defaultListableBeanFactory.initializeBean(defaultListableBeanFactory.getBean(beanName), beanName);
	}
	
	public static void changeBeanClass(String beanName, String className) throws ClassNotFoundException, LinkageError {
		XmlWebApplicationContext xmlWebApplicationContext = (XmlWebApplicationContext) ContextLoader.getCurrentWebApplicationContext();
		
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) xmlWebApplicationContext.getBeanFactory();
		
		BeanDefinition beanDefinition = defaultListableBeanFactory.getBeanDefinition(beanName);
	
		beanDefinition.setBeanClassName(className);
		
		defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
		
		
		xmlWebApplicationContext = (XmlWebApplicationContext) ContextLoader.getCurrentWebApplicationContext();
		defaultListableBeanFactory = (DefaultListableBeanFactory) xmlWebApplicationContext.getBeanFactory();
		beanDefinition = defaultListableBeanFactory.getBeanDefinition(beanName);
		beanDefinition.setBeanClassName(className);
		defaultListableBeanFactory.registerBeanDefinition( beanName, beanDefinition); 		
	}

}
