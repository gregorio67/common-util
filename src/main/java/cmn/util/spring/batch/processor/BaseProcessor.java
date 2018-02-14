package cmn.util.spring.batch.processor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import cmn.util.dao.BaseDao;


public abstract class BaseProcessor<T> implements ItemProcessor< T, T>, InitializingBean {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	protected BaseDao baseDao;
	
	/** Input Parameter **/
	protected Map<String, Object> parameterValues;

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}
	
	@Override
	public T process(T paramMap) throws Exception {
		LOGGER.info("{} processor is started", this.getClass().getName());

		T resultMap = executeProcess(paramMap);

		LOGGER.info("{} processor is successfullly ended", this.getClass().getName());
		return resultMap;
	}
	
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
	
		Assert.notNull(baseDao, "DAO should be configured");
	}

	public abstract T executeProcess(T paramMap) throws Exception;

}

