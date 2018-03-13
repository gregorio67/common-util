import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class PackageScanningJaxb2Marshaller  extends Jaxb2Marshaller{

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageScanningJaxb2Marshaller.class);
    
    /** base package location **/
    private List<String> basePackages;
    
    /** XML Filter for scanning package **/
    private final TypeFilter[] jaxb2TypeFilters = new TypeFilter[]{
    		            new AnnotationTypeFilter(XmlRootElement.class, false),
    		            new AnnotationTypeFilter(XmlType.class, false),
    		            new AnnotationTypeFilter(XmlSeeAlso.class, false),
    		            new AnnotationTypeFilter(XmlEnum.class, false)
    };


    public List<String> getBasePackages() { 
    	return basePackages; 
    }
    
    @Required
    public void setBasePackages(List<String> basePackages) { 
    	this.basePackages = basePackages; 
    }
    
    
    /**
     * This method should scan base package with XML related annotation filters to find classes 
     */
    private Class<?>[] getXmlRootElementClasses() throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        for (TypeFilter typeFilter : jaxb2TypeFilters) {
            scanner.addIncludeFilter(typeFilter);        	
        }
        
        List<Class<?>> classes = new ArrayList<Class<?>>();
        
        /** Find classes with base package **/
        for (String basePackage : basePackages) {
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition definition : definitions) {
                String className = definition.getBeanClassName();
               
                LOGGER.info("Found class: {}", className);
                
                classes.add(Class.forName(className));
            }
        }
        
        return classes.toArray(new Class[0]);
    }
    
    @PostConstruct
    public void init() throws Exception {
        setClassesToBeBound(getXmlRootElementClasses());
    }

}
