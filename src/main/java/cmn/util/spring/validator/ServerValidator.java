import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;	
import org.springframework.context.MessageSource;
import org.springmodules.validation.commons.DefaultValidatorFactory;


public class ServerValidator {

	private DefaultValidatorFactory validatorFactory = null;
	
	private static final Logger LOGGER = LogManager.getLogger(ServerValidator.class);
	
    @Resource(name ="messageSource")
    protected MessageSource messageSource;
    
	public void setValidatorFactory(DefaultValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}
	
	/**
	 * Server Side에서 Validation Rule을 읽어 validate 수행
	 * <pre>
	 *
	 * </pre>
	 * @param voName Value String Object Name
	 * @param voObj Object Value Object
	 * @return Map<String, String>
	 * @throws Exception
	 */
	public Map<String, String> serverValidation(String voName , Object voObj) throws Exception {

		Map<String, String> returnResult = new HashMap<String, String>();
    	ValidatorResources resource = validatorFactory.getValidatorResources();
    	Validator validator = new Validator(resource, voName); 
    	validator.setParameter(Validator.BEAN_PARAM , voObj);
    	validator.setOnlyReturnErrors(true);
    	
		ValidatorResults validResults = validator.validate();
		
		Form form = resource.getForm(Locale.getDefault(), voName);
		
		@SuppressWarnings("unchecked")
		Iterator<String> propertyNames = validResults.getPropertyNames().iterator();
		
		while( propertyNames.hasNext() ){
			
			String propertyName = propertyNames.next();
						
			Field field = form.getField(propertyName);

			ValidatorResult result = validResults.getValidatorResult(propertyName);
			Iterator<String> keys = result.getActions();
			
			while(keys.hasNext()){
				String actName = keys.next();
//				log.debug("actName = " + actName); // actName = byteMaxLength
				
				ValidatorAction action = resource.getValidatorAction(actName);
				
				if(!result.isValid(actName)){
					String msgCode = "sys.err.validate." + actName;
					String msg = messageSource.getMessage(msgCode, null, Locale.getDefault());
					returnResult.put(propertyName, msg);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug( "Validation Failed : {},{}", propertyName, msg) ;						
					}
				}	
			}	
		}   
		
		return returnResult;
	}
}
