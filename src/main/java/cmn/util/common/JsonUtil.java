package kics.framework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kics.framework.exception.KicsRuntimeException;


/**
 * @Project : KICS 고도화 및 고유정보 식별정보 암호화
 * @Class : JsonUtil.java
 * @Description : This class is for JSON manipulation. 
 * @Author : 
 * @Since : 2017. 6. 20.
 *
 * @Copyright (c) 2016 KICS All rights reserved.
 *----------------------------------------------------------
 * Modification Information
 *----------------------------------------------------------
 * 날짜            수정자             변경사유 
 *----------------------------------------------------------
 *2017. 6. 20.           최초작성
 *----------------------------------------------------------
 */

public class JsonUtil {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
	
	/** Character encoding **/
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	/** ObjectMapper **/
	private static ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * This method is for parsing JSON data to List
	 * <pre>
	 *
	 * </pre>
	 * @param jsonData
	 * @return
	 * @throws Exception
	 */
	public static <K, V> List<Map<K, V>> json2List(String jsonData) throws Exception {
		return json2List(jsonData, DEFAULT_ENCODING);
	}

	/**
	 * This method is for parsing JSON data to List
	 * @param jsonData
	 * @return List<Map<String, String>> 
	 * @throws Exception
	 */
	public static <K, V> List<Map<K, V>> json2List(String jsonData, String encoding) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("jsonData : " + jsonData);
		}
		byte[] mapData = jsonData.getBytes(encoding);
		
		List<Map<K, V>> lists = new ArrayList<Map<K, V>>();
		
		try {
			lists = objectMapper.readValue(mapData,  new TypeReference<List<Map<String, Object>>>() {});			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return lists;
	}

	/**
	 * This is for converting json data to Map 
	 * <pre>
	 *
	 * </pre>
	 * @param jsonData
	 * @return
	 * @throws Exception
	 */
	public static <K, V> Map<K, V> json2Map(String jsonData) throws Exception {
		return json2Map(jsonData, DEFAULT_ENCODING);
	}
	
	/**
	 * This is for converting JSON data to HashMap.
	 * @param jsonData
	 * @return Map<String, String>
	 * @throws Exception
	 */
	public static <K, V> Map<K, V> json2Map(String jsonData, String encoding) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("jsonData : " + jsonData);
		}
		
		byte[] mapData = jsonData.getBytes(encoding);
		
		Map<K, V> map = new HashMap<K, V>();
		
		try {
			map = objectMapper.readValue(mapData,  new TypeReference<Map<String, Object>>() {});			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * This method generate JSON message from Map
	 * @param dataMap Data in the Map to convert to JSON Data
	 * @return
	 * @throws Exception
	 */
	
	public static <K,V> String map2Json(Map<K, V> dataMap) throws Exception {
		String jsonData = null;
		
		try {
			jsonData = objectMapper.writeValueAsString(dataMap);			
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new KicsRuntimeException(e.getMessage());
		}
		
		return jsonData;
	}
	
	/**
	 * 
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	public static <E> String list2Json(List<E> dataList) throws Exception {
		String jsonData = null;
		
		try {
			jsonData = objectMapper.writeValueAsString(dataList);			
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new KicsRuntimeException(e.getMessage());
		}
		
		return jsonData;
	}
}
