import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import taxris.framework.util.NullUtil;
import taxris.framework.util.SecurityUtil;


public class XSSIntercepter {

	//LOGGER 설정
//	private static final Logger LOGGER = LoggerFactory.getLogger(XSSIntercepter.class);
	private static final Logger LOGGER = LogManager.getLogger(XSSIntercepter.class);	
	/** XSS skip parameters **/
	private List<String> skipParams;
	
	
	public void setSkipParams(List<String> skipParams) {
		this.skipParams = skipParams;
	}


	@SuppressWarnings("unchecked")
	public Map<String, Object> cleanXSS(Map<String, Object> map) throws Exception {
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		Iterator<?> itr = map.keySet().iterator();
		
		while(itr.hasNext()) {
			String key = (String)itr.next();
			
			if (map.get(key) instanceof Map) {
				retMap.put(key, xssCleanMapData((Map<String, Object>)map.get(key)));
			}
			else if (map.get(key) instanceof List) {
				retMap.put(key, xssCleanListData((List<?>)map.get(key)));
			}
		}
		
		return retMap;
	}

	
	/**
	 * XXS Clean all map 
	 * <pre>
	 *
	 * </pre>
	 * @param map Map<String, Object>
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	private Map<String, Object> xssCleanMapData(Map<String, Object> map) throws Exception {
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		Iterator<String> itr = map.keySet().iterator();
		
		while(itr.hasNext()) {
			String key = itr.next();
			
			if (map.get(key) instanceof List) {
				if (checkSkipParam(key)) {
					retMap.put(key, xssCleanListData((List<?>)map.get(key)));					
				}
				else {
					retMap.put(key, (List<?>)map.get(key));										
				}
			}
			else {
				if (checkSkipParam(key)) {
					retMap.put(key, map.get(key));										
				}
				else {
					retMap.put(key, SecurityUtil.escape(String.valueOf(map.get(key))));					
				}
			}
		}
		
		return retMap;
	}
	
	/**
	 * XSS clean all list
	 * <pre>
	 *
	 * </pre>
	 * @param list List<?>
	 * @return List<Object>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<Object> xssCleanListData(List<?> list) throws Exception {
		List<Object> retList = new ArrayList<Object>();
		int size = list.size();
		
		for(int i = 0; i < size; i++) {
			if (list.get(i) instanceof Map) {
				retList.add(xssCleanMapData((Map<String, Object>)list.get(i)));
			}
			else {
				retList.add(SecurityUtil.escape(String.valueOf(list.get(i))));				
			}
		}
		return retList;
	}
	
	/**
	 * Check skip parameters
	 * <pre>
	 *
	 * </pre>
	 * @param key String
	 * @return boolean
	 * @throws Exception
	 */
	private boolean checkSkipParam(String key) throws Exception {
		boolean isSkip = false;
		/** Check skip parameter NULL **/
		if (NullUtil.isNull(skipParams)) {
			return false;
		}
		
		for (String skipParam : skipParams) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Parameter :: {} Skip Parameters :: {}", key, skipParam);
			}
			if (skipParam.equals(key)) {
				return true;
			}
		}
		return isSkip;
	}
}

