package cmn.util.spring.batch.mapper;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import cmn.util.dao.CamelMap;

public class CamelMapMapper<T> implements FieldSetMapper<T>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CamelMapMapper.class);

	private String[] colNames;
	
	@SuppressWarnings("unchecked")
	@Override
	public T mapFieldSet(FieldSet fieldset) throws BindException {
		CamelMap camelMap = new CamelMap();
		
		List<String> listCols = Arrays.asList(colNames);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("colNames :: {}", listCols.toArray());
		}
		for (String attr : listCols) {
			camelMap.put(attr, fieldset.readString(attr));	
		}		
		return (T) camelMap;
	}

	public void setColNames(String[] colNames) {
		this.colNames = colNames;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(colNames, "Column name should be set. Check your configuration");
	}

}
