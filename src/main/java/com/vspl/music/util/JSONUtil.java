package com.vspl.music.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtil {
	private static ObjectMapper mapperObj = new ObjectMapper();

	/*
	 * TO DO : Caching of mapperObj left
	 */

	public static String mapToJson(Map<String, Object> map) {
		String jsonData = "";
		try {
			jsonData = mapperObj.writeValueAsString(map);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	public static String mapToJsonStr(HashMap<String, String> map) {
		String jsonData = "";
		try {
			jsonData = mapperObj.writeValueAsString(map);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	public static String objectToJson(Object object) {
		String jsonData = "";
		try {
			jsonData = mapperObj.writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	public static String mapListToJson(List<HashMap<String, String>> map) {
		String jsonData = "";
		try {
			jsonData = mapperObj.writeValueAsString(map);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	public static String mapObjectListToJson(List<Object> map) {
		String jsonData = "";
		try {
			jsonData = mapperObj.writeValueAsString(map);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	public static Object jsonToObject(String jsonData, Class<?> clazz) {
		try {
			return mapperObj.readValue(jsonData, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object mapToObject(Map<String, Object> map, Class<?> clazz) {
		return jsonToObject(mapToJson(map), clazz);
	}

	/*
	 * public static JSONArray jsonArrayFromString(String jsonString) {
	 * JSONParser parser = new JSONParser(); try{ Object obj =
	 * parser.parse(jsonString); JSONArray array = (JSONArray)obj; return array;
	 * }catch(org.json.simple.parser.ParseException pe){ pe.printStackTrace(); }
	 * return null; }
	 */
}
