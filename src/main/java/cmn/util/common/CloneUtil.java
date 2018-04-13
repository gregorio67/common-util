import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import stis.framework.vo.TestVo;

public class CloneUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloneUtil.class);
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Deep clone Object using Gson Library
	 *                When using this method, Number type is added precision 
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static <T> T copyObject(T source) throws Exception {
		
		String json = new Gson().toJson(source);
		T resultObject = new Gson().fromJson(json, new TypeToken<T>() {}.getType());
		
		return (T) (resultObject != null ? resultObject : null);
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Deep clone value object with SerializationUtils
	 *                When using this method, the value object must be implemented serializable
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T extends Serializable> T copyValueObject(T clazz) throws Exception {
		T copiedObj =  (T)SerializationUtils.clone(clazz);
		
		return copiedObj;
		
	}	
	
	public static void main(String args[]) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "kkimdoy");
		map.put("age", 10.20);
		
		Map<String, Object> copiedMap = copyObject(map);
		LOGGER.debug("copied Map :: {}", copiedMap.toString());
		
		
		List<String> list = new ArrayList<String>();
		list.add("11111");
		
		List<String> copiedList = copyObject(list);
		LOGGER.debug("copied List :: {}", copiedList.toArray());
		
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		list1.add(map);
		list1.add(copiedMap);
		
		List<Map<String, Object>> copiedList1 = copyObject(list1);
		LOGGER.debug("copied List :: {}", copiedList1.toArray());
		
		
		
		TestVo vo = new TestVo();
		vo.setAge(10);
		vo.setName("kkimdoy");
		vo.setPassword("password");
		
		TestVo vo1 = copyValueObject(vo);
		
		LOGGER.debug("{} :: {} :: {}", vo1.getName(), vo1.getPassword(), vo1.getAge());
	}
}
