	
	mport java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stis.framework.converter.JsonUtil;
import stis.framework.vo.TestVo;


public class MapUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String toStringMap(Map map) {
		StringBuffer sb = new StringBuffer();
		sb.append("");
		
		if (map != null && map.size() > 0) {
			Set<String> keySet = map.keySet();
			
			Iterator<String> iter = keySet.iterator();
			
			int cnt = 1;
			
			while (iter.hasNext()) {
				String key = iter.next();
				Object value = map.get(key);
				
				sb.append(cnt + " : " + "Column Name=" + key + ", " 
						+ "Value=" + value.toString() 
						+ ", DataType=" + value.getClass().getName() 
						+ "\n");
				
				cnt++;
			}
		} else {
			sb.append("정보가 없습니다.");
		}
		
		return sb.toString();
	}
	
	/**
	 *  

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map setMapToMap(Map sourceMap) throws Exception {
		Map<String, String> targetMap = new HashMap<String, String>();
		
		if (sourceMap != null && sourceMap.size() > 0) {
			Set<String> keySet = sourceMap.keySet();
			
			Iterator<String> iter = keySet.iterator();
			
			while (iter.hasNext()) {
				String key = iter.next();
				Object value = sourceMap.get(key);
				
				targetMap.put(key, value.toString());
			}
		}
		
		return targetMap != null && targetMap.size() > 0 ? targetMap : null;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Convert map to value object
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param map source map
	 * @param clazz target value object class
	 * @return value object
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T map2Object(Map<String, Object> map, Class<?> clazz) throws Exception {
		return (T) map2Object(map, clazz.newInstance());
	}

	/**
	 * 
	 *<pre>
	 * 1.Description: Convert map to value object
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param map source map
	 * @param vo target value object
	 * @return value object
	 * @throws Exception
	 */
	public static <T> T map2Object(Map<String, Object> map, T vo) throws Exception {
		
		Set<Entry<String, Object>> mapSet = map.entrySet();
		Iterator<Entry<String, Object>> itr = mapSet.iterator();
		
		while(itr.hasNext()) {
			String key = itr.next().getKey();
			if (key.contains("serialVersionUID")) 
				continue;
			FieldUtils.writeField(vo, key, map.get(key), true);
		}
		
		return (T) vo;
	}

	/**
	 *
	 *<pre>
	 * Convert Value Object to Map
	 *</pre>
	 * @param obj Class
	 * @return Map<String, Object>
	 */
	public static<T> Map<String, Object> object2Map(T clazz)throws Exception {
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			 Field[] fields = FieldUtils.getAllFields(clazz.getClass());
			   for (Field field : fields) {
				 	String name =field.getName();
					Object object = FieldUtils.readField(clazz, name, true);
					retMap.put(name, object);
			   }			 
		}
		catch(Exception ex) {
		}
		return retMap;
	}
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Merge source map to target map
	 *                if target map is null, create target map;
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param sourceMap
	 * @param targetMap
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> mergeMap(Map<String, Object> sourceMap,  Map<String, Object> targetMap) throws Exception {
		return mergeMap(sourceMap, targetMap, null);
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Merge source map to target map
	 *                if target map is null, create target map;
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param sourceMap source map
	 * @param targetMap target map
	 * @param args key name of source map for merging to target map
	 * @return map
	 * @throws Exception
	 */
	public static Map<String, Object> mergeMap(Map<String, Object> sourceMap,  Map<String, Object> targetMap, String[] args) throws Exception {
		if (targetMap == null) {
			targetMap = new HashMap<String, Object>();
		}
		
		Set<Entry<String, Object>> sourceSet = sourceMap.entrySet();
		Iterator<Entry<String, Object>> sourceItr = sourceSet.iterator();
		
		
		/** Add data from sourcMap to targetMap **/
		while(sourceItr.hasNext()) {
			String key = sourceItr.next().getKey();
			if (args != null) {
				for (String arg : args) {
					if (key.equals(arg)) {
						targetMap.put(key, sourceMap.get(key));										
					}
				}
			}
			else {
				targetMap.put(key, sourceMap.get(key));				
			}
		}
		return targetMap;
	}	
	/**
	 * 
	 *<pre>
	 * 1.Description: Convert HttpRequest payload to Map
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param request http servlet request
	 * @return map 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> httpRequest2Map(HttpServletRequest request) throws Exception {
		/** Get Input stream from HttpServletRequest **/
		InputStream is = request.getInputStream();

		/** Read data from InputStream **/
		byte[] requestData = IOUtils.toByteArray(is);
		
		String strParam = requestData != null ? new String(requestData) : null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Payload Data :: {}", strParam);
		}
		return (Map<String, Object>) (strParam != null ? JsonUtil.json2Map(strParam) : new HashMap<String, Object>());

	}
	
	
	public static void main(String args[]) throws Exception {
	   TestVo vo = new TestVo();
	   vo.setAge(10);
	   vo.setName("kkimdoy");
	   vo.setPassword("aaaaa");
	   
	   Map<String, Object> map = object2Map(vo);
	   LOGGER.debug("map :: {}", map);
	   
	   TestVo vo1 = new TestVo();
	   map2Object(map, vo1);
	   LOGGER.debug("name :: {}", vo1.getName());

	   
	   TestVo vo2 = map2Object(map, TestVo.class);
	   LOGGER.debug("name :: {}", vo2.getName());

	}
}
