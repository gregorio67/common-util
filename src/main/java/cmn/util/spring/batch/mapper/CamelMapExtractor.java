package cmn.util.spring.batch.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.InitializingBean;

import cmn.util.common.NullUtil;
import cmn.util.dao.CamelMap;

public class CamelMapExtractor<T> implements FieldExtractor<T>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CamelMapExtractor.class);
	
	private String[] colNames;
	
	private boolean trim;
	
	@Override
	public Object[] extract(T param) {
		List<String> values = new ArrayList<String>();
		
		CamelMap camelMap = (CamelMap) param;
		
		@SuppressWarnings("unchecked")
		Iterator<String> itr = camelMap.keySet().iterator();
		
		while(itr.hasNext()) {
			String key = itr.next();
			if (!NullUtil.isNull(colNames)) {
				if (!isContained(key)) {
					continue;
				}
			}
			String value = camelMap.get(key) != null ? String.valueOf(camelMap.get(key)) : "";
			if (this.trim) {
				values.add(value.trim());				
			}
			else {
				values.add(value);								
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Extract Values :: {}", values.toArray());
		}
		return values.toArray();
	}

	private boolean isContained(String key) {
		List<String> lists = Arrays.asList(colNames);
		if (lists.contains(key)) {
			return true;
		}
		else {
			return false;
		}
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		if (NullUtil.isNull(this.trim)) {
			this.trim = false;
		}
	}

	public void setColNames(String[] colNames) {
		this.colNames = colNames;
	}

	public void setTrim(boolean trim) {
		this.trim = trim;
	}


}
