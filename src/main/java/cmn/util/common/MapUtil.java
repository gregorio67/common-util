import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	 * 1.Description: Copy map
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> copyMap(Map<String, Object> map) throws Exception {
		
		String json = new Gson().toJson(map);
		Map<String, Object> resultMap = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("orignal Map :: {}", map.toString());
			LOGGER.debug("copied Map :: {}", resultMap.toString());
			
		}
		
		return resultMap != null ? resultMap : null;
	}
	
	public static <T extends Serializable> T copyObject(T clazz) throws Exception {
		T copiedObj =  (T)SerializationUtils.clone(clazz);
		
		return copiedObj;
		
	}	
	
	public static void main(String args[]) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "kkimdoy");
		map.put("age", 10);
		
		copyMap(map);
		
		
		TestVo vo = new TestVo();
		vo.setAge(10);
		vo.setName("kkimdoy");
		vo.setPassword("password");
		
		TestVo vo1 = copyObject(vo);
		
		LOGGER.debug("{} :: {} :: {}", vo1.getName(), vo1.getPassword(), vo1.getAge());
	}
}
