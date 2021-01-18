package com.vspl.music.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.print.DocFlavor.STRING;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vspl.music.model.eo.EOObject;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.VSPLUtil;

public class DBServices {

	public static void create(Object obj) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		session.save(obj);
		transaction.commit();
		session.close();
	}

	public static void update(Object obj) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		session.update(obj);
		transaction.commit();
		session.close();
	}

	public static List<Object> get(String queryStr) {
		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		for (Object obj : session.createQuery(queryStr).list()) {
			resultSet.add(obj);
		}
		transaction.commit();
		session.close();
		return resultSet;
	}

	public static List<Object> getLimitData(String queryStr, int Limitsize) {

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		Query qry = session.createQuery(queryStr);
		qry.setMaxResults(Limitsize);
		for (Object obj : qry.list()) {
			resultSet.add(obj);
		}
		transaction.commit();
		session.close();
		return resultSet;
	}

	public static void updateNativeQueryResult(String queryStr) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session.createQuery(queryStr);
		query.executeUpdate();
		transaction.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	public static List<HashMap<String, Object>> getNativeQueryResult(String queryStr) {
		List<HashMap<String, Object>> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		SQLQuery query = session.createSQLQuery(queryStr);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		for (Object obj : query.list()) {
			HashMap<String, Object> map = (HashMap<String, Object>) obj;
			resultSet.add(map);
		}
		transaction.commit();
		session.close();
		return resultSet;
	}

	public static Object checkObjectExists(String className, String email, String phone) {
		String qryStr = "FROM " + className + " u WHERE u.email = '" + email + "' OR u.phone = '" + phone + "'";
		List<Object> list = DBServices.get(qryStr);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}
	
	public static List<Object> checkPhoneExists(String className,String phone) {
		String qry = "FROM " + className + " u WHERE u.phone = '" + phone +"' AND isActive = true";
		List<Object> list = DBServices.get(qry);
		return (list != null && list.size() > 0) ? list : null;
	}
	
	public static List<Object> checkUserNameExists(String userName) {
		String qry = "FROM EOLoginDetails u WHERE u.userName = '" + userName +"' AND isActive = true";
		List<Object> list = DBServices.get(qry);
		return (list != null && list.size() > 0) ? list : null;
	}
	
	public static List<Object> checkBatchExists(String className,String batchPk) {
		String qry = "FROM " + className + " u WHERE u.eoBatch = '" + batchPk + "'"+" AND isActive = true";
		List<Object> list = DBServices.get(qry);
		return (list != null && list.size() > 0) ? list : null;
	}
	
	public static List<Object> checkCourse(String className,String coursePk) {
		String qry = "FROM " + className + " u WHERE u.eoCourses = '" + coursePk + "'"+" AND isActive = true";
		List<Object> list = DBServices.get(qry);
		return (list != null && list.size() > 0) ? list : null;
	}
	
	public static List<HashMap<String, Object>> getMaxRunningNo(String className) {
		String str = "SELECT MAX(running_no) as runningno FROM "+className;
		List<HashMap<String, Object>> list = DBServices.getNativeQueryResult(str);
		return (list != null && list.size() > 0) ? list : null;
	}

	public static HashMap<String, Object> getMaxPrimaryKey(String className) {
		String qryStr = "SELECT max(primary_key) FROM " + className;
		List<HashMap<String, Object>> list = DBServices.getNativeQueryResult(qryStr);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}
	
	public static EOObject getObjectBycolumnName(String className,String columnName, String value) {
		String qry = "FROM " + className + " u WHERE u."+columnName +" = '"+value+"' AND isActive = true ";
		//List<Object> list = (EOObject)DBServices.get(qry);
		return ((EOObject)(DBServices.get(qry)).get(0) != null ? (EOObject)(DBServices.get(qry)).get(0) : null);
	}
	
	public static EOObject checkMapping (String className,String Name1, String pk1,String Name2, String pk2) {
		String qry = "FROM " + className + " u WHERE u."+Name1 +" = "+pk1+" AND u."+Name2+" = "+pk2;
		return ((EOObject)(DBServices.get(qry)).get(0) != null ? (EOObject)(DBServices.get(qry)).get(0) : null);
	}
	
	public static Integer changePassword(HashMap<String, String> map) {
		String usrName = map.get("usrName");
		String password = map.get("password");
		boolean isEmail = (usrName.indexOf('@') != -1) ? true : false;
		String qryStr = "";
		   
			if (isEmail)
				qryStr = "Update EOManagementUser set isUserVerified = true, password ='" + VSPLUtil.md5Encrypt(password)
						+ "' where upper(email)= '" + usrName.toUpperCase() + "'";
			else
				qryStr = "Update EOManagementUser set isUserVerified = true, password ='" + VSPLUtil.md5Encrypt(password)
						+ "' where phone= '" + usrName.toUpperCase() + "'";
		  

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session.createQuery(qryStr);
		int rowsAffected = query.executeUpdate();
		transaction.commit();
		session.close();
		return rowsAffected;
	}
	
	public static Integer resetPassword(HashMap<String, String> map) {
		String usrName = map.get("usrName");
		String password = map.get("password");
		boolean isEmail = (usrName.indexOf('@') != -1) ? true : false;
		String qryStr = "";
		   
			if (isEmail)
				qryStr = "Update EOManagementUser set isUserVerified = true, password ='" + VSPLUtil.md5Encrypt(password)
						+ "' where upper(email)= '" + usrName.toUpperCase() + "'";
			else
				qryStr = "Update EOManagementUser set isUserVerified = true, password ='" + VSPLUtil.md5Encrypt(password)
						+ "' where phone= '" + usrName.toUpperCase() + "'";
		  

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session.createQuery(qryStr);
		int rowsAffected = query.executeUpdate();
		transaction.commit();
		session.close();
		return rowsAffected;
	}
	
	


}
