package com.vspl.music.model.eo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.vspl.music.model.hp.HPCustomer;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.EOObjectUtil;
import com.vspl.music.util.JSONUtil;

@MappedSuperclass
@JsonSerialize
public class EOObject {

	public static EOObject createObject(Map<String, Object> map) {
		EOObject eoObject = null;
		try {
			String className = (String) map.get("className");
			
			if(className.startsWith("EO")){
			Class<?> clazz = Class.forName("com.vspl.music.model.eo." + className);
			
				eoObject = (EOObject) clazz.newInstance();
				map.remove("className");
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getValue() == null) {
						continue;
					}
					Class<?> clazzType = EOObjectUtil.entityMapWithAttrType.get(className).get(entry.getKey());
					//System.err.println("Key :::: " + entry.getKey() + "  Value ::::::" + entry.getValue());
					Object obj = EOObjectUtil.getDefaultObject(clazzType) != null ? EOObjectUtil.getDefaultObject(clazzType)
							: clazzType.newInstance();
					Field field = EOObjectUtil.entityMapWithAttrField.get(className).get(entry.getKey());
					field.setAccessible(true);
					if ((field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class))
							&& entry.getValue() != null) {
						String queryStr = (String.valueOf(entry.getValue())).replace('[', ' ').replace(']', ' ');
						String entityName = ((Class<?>) ((ParameterizedType) field.getGenericType())
								.getActualTypeArguments()[0]).getSimpleName();
						// List<Object> objList = DBServices.get("select primary_key
						// From "+ entityName+ " where primary_key in " + queryStr);
						Field relfield = EOObjectUtil.entityMapWithAttrField.get(entityName).get("primaryKey");
						relfield.setAccessible(true);
						List<Object> objList = new ArrayList<>();
						StringTokenizer st2 = new StringTokenizer(queryStr.trim(), ",");
						while (st2.hasMoreElements()) {
							Object pk = st2.nextElement();
							if (entityName.startsWith("EO")) {
								Class<?> entityNameClz = Class.forName("com.vspl.music.model.eo." + entityName);
								EOObject relEOObject = (EOObject) entityNameClz.newInstance();
								relfield.set(relEOObject, Long.parseLong(((String) pk).trim()));
								objList.add(relEOObject);
							} else {
								Class<?> entityNameClz = Class.forName("com.vspl.music.model.lk." + entityName);
								EOObject relEOObject = (EOObject) entityNameClz.newInstance();
								relfield.set(relEOObject, Long.parseLong(((String) pk).trim()));
								objList.add(relEOObject);
							}
						}
						field.set(eoObject, objList);
					} else if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
						String entityName = field.getType().getSimpleName();
						// List<Object> objList = DBServices.get("From " +
						// entityName + " where primary_key=" + entry.getValue());
						Field relfield = EOObjectUtil.entityMapWithAttrField.get(entityName).get("primaryKey");
						relfield.setAccessible(true);
						EOObject relEOObject = null;
						long pk = 0;
						if (entry.getValue() instanceof java.lang.String)
							pk = Long.parseLong((String) entry.getValue());
						if (entry.getValue() instanceof java.lang.Integer)
							pk = new Long((Integer) entry.getValue());
						if (entry.getValue() instanceof java.lang.Long)
							pk = (Long) entry.getValue();
						if (entityName.startsWith("EO") && pk > 0) {
							Class<?> entityNameClz = Class.forName("com.vspl.music.model.eo." + entityName);
							relEOObject = (EOObject) entityNameClz.newInstance();
							relfield.set(relEOObject, pk);
						} else if (pk > 0) {
							Class<?> entityNameClz = Class.forName("com.vspl.music.model.lk." + entityName);
							relEOObject = (EOObject) entityNameClz.newInstance();
							relfield.set(relEOObject, pk);
						}
						// field.set(eoObject, objList.size() == 0 ? null :
						// objList.get(0));
						field.set(eoObject, relEOObject);
					} else if (obj instanceof java.lang.String) {
						field.set(eoObject, clazzType.getMethod("valueOf", Object.class).invoke(obj, entry.getValue()));
					} else if (obj instanceof java.lang.Boolean) {
						field.set(eoObject, entry.getValue());
					} else if (obj instanceof byte[]) {
						field.set(eoObject, String.valueOf(entry.getValue()).getBytes());
					} else if (obj instanceof Double) {
						field.set(eoObject, Double.parseDouble(String.valueOf(entry.getValue())));
					} else if (obj instanceof java.lang.Long) {
						field.set(eoObject, Long.parseLong(String.valueOf(entry.getValue())));
					} else if (obj instanceof java.lang.Integer) {
						field.set(eoObject, Integer.parseInt(String.valueOf(entry.getValue())));
					} else {
						field.set(eoObject, clazzType.getMethod("valueOf", String.class).invoke(obj, entry.getValue()));
					}
				}
			}else{
	
				Class<?> clazz11 = Class.forName("com.vspl.music.model.lk." + className);
				eoObject = (EOObject) clazz11.newInstance();
				map.remove("className");
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getValue() == null) {
						continue;
					}
					System.err.println("Key :::: " + entry.getKey() + "  Value ::::::" + entry.getValue());
					Class<?> clazzType = EOObjectUtil.entityMapWithAttrType.get(className).get(entry.getKey());
					//System.err.println("Key :::: " + entry.getKey() + "  Value ::::::" + entry.getValue());
					Object obj = EOObjectUtil.getDefaultObject(clazzType) != null ? EOObjectUtil.getDefaultObject(clazzType)
							: clazzType.newInstance();
					Field field = EOObjectUtil.entityMapWithAttrField.get(className).get(entry.getKey());
					field.setAccessible(true);
					if ((field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class))
							&& entry.getValue() != null) {
						String queryStr = (String.valueOf(entry.getValue())).replace('[', ' ').replace(']', ' ');
						String entityName = ((Class<?>) ((ParameterizedType) field.getGenericType())
								.getActualTypeArguments()[0]).getSimpleName();
						// List<Object> objList = DBServices.get("select primary_key
						// From "+ entityName+ " where primary_key in " + queryStr);
						Field relfield = EOObjectUtil.entityMapWithAttrField.get(entityName).get("primaryKey");
						relfield.setAccessible(true);
						List<Object> objList = new ArrayList<>();
						StringTokenizer st2 = new StringTokenizer(queryStr.trim(), ",");
						while (st2.hasMoreElements()) {
							Object pk = st2.nextElement();
							if (entityName.startsWith("EO")) {
								Class<?> entityNameClz = Class.forName("com.vspl.music.model.eo." + entityName);
								EOObject relEOObject = (EOObject) entityNameClz.newInstance();
								relfield.set(relEOObject, Long.parseLong(((String) pk).trim()));
								objList.add(relEOObject);
							} else {
								Class<?> entityNameClz = Class.forName("com.vspl.music.model.lk." + entityName);
								EOObject relEOObject = (EOObject) entityNameClz.newInstance();
								relfield.set(relEOObject, Long.parseLong(((String) pk).trim()));
								objList.add(relEOObject);
							}
						}
						field.set(eoObject, objList);
					} else if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
						String entityName = field.getType().getSimpleName();
						// List<Object> objList = DBServices.get("From " +
						// entityName + " where primary_key=" + entry.getValue());
						Field relfield = EOObjectUtil.entityMapWithAttrField.get(entityName).get("primaryKey");
						relfield.setAccessible(true);
						EOObject relEOObject = null;
						long pk = 0;
						if (entry.getValue() instanceof java.lang.String)
							pk = Long.parseLong((String) entry.getValue());
						if (entry.getValue() instanceof java.lang.Integer)
							pk = new Long((Integer) entry.getValue());
						if (entry.getValue() instanceof java.lang.Long)
							pk = (Long) entry.getValue();
						if (entityName.startsWith("EO") && pk > 0) {
							Class<?> entityNameClz = Class.forName("com.vspl.music.model.eo." + entityName);
							relEOObject = (EOObject) entityNameClz.newInstance();
							relfield.set(relEOObject, pk);
						} else if (pk > 0) {
							Class<?> entityNameClz = Class.forName("com.vspl.music.model.lk." + entityName);
							relEOObject = (EOObject) entityNameClz.newInstance();
							relfield.set(relEOObject, pk);
						}
						// field.set(eoObject, objList.size() == 0 ? null :
						// objList.get(0));
						field.set(eoObject, relEOObject);
					} else if (obj instanceof java.lang.String) {
						field.set(eoObject, clazzType.getMethod("valueOf", Object.class).invoke(obj, entry.getValue()));
					} else if (obj instanceof java.lang.Boolean) {
						field.set(eoObject, entry.getValue());
					} else if (obj instanceof byte[]) {
						field.set(eoObject, String.valueOf(entry.getValue()).getBytes());
					} else if (obj instanceof Double) {
						field.set(eoObject, Double.parseDouble(String.valueOf(entry.getValue())));
					} else if (obj instanceof java.lang.Long) {
						field.set(eoObject, Long.parseLong(String.valueOf(entry.getValue())));
					} else if (obj instanceof java.lang.Integer) {
						field.set(eoObject, Integer.parseInt(String.valueOf(entry.getValue())));
					} else {
						field.set(eoObject, clazzType.getMethod("valueOf", String.class).invoke(obj, entry.getValue()));
					}
				
			}
			}
			
		
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return eoObject;
	}

	public void postSave() {

	}

	public void postCreate(EOObject eoObject) {
		DBServices.update(eoObject);
	}

	public static EOObject getLatestObject(String className) {
		return (EOObject) (DBServices
				.get("From " + className + " where primary_key = (select max(primaryKey) from " + className + ")"))
						.get(0);
	}
	
	public static EOObject getLatestObjectWithCondition(String className, String condition, String conditionValue) {
		return (EOObject) (DBServices
				.get("From " + className + " where primary_key = (select max(primaryKey) from " + className + " where "+condition
						+ " = '"+conditionValue+"'"))
						.get(0);
	}

	public static Long getMaxPK(String className) {
		return (Long) (DBServices.get("Select max(primaryKey) From " + className)).get(0);
	}

	public static EOObject getObjectByPK(String className, Integer pk) {
		return (EOObject) (DBServices.get("From " + className + " where primaryKey =" + pk)).get(0);
	}

	public static EOObject getObjectByPK(String className, String pk) {
		return (EOObject) (DBServices.get("From " + className + " where primaryKey =" + pk)).get(0);
	}

	public static EOObject updateObject(EOObject eoObject, Map<String, Object> map) {
		String className = eoObject.getClass().getSimpleName();
		map.remove("primaryKey");
		map.remove("className");
		try {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if (entry.getValue() == null) {
					continue;
				}
				Class<?> clazzType = EOObjectUtil.entityMapWithAttrType.get(className).get(entry.getKey());
				System.err.println("KEY   " + entry.getKey() + "    VALUE  " + entry.getValue());
				Object obj = EOObjectUtil.getDefaultObject(clazzType) != null ? EOObjectUtil.getDefaultObject(clazzType)
						: clazzType.newInstance();
				Field field = EOObjectUtil.entityMapWithAttrField.get(className).get(entry.getKey());
				field.setAccessible(true);
				if ((field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class))
						&& entry.getValue() != null) {
					String queryStr = (String.valueOf(entry.getValue())).replace('[', ' ').replace(']', ' ');
					String entityName = ((Class<?>) ((ParameterizedType) field.getGenericType())
							.getActualTypeArguments()[0]).getSimpleName();
					// List<Object> objList = DBServices.get("select primary_key
					// From "+ entityName+ " where primary_key in " + queryStr);
					Field relfield = EOObjectUtil.entityMapWithAttrField.get(entityName).get("primaryKey");
					relfield.setAccessible(true);
					List<Object> objList = new ArrayList<>();
					StringTokenizer st2 = new StringTokenizer(queryStr.trim(), ",");
					while (st2.hasMoreElements()) {
						Object pk = st2.nextElement();
						if (entityName.startsWith("EO")) {
							Class<?> entityNameClz = Class.forName("com.vspl.music.model.eo." + entityName);
							EOObject relEOObject = (EOObject) entityNameClz.newInstance();
							relfield.set(relEOObject, Long.parseLong(((String) pk).trim()));
							objList.add(relEOObject);
						} else {
							Class<?> entityNameClz = Class.forName("com.vspl.music.model.lk." + entityName);
							EOObject relEOObject = (EOObject) entityNameClz.newInstance();
							relfield.set(relEOObject, Long.parseLong(((String) pk).trim()));
							objList.add(relEOObject);
						}
					}
					field.set(eoObject, objList);
				} else if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
					String entityName = field.getType().getSimpleName();
					// List<Object> objList = DBServices.get("From " +
					// entityName + " where primary_key=" + entry.getValue());
					Field relfield = EOObjectUtil.entityMapWithAttrField.get(entityName).get("primaryKey");
					relfield.setAccessible(true);
					EOObject relEOObject = null;
					long pk = 0;
					if (entry.getValue() instanceof java.lang.String)
						pk = Long.parseLong((String) entry.getValue());
					if (entry.getValue() instanceof java.lang.Integer)
						pk = new Long((Integer) entry.getValue());
					if (entry.getValue() instanceof java.lang.Long)
						pk = (Long) entry.getValue();
					if (entityName.startsWith("EO") && pk > 0) {
						Class<?> entityNameClz = Class.forName("com.vspl.music.model.eo." + entityName);
						relEOObject = (EOObject) entityNameClz.newInstance();
						relfield.set(relEOObject, pk);
					} else if (pk > 0) {
						Class<?> entityNameClz = Class.forName("com.vspl.music.model.lk." + entityName);
						relEOObject = (EOObject) entityNameClz.newInstance();
						relfield.set(relEOObject, pk);
					}
					field.set(eoObject, relEOObject);
				} else if (obj instanceof java.lang.String) {
					field.set(eoObject, clazzType.getMethod("valueOf", Object.class).invoke(obj, entry.getValue()));
				} else if (obj instanceof java.lang.Boolean) {
					field.set(eoObject, entry.getValue());
				} else if (obj instanceof byte[]) {
					field.set(eoObject, String.valueOf(entry.getValue()).getBytes());
				} else if (obj instanceof Double) {
					field.set(eoObject, Double.parseDouble(String.valueOf(entry.getValue())));
				} else if (obj instanceof java.lang.Long) {
					field.set(eoObject, Long.parseLong(String.valueOf(entry.getValue())));
				} else if (obj instanceof java.lang.Integer) {
					field.set(eoObject, Integer.parseInt(String.valueOf(entry.getValue())));
				} else {
					field.set(eoObject, clazzType.getMethod("valueOf", String.class).invoke(obj, entry.getValue()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return eoObject;
	}
	
	public void updateUserDetail(Map<String, Object> map) {
//		List<Object> eoQualificationArray = (List<Object>) map.get("eoQualificationArray");

//		map.remove("eoQualificationArray");
		 
		HPCustomer hpUser = (HPCustomer) JSONUtil.mapToObject(map, HPCustomer.class);
		// For Address Detail

	}

	public static EOObject getObjectByPK(String className, BigInteger pk) {
		return (EOObject) (DBServices.get("From " + className + " where primaryKey =" + pk)).get(0);
	}
}
