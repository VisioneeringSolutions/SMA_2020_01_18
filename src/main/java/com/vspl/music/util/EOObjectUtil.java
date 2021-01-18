package com.vspl.music.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Column;

import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;

import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.VEMSSerial;
import com.vspl.music.model.hp.HPJsonEntityObject;
import com.vspl.music.model.hp.HPJsonObject;

public class EOObjectUtil {

	/*
	 * Load all Entity classes from com.vspl.nemi.model package
	 */
	public static void LoadAllEntityClassJson() {
		Reflections reflections = new Reflections("com.vspl.music.model.eo");
		Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(javax.persistence.Entity.class);
		
		reflections = new Reflections("com.vspl.music.model.lk");
		allClasses.addAll(reflections.getTypesAnnotatedWith(javax.persistence.Entity.class));
		buildData(allClasses);
	}

	private static void buildData(Set<Class<?>> allClasses) {
		createEntityMapWithClassName(allClasses);
		for (Class<?> item : allClasses) {
			Field[] fieldList = item.getDeclaredFields();
			createEntityMapWithHPObject(item, fieldList);
			createEntityMapWithAttrField(item.getSimpleName(), fieldList);
			createEntityMapWithAttrType(item.getSimpleName(), fieldList);
			createEntityMapWithAttrTypeString(item.getSimpleName(), fieldList);
		}
	}

	public static HashMap<String, Class<?>> entityMapWithClassName;

	private static void createEntityMapWithClassName(Set<Class<?>> allClasses) {
		if (entityMapWithClassName == null) {
			entityMapWithClassName = new HashMap<>();
		}
		for (Class<?> item : allClasses) {
			entityMapWithClassName.put(item.getSimpleName(), item);
		}
	}

	public static HashMap<String, HashMap<String, Class<?>>> entityMapWithAttrType;

	private static void createEntityMapWithAttrType(String simpleName, Field[] fieldList) {
		if (entityMapWithAttrType == null) {
			entityMapWithAttrType = new HashMap<>();
		}
		if (entityMapWithAttrType.get(simpleName) == null) {
			HashMap<String, Class<?>> attrTypeMap = new HashMap<>();
			for (Field field : fieldList) {
				attrTypeMap.put(field.getName(), getClassForType(field.getType().getName()));
			}
			entityMapWithAttrType.put(simpleName, attrTypeMap);
		}
	}

	public static HashMap<String, HashMap<String, String>> entityMapWithAttrTypeString;

	private static void createEntityMapWithAttrTypeString(String simpleName, Field[] fieldList) {
		if (entityMapWithAttrTypeString == null) {
			entityMapWithAttrTypeString = new HashMap<>();
		}
		if (entityMapWithAttrTypeString.get(simpleName) == null) {
			HashMap<String, String> attrTypeMap = new HashMap<>();
			for (Field field : fieldList) {
				if (field.isAnnotationPresent((Class<? extends Annotation>) VEMSSerial.class)) {
					attrTypeMap.put(field.getName(), getClassForType(field.getType().getName()).getSimpleName());
				}
			}
			entityMapWithAttrTypeString.put(simpleName, attrTypeMap);
		}
	}

	public static HashMap<String, HashMap<String, Field>> entityMapWithAttrField;

	private static void createEntityMapWithAttrField(String simpleName, Field[] fieldList) {
		if (entityMapWithAttrField == null) {
			entityMapWithAttrField = new HashMap<>();
		}
		if (entityMapWithAttrField.get(simpleName) == null) {
			HashMap<String, Field> attrFieldMap = new HashMap<>();
			for (Field field : fieldList) {
				attrFieldMap.put(field.getName(), field);
			}
			entityMapWithAttrField.put(simpleName, attrFieldMap);
		}
	}

	public static HashMap<String, ArrayList<HPJsonObject>> entityMapWithHPObject;

	private static void createEntityMapWithHPObject(Class<?> clazz, Field[] fieldList) {
		if (entityMapWithHPObject == null) {
			entityMapWithHPObject = new HashMap<>();
		}
		if (clazz.isAnnotationPresent(Resource.class)) {
			String keyName = clazz.getAnnotation(Resource.class).description();
			if (entityMapWithHPObject.get(keyName) == null) {
				ArrayList<HPJsonObject> hpJsonObjectArray = new ArrayList<>();
				for (Field field : fieldList) {
					String fieldDisplayName = field.getName();
					if (field.isAnnotationPresent(Resource.class)) {
						fieldDisplayName = field.getAnnotation(Resource.class).description();

						hpJsonObjectArray.add(new HPJsonObject(field.getName(),
								field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).name()
										: field.getName(),
								getClassForType(field.getType().getName()).getSimpleName(), fieldDisplayName));
					}
				}
				entityMapWithHPObject.put(keyName, hpJsonObjectArray);
			}
		}
	}

	private static List<HPJsonEntityObject> entityListWithHPObject;

	public static List<HPJsonEntityObject> getRuleSetEntityList() {
		if (entityListWithHPObject != null && entityListWithHPObject.size() > 0) {
			return entityListWithHPObject;
		}

		entityListWithHPObject = new ArrayList<HPJsonEntityObject>();
		Reflections reflections = new Reflections("com.vspl.music.model");
		Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(javax.persistence.Entity.class);

		for (Class<?> item : allClasses) {
			if (item.isAnnotationPresent(Resource.class)) {

				Field[] fieldList = item.getDeclaredFields();
				ArrayList<HPJsonObject> hpJsonObjectArray = new ArrayList<>();
				for (Field field : fieldList) {
					String fieldDisplayName = field.getName();
					if (field.isAnnotationPresent(Resource.class)) {
						fieldDisplayName = field.getAnnotation(Resource.class).description();
					}
					hpJsonObjectArray.add(new HPJsonObject(
							field.getName(), field.isAnnotationPresent(Column.class)
									? field.getAnnotation(Column.class).name() : field.getName(),
							((Class<?>) field.getType()).getSimpleName(), fieldDisplayName));
				}
				entityListWithHPObject.add(new HPJsonEntityObject(item.getSimpleName(),
						item.getAnnotation(Resource.class).description(), hpJsonObjectArray));
			}

		}
		return entityListWithHPObject;

	}

	private static HashMap<String, Class<?>> dataTypeClassMap;

	public static HashMap<String, Class<?>> dataTypeClassMap() {
		if (dataTypeClassMap == null) {
			dataTypeClassMap = new HashMap<>();
			dataTypeClassMap.put("int", Integer.class);
			dataTypeClassMap.put("long", Long.class);
			dataTypeClassMap.put("double", Double.class);
			dataTypeClassMap.put("float", Float.class);
			dataTypeClassMap.put("bool", Boolean.class);
			dataTypeClassMap.put("boolean", Boolean.class);
			dataTypeClassMap.put("char", Character.class);
			dataTypeClassMap.put("byte", Byte.class);
			dataTypeClassMap.put("void", Void.class);
			dataTypeClassMap.put("short", Short.class);
		}
		return dataTypeClassMap;
	}

	public static Class<?> getClassForType(String typeStr) {
		Class<?> classValue = dataTypeClassMap().get(typeStr);
		if (classValue != null) {
			return classValue;
		}
		try {
			classValue = Class.forName(typeStr);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classValue;
	}

	public static Object getDefaultObject(Class<?> clazzType) {
		if (clazzType.getSimpleName().equals("Integer")) {
			return new Integer(0);
		}
		if (clazzType.getSimpleName().equals("byte[]")) {
			return new byte[] {};
		}
		if (clazzType.getSimpleName().equals("Long")) {
			return new Long(0L);
		}
		if (clazzType.getSimpleName().equals("Float")) {
			return new Float(0.0f);
		}
		if (clazzType.getSimpleName().equals("Double")) {
			return new Double(0.0d);
		}
		if (clazzType.getSimpleName().equals("Boolean")) {
			return new Boolean(false);
		}
		if (clazzType.getSimpleName().equals("Date")) {
			return new Date(System.currentTimeMillis());
		}
		if (clazzType.getSimpleName().equals("List")) {
			return new LinkedList<>();
		}
		if (clazzType.getSimpleName().equals("Set")) {
			return new LinkedHashSet<>();
		}
		if (clazzType.getSimpleName().equals("EOObject")) {
			return new EOObject();
		}
		return null;
	}

	public static Map<String, Object> objectToMap(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();

		@SuppressWarnings("unchecked")
		Map<String, Object> objectAsMap = objectMapper.convertValue(obj, Map.class);
		return objectAsMap;
	}
}
