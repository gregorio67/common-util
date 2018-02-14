package cmn.util.dao;

import org.apache.commons.collections.map.ListOrderedMap;

import cmn.util.converter.CamelUtil;

public class CamelMap extends ListOrderedMap {

	private static final long serialVersionUID = -8644338222761233955L;
	
	public Object put(Object key, Object value) {
		return super.put(CamelUtil.convert2CamelCase((String)key), value);
	}
}
