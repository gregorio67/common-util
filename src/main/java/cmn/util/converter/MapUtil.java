package cmn.util.converter;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);

	public static Map<String, Object> merge(Map<String, Object> source, Map<String, Object> target) {

		return merge(source, target, false);
	}

	public static Map<String, Object> merge(Map<String, Object> source, Map<String, Object> target, boolean exist) {

		return merge(source, target, false, null);
	}

	public static Map<String, Object> merge(Map<String, Object> source, Map<String, Object> target, boolean exist, String[] arg) {

		if ( arg == null ){
		   	 for (@SuppressWarnings("rawtypes") Map.Entry entry : source.entrySet()) {
			    String key = (String)entry.getKey();
			    Object value = entry.getValue();
			    if ( exist ){
			    	if ( target.containsKey(key)){
			    		target.put(key, value);
			    	}

			    } else {
			    	target.put(key, value);
			    }
			}
		} else {

			for ( int i = 0; i < arg.length; i++ ){
			    if ( exist ){
			    	if ( target.containsKey(arg[i])){
			    		target.put(arg[i], source.get(arg[i]));
			    	}

			    } else {
			    	target.put(arg[i], source.get(arg[i]));
			    }
			}
		}
		return target;
	}

	public static List merge(Map source,List target) {

		return merge(source, target, false);
	}

	public static List merge(Map source,List target, boolean exist) {

		return merge(source, target, false, null);
	}

	public static List merge(Map source,List target, boolean exist, String[] arg) {

		int cnt = target.size();
		for (int i = 0; i<cnt; i++){
			Map data = (Map)target.get(i);

			data = merge( source, data, exist, arg);

			target.set(i, data);
		}

		return target;
	}


	/**
	 * Replace all keys in java.util.Map to lower case.
	 * @param map
	 * @return Converted map
	 */
	public static HashMap<String, Object> toLowerCase(HashMap<String, Object> map) {
		return (HashMap<String, Object>)toLowerCase((Map<String, Object>)map);
	}

	/**
	 * Replace all keys in java.util.Map to lower case.
	 * @param map
	 * @return Converted map
	 */
	public static Map<String, Object> toLowerCase(Map<String, Object> map) {
		Map<String, Object> mapResult = new HashMap<>();

		for (String strKey : map.keySet()) {
			mapResult.put(strKey.toLowerCase(), map.get(strKey));
		}

		return mapResult;
	}

	/**
	 * Replace all keys in java.util.Map in list to lower case.
	 * @param map
	 * @return Converted list of map
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> toLowerCase(List<Map<String, Object>> map) {
		List<Map<String, Object>> mapResult = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> mapResultRow = new HashMap<>();

		for (Map<String, Object> row : map) {
			mapResultRow.clear();
			for (String strKey : row.keySet()) {
				mapResultRow.put(strKey.toLowerCase(), row.get(strKey));
			}
			mapResult.add((Map<String, Object>) mapResultRow.clone());
		}

		return mapResult;
	}

	/**
	 * Read Request JSON Data
	 * <pre>
	 *
	 * </pre>
	 * @param request HttpServletRequest
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public static Map<String, Object> readRequestData(HttpServletRequest request) throws Exception {
		/** Get Inputstream from HttpServletRequest **/
		InputStream is = request.getInputStream();

		/** Read data from InputStream **/
		byte[] requestData = IOUtils.toByteArray(is);

		String strParam = new String(requestData);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Receive Data from ESB :: {}", strParam);
		}
		return JsonUtil.json2Map(strParam);

	}

	/**
	 *
	 *<pre>
	 * Conver Map to Value Object Object
	 *</pre>
	 * @param map
	 * @param objClass
	 * @return
	 */
	public static Object convertMapToObject(Map<String, Object> map, Object clazz) throws Exception {
		String keyAttribute = null;
		String setMethodString = "set";
		String methodString = null;
		Iterator<String> itr = map.keySet().iterator();
		Field[] fields = clazz.getClass().getDeclaredFields();
		while(itr.hasNext()){
			keyAttribute = itr.next();
			methodString = setMethodString + keyAttribute.substring(0,1).toUpperCase()+ keyAttribute.substring(1);
			for (Field field : fields) {
	            System.out.println("Field Name::" + field.getName());
	            if(keyAttribute.equals(field.getName())) {
	            	BeanUtils.setProperty(clazz, field.getName(), map.get(keyAttribute));
	            	break;
	            }
	        }
//				Method[] methods = objClass.getClass().getDeclaredMethods();
//				for(int i=0;i<=methods.length-1;i++){
//						if(methodString.equals(methods[i].getName())){
//							System.out.println("invoke : "+methodString);
//							methods[i].invoke(objClass, map.get(keyAttribute));
//							break;
//						}
//					}
		}
		return clazz;
	}

	/**
	 *
	 *<pre>
	 * Convert Value Object to Map
	 *</pre>
	 * @param obj Class
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> ConverObjectToMap(Object clazz)throws Exception {
		try {
			//Field[] fields = obj.getClass().getFields();
			//private field는 나오지 않음.
			Field[] fields = clazz.getClass().getDeclaredFields();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for(int i=0; i <= fields.length - 1; i++){
				fields[i].setAccessible(true);
				String value = BeanUtils.getProperty(clazz, fields[i].getName());
				resultMap.put(fields[i].getName(), fields[i].get(clazz));
			}
			return resultMap;
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
