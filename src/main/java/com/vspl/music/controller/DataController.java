package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.model.bean.EOTeacherHomeWorkBean;
import com.vspl.music.model.eo.EOCompetition;
import com.vspl.music.model.eo.EOCompetitionMapping;
import com.vspl.music.model.eo.EOImage;
import com.vspl.music.model.eo.EOLoginDetails;
import com.vspl.music.model.eo.EOMasterStudentCriteria;
import com.vspl.music.model.eo.EONews;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOQueryForm;
import com.vspl.music.model.eo.EOStudentBatch;
import com.vspl.music.model.eo.EOStudentBatchMapping;
import com.vspl.music.model.eo.EOStudentCriteria;
import com.vspl.music.model.eo.EOStudentRating;
import com.vspl.music.model.eo.EOStudentUser;
import com.vspl.music.model.eo.EOTeacherBatch;
import com.vspl.music.model.eo.EOTeacherUser;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.JSONUtil;

@Path("/ajax")
public class DataController {

	public static Logger logger = LoggerFactory.getLogger(DataController.class);

	@POST
	@Path("/createObject")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createObject(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DBServices.create(EOObject.createObject(map));
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getObject")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjectFromDB(HashMap<String, String> map) {
		String className = map.get("objName");
		List<Object> objList = DBServices.get("From " + className + " ORDER BY PRIMARY_KEY");
		return Response.status(200).entity(JSONUtil.objectToJson(objList)).build();
	}

	@POST
	@Path("/updateObject")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateObject(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String className = (String) map.get("className");
		String pk = (String) map.get("primaryKey");

		EOObject eoObject = EOObject.updateObject(EOObject.getObjectByPK(className, pk), map);
		DBServices.update(eoObject);

		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/createImgObject")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createImgObject(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EOImage eoImage = EOImage.createEO(map);
		EOImage responseImgObj = new EOImage();
		responseImgObj.primaryKey = eoImage.primaryKey;
		return Response.status(201).entity(JSONUtil.objectToJson(responseImgObj)).build();
	}
	
	
	@POST
	@Path("/updateImgObject")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateImgObject(Map<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String className = String.valueOf(map.get("className"));
		Integer pk = (Integer) map.get("primaryKey");
		if (pk == null) {
			EOImage eoImage = EOImage.createEO(map);
			EOImage responseImgObj = new EOImage();
			responseImgObj.primaryKey = eoImage.primaryKey;
			return Response.status(201).entity(JSONUtil.objectToJson(responseImgObj)).build();
		}
		EOImage eoObject = (EOImage) EOObject.getObjectByPK(className, pk);
		eoObject.update(map);
		EOImage responseImgObj = new EOImage();
		responseImgObj.primaryKey = eoObject.primaryKey;
		return Response.status(201).entity(JSONUtil.objectToJson(responseImgObj)).build();
	}

	// for teacher rating and feedback--------------------------:: //nikita----

	@POST
	@Path("/getBatchListForTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBatchListForTeacher(Map<String, Object> map) throws ParseException {
		String dateFrom = (String) map.get("dateFrom");
		String dateTo = (String) map.get("dateTo");
		String userPk = (String) map.get("userPk");

		String qry = "SELECT distinct ds.eobatch_primary_key,eob.batch_name "
				+ " FROM eodefined_slot ds INNER JOIN eobatch eob ON ds.eobatch_primary_key =eob.primary_key "
				+ " Where ds.date BETWEEN '" + dateFrom + "' AND '" + dateTo + "' "
				+ " and ds.eoteacheruser_primary_key='" + userPk + "' ORDER BY ds.eobatch_primary_key,eob.batch_name ";

		List<HashMap<String, Object>> objList = DBServices.getNativeQueryResult(qry);

		return Response.status(201).entity(JSONUtil.objectToJson(objList)).build();
	}
	// end--------------

	@POST
	@Path("/getDateAndTimeForBatch")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDateAndTimeForBatch(Map<String, Object> map) throws ParseException {
		String userPk = (String) map.get("userPk");
		String batchPk = (String) map.get("batchPk");
		String qry = "Select ds.primary_key, ds.date,ds.month, ds.year, ds.start_time, ds.end_time, ds.eobatch_primary_key "
				+ " FROM eodefined_slot ds " + " WHERE  ds.eoteacheruser_primary_key='" + userPk
				+ "' and  ds.eobatch_primary_key='" + batchPk + "'ORDER BY ds.start_time,ds.date ";
		List<HashMap<String, Object>> objList = DBServices.getNativeQueryResult(qry);
		return Response.status(201).entity(JSONUtil.objectToJson(objList)).build();

	}

	// student list for teacher batch nikita----------

	@POST
	@Path("/getStudentListForTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentListForTeacher(Map<String, Object> map) throws ParseException {
		String batchPk = (String) map.get("batchPk");
		String slotPk = (String) map.get("slotPk");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		if (map.get("batchPk") != null) {

		}
		for (Object obj : session
				.createQuery("FROM EOStudentBatch WHERE eobatch_primary_key =" + batchPk + " and isActive = true")
				.list()) {
			resultSet.add(obj);
		}

		List<HashMap<String, Object>> finaList = new ArrayList<>();
		for (Object obj : resultSet) {
			HashMap<String, Object> studentBatchList = new HashMap<>();
			EOStudentBatch eoStudentBatch = (EOStudentBatch) obj;

			List<String> pkObj = (List<String>) JSONUtil.jsonToObject(eoStudentBatch.studentsPk, List.class);

			// int count = 0;
			for (String str : pkObj) {

				String stdPk = str;

				String query = "SELECT primary_key , CONCAT(first_name, ' ',last_name) as stdfullname FROM eostudent_user"
						+ " WHERE is_active = true AND primary_key = " + stdPk;

				HashMap<String, Object> tempMap = DBServices.getNativeQueryResult(query).get(0);
				HashMap<String, Object> finalMap = new HashMap<>();
				finalMap.put("primaryKey", tempMap.get("primary_key"));
				finalMap.put("studentfullName", tempMap.get("stdfullname"));
				finalMap.put("batchPk", batchPk);
				finalMap.put("slotPk", slotPk);

				finaList.add(finalMap);
			}

		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(finaList)).build();
	}
	// end--------------

	// nikita-------------- feedback

	@POST
	@Path("/createFeedbackByTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createFeedbackByTeacher(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String className = (String) map.get("className");
		String homeWork = (String) map.get("homeWork");
		String improvementComments = (String) map.get("improvementComments");
		String comments = (String) map.get("comments");
		String eoBatch = (String) map.get("eoBatch");
		String eoDefinedSlot = (String) map.get("eoDefinedSlot");
		List<Object> eoAttachments = (List<Object>) map.get("eoAttachmentArray");
		map.remove("eoAttachmentArray");

		if(map.get("primaryKey") == null) {
		
		EOImage eoImg = null;
		if (map.get("eoImage") != null) {
			eoImg = (EOImage) EOObject.getObjectByPK("EOImage", (Integer) map.get("eoImage"));
		}
		map.put("createdDate", DateUtil.formatedCurrentDate());
		DBServices.create(EOObject.createObject(map));
		EOTeacherBatch eoTeacherBatch = (EOTeacherBatch) EOObject.getLatestObject(className);
		if (eoAttachments != null && eoAttachments.size() > 0) {
			for (Object attchObj : eoAttachments) {
				Map<String, Object> attchMap = (Map<String, Object>) attchObj;
				EOImage attachment = EOImage.createEO(attchMap);
				eoTeacherBatch.eoAttachmentArray.add(attachment);
				attachment.headerPk = eoTeacherBatch.primaryKey;
				attachment.postCreate(attachment);
			}
			DBServices.update(eoTeacherBatch);
		}
		
		}
		else{
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			EOObject eoObject = EOObject.getObjectByPK(className, (Integer) map.get("primaryKey"));
			DBServices.update(EOObject.updateObject(eoObject, map));	
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	// for ratingss-------- nikitaaa
	@POST
	@Path("/getAttributeForRating")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAttributeForRating(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String batchPk = (String)map.get("batchPk");
		String studPk = (String) map.get("studPk");
		String slotPk = (String)map.get("slotPk");
		String teacherPk = (String)map.get("teacherPk");
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		
		String qry = "FROM EOStudentRating where eoDefinedSlot = "+slotPk+" "
				+ "and eoBatch.primaryKey = "+batchPk + " and eoTeacherUser.primaryKey = "+teacherPk
						+ "and eoStudentUser.primaryKey = "+studPk;
		
		List<Object> objList = DBServices.get(qry);
		if(objList != null && objList.size()> 0 ){
			EOStudentRating eoStudentRating = (EOStudentRating)objList.get(0);
			for(Object obj : eoStudentRating.eoStudentCriteria){
				EOStudentCriteria eoStudentCriteria = (EOStudentCriteria)obj;
				HashMap<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("primaryKey", eoStudentCriteria.primaryKey);
				tmpMap.put("criteria", eoStudentCriteria.eoMasterStudentCriteria.criteria);
				tmpMap.put("maxRating", eoStudentCriteria.eoMasterStudentCriteria.maxRating);
				tmpMap.put("optedRating", Double.toString(eoStudentCriteria.optedRating));   /*convert double to string */
				resultList.add(tmpMap);
			}
			
			
		}else{
			qry = "FROM EOMasterStudentCriteria where is_active = true";
			List<Object> criteriaList = DBServices.get(qry);
			if(criteriaList != null && criteriaList.size() > 0){
				for(Object obj : criteriaList){
					EOMasterStudentCriteria eoMaster = (EOMasterStudentCriteria)obj;
					HashMap<String, Object> tmpMap = new HashMap<>();
					tmpMap.put("eoMasterStudentCriteria", eoMaster.primaryKey);
					tmpMap.put("criteria", eoMaster.criteria);
					tmpMap.put("maxRating", eoMaster.maxRating);
					
					resultList.add(tmpMap);
				}
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(resultList)).build();
	}

	@POST
	@Path("/createRatings")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRatings(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		
		List<HashMap<String, Object>> criteriaList = (List<HashMap<String, Object>>)map.get("criteriaList");
		HashMap<String, Object> ratingMap = (HashMap<String, Object>)map.get("ratingMap");
		
		DBServices.create(EOObject.createObject(ratingMap));
		EOStudentRating eoStudentRating = (EOStudentRating)EOObject.getLatestObject("EOStudentRating");
		
		for(HashMap<String, Object> criteriaMap :  criteriaList){
			criteriaMap.put("eoStudentRating", eoStudentRating.primaryKey);
			DBServices.create(EOObject.createObject(criteriaMap));
		}
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getStudentRating")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentRating(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String stdPk = (String) map.get("stdPk");

		String str = "SELECT primary_key, eodefinedslot_primary_key, eobatch_primary_key, opted_rating, "
				+ " eomasterstudentcriteria_primary_key, eostudentuser_primary_key, " + " eoteacheruser_primary_key"
				+ " FROM eostudent_criteria WHERE eostudentuser_primary_key = " + stdPk;

		List<HashMap<String, Object>> objList = DBServices.getNativeQueryResult(str);

		List<Map<String, Object>> ratingList = new ArrayList<>();
		for (HashMap<String, Object> o : objList) {
			Map<String, Object> stdMap = new HashMap<>();
			stdMap.put("primaryKey", o.get("primary_key"));
			stdMap.put("batchPk", o.get("eobatch_primary_key"));
			stdMap.put("optedRating", o.get("opted_rating"));
			stdMap.put("eoMasterStudentCriteria", o.get("eomasterstudentcriteria_primary_key"));
			stdMap.put("eoStudentUser", o.get("eostudentuser_primary_key"));
			stdMap.put("eoTeacherUser", o.get("eoteacheruser_primary_key"));
			stdMap.put("eodefinedslot_primary_key", o.get("eodefinedslot_primary_key"));
			stdMap.put("stdPk", stdPk);
			ratingList.add(stdMap);
		}

		Map<String, Object> finalMap = new HashMap<>();
		finalMap.put("ratingList", ratingList);
		return Response.status(201).entity(JSONUtil.objectToJson(finalMap)).build();
	}

	@POST
	@Path("/deleteObject")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteObject(HashMap<String, Object> map) {

		String primaryKey = (String) map.get("primaryKey");
		String className = (String) map.get("className");

		String Query = "Update " + className + " set isActive = false where primaryKey = " + primaryKey;
		DBServices.updateNativeQueryResult(Query);

		return Response.status(201).entity(JSONUtil.objectToJson("success")).build();
	}
	
	@POST
	@Path("/deleteImageForNews")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteImageForNews(HashMap<String, Object> map) {

		String primaryKey = (String) map.get("primaryKey");
		String className = (String) map.get("className");

		String Qry = "Update " + className + " set isActive = false where primaryKey = " + primaryKey;

		DBServices.updateNativeQueryResult(Qry);

		return Response.status(201).entity(JSONUtil.objectToJson("success")).build();
	}
	
	
	@POST
	@Path("/getHomeWrkList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getHomeWrkList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String eoBatch =(String)map.get("eoBatch");
		String eoDefinedSlot =(String)map.get("eoDefinedSlot");
		String userPk = (String) map.get("userPk");
		
		String qry = "FROM EOTeacherBatch where teacherPk = '"+userPk+"' and eoBatch = '"+eoBatch+ "' and eoDefinedSlot="+eoDefinedSlot;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		List<EOTeacherHomeWorkBean> beanList = new ArrayList<>();
		for (Object obj : session.createQuery(qry).list()) {
			EOTeacherBatch eoTeacherBatch = (EOTeacherBatch) obj;
			EOTeacherHomeWorkBean eoTeacherHomeWorkBean = new EOTeacherHomeWorkBean();
			eoTeacherHomeWorkBean.primaryKey = eoTeacherBatch.primaryKey;
			eoTeacherHomeWorkBean.comments = eoTeacherBatch.comments;
			eoTeacherHomeWorkBean.homeWork = eoTeacherBatch.homeWork;
			eoTeacherHomeWorkBean.improvementComments = eoTeacherBatch.improvementComments;
			if (eoTeacherBatch.eoAttachmentArray != null && eoTeacherBatch.eoAttachmentArray.size() > 0) {
				for (EOImage eoImage : eoTeacherBatch.eoAttachmentArray) {
					if (eoImage.isActive) {
						eoTeacherHomeWorkBean.eoAttachmentArray.add(eoImage);
						//eoTeacherBean.logo = eoTeacherBatch.eoAttachmentArray.get(0).imageUrl;
					}
				}
			}
			beanList.add(eoTeacherHomeWorkBean);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(beanList)).build();
	}
	
	
	@POST
	@Path("/getHomeWorkData")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getHomeWorkData(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String pk = (String) map.get("eoHomWrkPK");
		
		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		for (Object obj : session
				.createQuery("From EOTeacherBatch WHERE primaryKey=" + pk)
				.list()) {
			resultSet.add(obj);
		}
		
		
		HashMap<String, Object> hmeWrkList = new HashMap<>();
		for(Object obj : resultSet) {
			EOTeacherBatch eoTeacherBatch = (EOTeacherBatch) obj;
			
			hmeWrkList.put("primaryKey", eoTeacherBatch.primaryKey);
			hmeWrkList.put("comments", eoTeacherBatch.comments);
			hmeWrkList.put("homeWork", eoTeacherBatch.homeWork);
			hmeWrkList.put("improvementComments", eoTeacherBatch.improvementComments);
			hmeWrkList.put("teacherPk", eoTeacherBatch.teacherPk);
			hmeWrkList.put("eoBatch", eoTeacherBatch.eoBatch);
			hmeWrkList.put("eoDefinedSlot", eoTeacherBatch.eoDefinedSlot);
			
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(hmeWrkList)).build();	
	}


	/* For News------------------------------------------------ */

	@POST
	@Path("/createNewsForAdmin")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewsForAdmin(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String className = (String) map.get("className");
		if(map.get("primaryKey") == null){
			map.put("createdDate", DateUtil.formatedCurrentDate());
			DBServices.create(EOObject.createObject(map));
		}	
		else {
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			EOObject eoObject = EOObject.getObjectByPK(className, (Integer) map.get("primaryKey"));
			
			DBServices.update(EOObject.updateObject(eoObject, map));
		}	
	
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getNewsList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getNewsList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = " SELECT n.primary_key, n.news_date, n.news_desc " + " FROM eonews n  where n.is_active = true ";
		List<HashMap<String, Object>> newsList = DBServices.getNativeQueryResult(str);

		return Response.status(201).entity(JSONUtil.objectToJson(newsList)).build();
	}

	@POST
	@Path("/getNewsByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getNewsByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoNewsPK");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("From EONews WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> newsList = new HashMap<>();
		for (Object obj : resultSet) {
			EONews eoNews = (EONews) obj;

			newsList.put("primaryKey", eoNews.primaryKey);
			newsList.put("newsDate", eoNews.newsDate);
			newsList.put("newsDesc", eoNews.newsDesc);

		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(newsList)).build();
	}
	
	@POST
	@Path("/deleteNewsByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteNewsByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String reason = (String) map.get("reason");
		String newsPk = (String) map.get("newsPk");
		
		String qry = "UPDATE EONews SET is_active = false , removingReason ='"+reason+"' WHERE primaryKey ="+newsPk;
		DBServices.updateNativeQueryResult(qry);
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();		
	}	

	/* For News----------------------------END-------------------- */
	
	
	
	/* For QUERY  FORm-------Start------------------------- */

	@POST
	@Path("/createQueryForm")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createQueryForm(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			//System.out.println("map::"+map);
			
			String className = (String) map.get("className");
			String studentList = (String) map.get("musicDesc");
			String studentId = (String) map.get("studentId");
			String usrName = (String) map.get("userName");
		    map.remove("musicCategoryList");
			map.remove("musicDesc");
			map.remove("studentId");
			if(map.get("primaryKey") == null) {
				map.put("createdDate", DateUtil.formatedCurrentDate());
				DBServices.create(EOObject.createObject(map));
				if(studentList.length() >0){
					HashMap<String, Object> loginMap = new HashMap<>();
					map.remove("isVisible"); 
					loginMap.put("userName", map.get("userName"));
					loginMap.put("password", "f36136ae5e5863d813353c795f15cbf4");
					loginMap.put("className", "EOLoginDetails");
					loginMap.put("role", "EOStudentUser"); 
					loginMap.put("createdDate",DateUtil.formatedCurrentDate());
					DBServices.create(EOObject.createObject(loginMap));
					EOLoginDetails eoLoginDetails = (EOLoginDetails) EOObject.getLatestObject("EOLoginDetails"); 
					map.remove("address");
					map.remove("nextFollowupDate"); 
					map.remove("enquiryDate");
					map.remove("remarks");
					map.put("coursePkList", JSONUtil.jsonToObject(map.get("coursePk")+"", List.class));
					map.remove("coursePk");
					map.put("eoLoginDetails",eoLoginDetails.primaryKey); 
					map.put("createdDate",DateUtil.formatedCurrentDate());
					map.put("enrollmentDate",DateUtil.formatedCurrentDate());
					map.put("isVisible", true);
					map.put("studentId", studentId);
					map.put("isTrial", true);
					map.put("className","EOStudentUser"); 
					map.remove("primaryKey");
					List<HashMap<String, Object>> courseList = (List<HashMap<String, Object>>)map.get("coursePkList"); 
					map.remove("coursePkList");
					map.remove("userName");
					DBServices.create(EOObject.createObject(map));
					HashMap<String,Object> courseArray = new HashMap<>();
					courseArray.put("type", "create");
					courseArray.put("courseList", courseList);
					RegistrationController.createBatches(courseArray);
				}
			
			}	
			else {
				String musicList = (String) map.get("musicValue");
				map.remove("musicValue");
				map.put("updatedDate", DateUtil.formatedCurrentDate());
				EOObject eoObject = EOObject.getObjectByPK(className, (Integer) map.get("primaryKey"));
				DBServices.update(EOObject.updateObject(eoObject, map));
				if(studentList.length()>0 || musicList.length()>0){
					map.remove("address");
					map.remove("nextFollowupDate");
					map.remove("enquiryDate");
					map.remove("remarks");
					//map.remove("studentId");
					map.remove("coursePkList");
					map.remove("userName");
					String quryValue = "SELECT primary_key " + "FROM eologin_details WHERE user_name = '" + usrName + "'";
					List<HashMap<String, Object>> formList = DBServices.getNativeQueryResult(quryValue);
					String quryValue1 = "SELECT primary_key " + "FROM eostudent_user WHERE eologindetails_primary_key = "
							+ formList.get(0).get("primary_key");
					List<HashMap<String, Object>> formList1 = DBServices.getNativeQueryResult(quryValue1);
					BigInteger primaryKey = (BigInteger) formList1.get(0).get("primary_key");
					map.put("primaryKey", primaryKey);
					EOObject eoObject1 = EOObject.getObjectByPK("EOStudentUser", (BigInteger) map.get("primaryKey"));
					if (musicList.length() > 0) {
						map.put("isActive", true);
						map.put("isVisible", true);
					} else {
						map.put("isActive", false);
						map.put("isVisible", false);
					}
					DBServices.update(EOObject.updateObject(eoObject1, map));
					map.remove("primaryKey");
					String qury = "Update EOLoginDetails set isActive = " + map.get("isActive") + " where userName = '"
							+ usrName + "'";
					DBServices.updateNativeQueryResult(qury);
				}
		       else {
					HashMap<String, Object> loginMap = new HashMap<>();
					map.remove("isVisible");
					loginMap.put("userName", map.get("userName"));
					loginMap.put("password", "f36136ae5e5863d813353c795f15cbf4");
					loginMap.put("className", "EOLoginDetails");
					loginMap.put("role", "EOStudentUser");
					loginMap.put("createdDate", DateUtil.formatedCurrentDate());
					DBServices.create(EOObject.createObject(loginMap));
					EOLoginDetails eoLoginDetails = (EOLoginDetails) EOObject.getLatestObject("EOLoginDetails");
					map.remove("address");
					map.remove("nextFollowupDate");
					map.remove("enquiryDate");
					map.remove("remarks");
					map.put("coursePkList", JSONUtil.jsonToObject(map.get("coursePk") + "", List.class));
					map.remove("coursePk");
					map.put("eoLoginDetails", eoLoginDetails.primaryKey);
					map.put("createdDate", DateUtil.formatedCurrentDate());
					map.put("enrollmentDate", DateUtil.formatedCurrentDate());
					map.put("isVisible", true);
					map.put("studentId", studentId);
					map.put("isTrial", true);
					map.put("className", "EOStudentUser");
					map.remove("primaryKey");
					List<HashMap<String, Object>> courseList = (List<HashMap<String, Object>>) map.get("coursePkList");
					map.remove("coursePkList");
					map.remove("userName");
					DBServices.create(EOObject.createObject(map));
					HashMap<String, Object> courseArray = new HashMap<>();
					courseArray.put("type", "create");
					courseArray.put("courseList", courseList);
					RegistrationController.createBatches(courseArray);
				}

		}		
			
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	@POST
	@Path("/getQueryForm")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getQueryForm(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		

		/*String str = "SELECT n.primary_key, n.enquiry_date, n.email, n.first_name  , n.last_name  ,CONCAT(n.first_name, ' ',n.last_name) as stdfullname, n.phone, n.gender, n.remarks, n.music_pk, "
				+ "  n.next_followup_date,n.address, n.date_of_birth, c.course_name  " 
				+ " FROM eoquery_form n INNER JOIN EOCourses c ON n.eocourses_primary_key=c.primary_key where n.is_active = true ";*/
		
		String str = "SELECT n.primary_key, n.enquiry_date, n.email, n.first_name , n.last_name, n.first_name_jap, n.last_name_jap,"
				+ " CONCAT(n.first_name, ' ',n.last_name) as stdfullname, n.phone, n.gender, n.remarks, n.course_pk, "
				+ " n.next_followup_date,n.address, n.date_of_birth, n.user_name , n.music_pk" 
				+ " FROM eoquery_form n where n.is_active = true AND n.is_convert = true";
		List<HashMap<String, Object>> formList = DBServices.getNativeQueryResult(str);
		List<HashMap<String, Object>> dataList = new ArrayList<>();
		for(HashMap<String, Object> obj : formList){
			HashMap<String , Object> innerMap = new HashMap<>();
			
			innerMap.put("primary_key", obj.get("primary_key"));
			innerMap.put("enquiry_date", obj.get("enquiry_date"));
			innerMap.put("email", obj.get("email"));
			innerMap.put("first_name", obj.get("first_name"));
			innerMap.put("last_name", obj.get("last_name"));
			innerMap.put("firstNameJap", obj.get("first_name_jap"));
			innerMap.put("lastNameJap", obj.get("last_name_jap"));
			innerMap.put("stdfullname", obj.get("stdfullname"));
			innerMap.put("phone", obj.get("phone"));
			innerMap.put("gender", obj.get("gender"));
			innerMap.put("remarks", obj.get("remarks"));
			innerMap.put("music_pk", obj.get("music_pk"));
			innerMap.put("course_pk_list", JSONUtil.jsonToObject(obj.get("course_pk")+"", List.class));
			innerMap.put("next_followup_date", obj.get("next_followup_date"));
			innerMap.put("address", obj.get("address"));
			innerMap.put("date_of_birth", obj.get("date_of_birth"));
			innerMap.put("user_name", obj.get("user_name"));
			dataList.add(innerMap);
		}
		

		return Response.status(201).entity(JSONUtil.objectToJson(dataList)).build();
			
	}
	
	@POST
	@Path("/moveQryFormFn")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response moveQryFormFn(HashMap<String, Object> map) throws ClassNotFoundException, NoSuchMethodException, 
		SecurityException, InstantiationException, IllegalAccessException, 
		IllegalArgumentException, InvocationTargetException {

		List<Object> studList = (List<Object>) map.get("studList");
		
		String className = (String) map.get("className");
		String qryClassName ="EOQueryForm";
		map.remove("studList");
		
		String studListArr = " ( ";
		int i = 0;
		for (Object st : studList) {
			i++;
			if (i < studList.size()) {
				studListArr = studListArr + st + " , ";
			} else {
				studListArr = studListArr + st;
			}
		}
		studListArr = studListArr + " ) ";
		
		List<HashMap<String, Object>> studDataList = (List<HashMap<String, Object>>) map.get("studDataList");
		
		
		for (HashMap<String, Object> innerMap : studDataList) {
			HashMap<String, Object> loginMap = new HashMap<>();
			loginMap.put("userName", innerMap.get("userName"));
			String quryValue = "SELECT primary_key "
					+ "FROM eologin_details WHERE user_name = '"+loginMap.get("userName")+"'";
			List<HashMap<String, Object>> formList = DBServices.getNativeQueryResult(quryValue);
			if(formList.size()>0) {
				String qurystr = "Update EOStudentUser set isActive = true , isVisible = true , isTrial = false where eoLoginDetails.primaryKey = " +formList.get(0).get("primary_key");
				DBServices.updateNativeQueryResult(qurystr);
				String qury = "Update EOLoginDetails set isActive = true  where userName = '" + loginMap.get("userName")+"'";
				DBServices.updateNativeQueryResult(qury);
				}
			else {
			loginMap.put("password", "f36136ae5e5863d813353c795f15cbf4");
			loginMap.put("className", "EOLoginDetails");
			loginMap.put("role", "EOStudentUser");
			loginMap.put("createdDate", DateUtil.formatedCurrentDate());
			DBServices.create(EOObject.createObject(loginMap));
			EOLoginDetails eoLoginDetails = (EOLoginDetails) EOObject.getLatestObject("EOLoginDetails");
			innerMap.put("eoLoginDetails", eoLoginDetails.primaryKey);
			innerMap.put("createdDate", DateUtil.formatedCurrentDate());
			innerMap.put("enrollmentDate",DateUtil.formatedCurrentDate());
			map.put("isVisible", true);
		    map.put("isTrial", false);
			innerMap.put("className","EOStudentUser");
			innerMap.remove("primaryKey");
			List<HashMap<String, Object>> courseList = (List<HashMap<String, Object>>) innerMap.get("coursePkList");
			innerMap.remove("coursePkList");
			innerMap.remove("userName");
			DBServices.create(EOObject.createObject(innerMap));
			HashMap<String,Object> courseArray = new HashMap<>();
			courseArray.put("type", "create");
			courseArray.put("courseList", courseList);
			RegistrationController.createBatches(courseArray);
			}
		}
	    String Query = "Update " + qryClassName + " set isActive = false where primaryKey in " + studListArr;
	    DBServices.updateNativeQueryResult(Query);
	    return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	@POST
	@Path("/getQueryStudentsByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getQueryStudentsByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String pk = (String) map.get("eoStudUserPK");
		
		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("From EOQueryForm WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> queryFormList = new HashMap<>();
		for (Object obj : resultSet) {
			EOQueryForm eoQueryForm = (EOQueryForm) obj;

			queryFormList.put("primaryKey", eoQueryForm.primaryKey);
			queryFormList.put("firstName", eoQueryForm.firstName);
			queryFormList.put("lastName", eoQueryForm.lastName);
			queryFormList.put("firstNameJap", eoQueryForm.firstNameJap);
			queryFormList.put("lastNameJap", eoQueryForm.lastNameJap);
			queryFormList.put("gender", eoQueryForm.gender);
			queryFormList.put("dateOfBirth", eoQueryForm.dateOfBirth);
			queryFormList.put("enquiryDate", eoQueryForm.enquiryDate);
			queryFormList.put("email", eoQueryForm.email);
			queryFormList.put("phone", eoQueryForm.phone);
			queryFormList.put("address", eoQueryForm.address);
			queryFormList.put("remarks", eoQueryForm.remarks);
			queryFormList.put("userName", eoQueryForm.userName);
			//queryFormList.put("isVisible", eoQueryForm.isVisible);
			queryFormList.put("nextFollowupDate", eoQueryForm.nextFollowupDate);
			queryFormList.put("coursePk", (List<Object>) JSONUtil.jsonToObject(eoQueryForm.coursePk, List.class));
			
			if(eoQueryForm.eoCourses != null) {
			queryFormList.put("eoCourses", eoQueryForm.eoCourses.primaryKey+"");
			}
			
			
			if(eoQueryForm.musicPk != null) {
				List<Object> pkObj = (List<Object>) JSONUtil.jsonToObject(eoQueryForm.musicPk, List.class);
				
				List<HashMap<String, Object>> finalMap = new ArrayList<>();

				for(Object str : pkObj){
					
					String qryStr = "select primary_key , music_type FROM lkmusic_type  where is_active =  true AND primary_key ="+str;
				    List<HashMap<String, Object>> musicList = DBServices.getNativeQueryResult(qryStr);
					HashMap<String, Object> tempMap = new HashMap<>();
				 	tempMap.put("primaryKey", musicList.get(0).get("primary_key")+"");					
					tempMap.put("musicType", musicList.get(0).get("music_type")+"");
					finalMap.add(tempMap);
				}
				queryFormList.put("musicCategoryList", finalMap);
			}
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(queryFormList)).build();
	}
	
	
	@POST
	@Path("/notConvertedStudFn")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response notConvertedStudFn(HashMap<String, Object> map) {

		//String studPk = (String)map.get("studPk");
		String className = (String) map.get("className");
		List<Object> studList = (List<Object>) map.get("studList");
		List<Object> userNameList = (List<Object>) map.get("userNameList");
		String studListArr = " ( ";
		int i = 0;
		for (Object st : studList) {
			i++;
			if (i < studList.size()) {
				studListArr = studListArr + st + " , ";
			} else {
				studListArr = studListArr + st;
			}
		}
		studListArr = studListArr + " ) ";
		String studUserListArr = " ( ";
		int j = 0;
		for (Object st : userNameList) {
			j++;
			if (j < userNameList.size()) {
				studUserListArr = studUserListArr + st + " , ";
			} else {
				studUserListArr = studUserListArr + st;
			}
		}
		studUserListArr = studUserListArr + " ) ";
		String convertDate = DateUtil.formatedCurrentDateInFormat();
		String Query = "Update " + className + " set isConvert = false ,  isActive = false ,convertDate = "+convertDate+"  where primaryKey in " + studListArr;
		DBServices.updateNativeQueryResult(Query);
		if(userNameList!=null && userNameList.size()>0 ) {
		for(Object obj : userNameList){
			String qury = "SELECT primary_key "
				+ "FROM eologin_details WHERE user_name = '"+obj+"' and is_active='true'";
			List<HashMap<String, Object>> formList = DBServices.getNativeQueryResult(qury);
			List<HashMap<String, Object>> dataList = new ArrayList<>();
			for(HashMap<String, Object> objlst : formList){
				String qurystr = "Update EOStudentUser set isActive = false, isVisible = false where eoLoginDetails.primaryKey = " + objlst.get("primary_key");
				DBServices.updateNativeQueryResult(qurystr);
				}
			}
		}
		
        return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	
	@POST
	@Path("/getFollowUpDate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getFollowUpDate(HashMap<String, Object> map) {
		String currentDate =  DateUtil.formatedCurrentDateInFormat();
		String query = "SELECT primary_key, first_name, phone "
				+ "FROM eoquery_form WHERE next_followup_date ='"+currentDate+"' and is_active='true'";
		
		List<HashMap<String, Object>> objList = DBServices.getNativeQueryResult(query);
		
		return Response.status(201).entity(JSONUtil.objectToJson(objList)).build();
	}
	
	
	@POST
	@Path("/getAllFollowUpDate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAllFollowUpDate(HashMap<String, Object> map) {
		//String currentDate =  DateUtil.formatedCurrentDateInFormat();
		//System.out.println("currentDate currentDate currentDate::::::"+currentDate);
		String query = "SELECT primary_key, first_name, phone, next_followup_date "
				+ "FROM eoquery_form WHERE  is_active='true'";
		List<HashMap<String, Object>> objList = DBServices.getNativeQueryResult(query);
		
		return Response.status(201).entity(JSONUtil.objectToJson(objList)).build();
	}
	
	
	
	@POST
	@Path("/getPhoneNumberList")   //nikitaaaa
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPhoneNumberList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		List<String> finalList = new ArrayList<>();
		
		List<Object> teacherObjList = DBServices.get(" FROM  EOTeacherUser WHERE is_active = true");
		List<String> teacherList = new ArrayList<>();
		for(Object obj : teacherObjList){
			EOTeacherUser eoTeacherUser = (EOTeacherUser) obj;
			/*HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("phone", eoTeacherUser.phone);*/
			teacherList.add(eoTeacherUser.phone);
		}
		
		List<Object> studentObjList = DBServices.get(" FROM  EOStudentUser WHERE is_active = true");
		
		List<String> studentList = new ArrayList<>();
		for(Object obj : studentObjList){
			EOStudentUser eoStudentUser = (EOStudentUser) obj;
			/*HashMap<String, Object> innerMap = new HashMap<>();*/
			/*innerMap.put("phone", eoStudentUser.phone);*/
			studentList.add(eoStudentUser.phone);
		}
		
		/*((List<Object>) finalMap).add(studentList);
		((List<Object>) finalMap).add(teacherList);*/
		finalList.addAll(studentList);
		finalList.addAll(teacherList);
	
		
		/*finalList.put("studentPhoneList", studentList);
		((HashMap<String, Object>) finalList).put("teacherPhoneList", teacherList);*/
		
		
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();		
	}
	
	
	/* For QUERY  FORm-------------End------------------------ */
	
	
	
	
	
	/* For Competition-------Start------------------------- */

	@POST
	@Path("/createCompetition")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createCompetition(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			String className = (String) map.get("className");
			
			map.remove("musicCategoryList");
			map.remove("studentsCompetitionList");
			map.remove("teachersCompetitionList");
			
			List<HashMap<String,Object>> studentBatchMappingList = (List<HashMap<String,Object>>)map.get("eoCompetitionMapping");
			map.remove("eoCompetitionMapping");
			
			
			
			if(map.get("primaryKey") == null) {
				map.put("createdDate", DateUtil.formatedCurrentDate());
				DBServices.create(EOObject.createObject(map));
				
				EOCompetition eoCompetition = (EOCompetition)EOObject.getLatestObject(className);
				for(HashMap<String, Object> sbMap : studentBatchMappingList){
					sbMap.put("eoCompetition",eoCompetition.primaryKey);
					DBServices.create(EOObject.createObject(sbMap));
				}	
			}	
			
			else {
				map.put("updatedDate", DateUtil.formatedCurrentDate());
				EOCompetition eoCompetition = (EOCompetition)EOObject.getObjectByPK(className, (Integer) map.get("primaryKey"));
				
				DBServices.update(EOObject.updateObject(eoCompetition, map));
				
				
				//EOCompetition eoCompetition = (EOCompetition)EOObject.getObjectByPK(className,primaryKey);
				for(HashMap<String, Object> sbMap : studentBatchMappingList) {
					
					if(sbMap.get("primaryKey") != null) {
						EOCompetitionMapping eocompMap = (EOCompetitionMapping)EOObject.getObjectByPK("EOCompetitionMapping", (String)sbMap.get("primaryKey"));
						
						DBServices.update(EOObject.updateObject(eocompMap, sbMap));
						
					} else {
						sbMap.put("eoCompetition",eoCompetition.primaryKey);
						DBServices.create(EOObject.createObject(sbMap));
					}
					
				}
			}		
			
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	
	@POST
	@Path("/getCompetition")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompetition(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = "SELECT n.primary_key, n.competition_date, n.organizing_authority, n.venue, n.prerequisite, n.name, n.music_pk,n.details,n.brochure_url "
					+" FROM eocompetition n where n.is_active = true  ";
		
		List<HashMap<String, Object>> competitionList = DBServices.getNativeQueryResult(str);

		return Response.status(201).entity(JSONUtil.objectToJson(competitionList)).build();
			
	}
	
	
	@POST
	@Path("/getCompetitionByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompetitionByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoCompetitionPK");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("From EOCompetition WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> competitionList = new HashMap<>();
		for (Object obj : resultSet) {
			EOCompetition eoCompetition = (EOCompetition) obj;

			competitionList.put("primaryKey", eoCompetition.primaryKey);
			competitionList.put("name", eoCompetition.name);
			competitionList.put("competitionDate", eoCompetition.competitionDate);
			
			competitionList.put("details", eoCompetition.details);
			competitionList.put("prerequisite", eoCompetition.prerequisite);
			competitionList.put("organizingAuthority", eoCompetition.organizingAuthority);  
			competitionList.put("venue", eoCompetition.venue);  
			competitionList.put("brochureUrl", eoCompetition.brochureUrl); 
			
			
			if(eoCompetition.musicPk != null) {
				List<Object> pkObj = (List<Object>) JSONUtil.jsonToObject(eoCompetition.musicPk, List.class);
				
				List<HashMap<String, Object>> finalMap = new ArrayList<>();

				for(Object str : pkObj){
					
					String qryStr = "select primary_key , music_type FROM lkmusic_type  where is_active =  true AND primary_key ="+str;
					List<HashMap<String, Object>> musicList = DBServices.getNativeQueryResult(qryStr);
				 		 					
				 	HashMap<String, Object> tempMap = new HashMap<>();
				 	tempMap.put("primaryKey", musicList.get(0).get("primary_key")+"");					
					tempMap.put("musicType", musicList.get(0).get("music_type")+"");
					finalMap.add(tempMap);
				}
				competitionList.put("musicCategoryList", finalMap);
			}
			
			List<EOCompetitionMapping> compDetailedList = eoCompetition.eoCompetitionMapping;
			List<HashMap<String, Object>> finalList = new ArrayList();
			for(Object obj2 :compDetailedList) {
				HashMap<String, Object> innerMap = new HashMap<>();
				EOCompetitionMapping eoCompetitionMapping = (EOCompetitionMapping) obj2;
				if(eoCompetitionMapping.isActive == true) {
					if(eoCompetitionMapping.eoStudentUser != null){
						innerMap.put("eoStudentUser", eoCompetitionMapping.eoStudentUser.primaryKey+"");
					}
					if(eoCompetitionMapping.eoTeacherUser != null){
						innerMap.put("eoTeacherUser", eoCompetitionMapping.eoTeacherUser.primaryKey+"");
					}
					innerMap.put("isActive", eoCompetitionMapping.isActive);
					innerMap.put("primaryKey", eoCompetitionMapping.primaryKey+"");
					finalList.add(innerMap);
				}
				
				
			}
			
			competitionList.put("eoCompetitionMapping", finalList);		
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(competitionList)).build();
	}
	
	
	@POST
	@Path("/getCompListForStud")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompListForStud(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String userPk = (String) map.get("userPk");
		
		String query = "SELECT Distinct ec.primary_key, ec.name, ec.competition_date, ec.organizing_authority, "
						+ "ec.venue, ec.prerequisite, ec.music_pk, ec.details, ec.brochure_url "
						+" FROM eocompetition ec INNER JOIN eocompetition_mapping ecm ON ec.primary_key = ecm.eocompetition_primary_key "
						+" where ec.is_active ='true' and ecm.is_active ='true' and ecm.eostudentuser_primary_key = '"+userPk+"'";
		
		List<HashMap<String, Object>> objList = DBServices.getNativeQueryResult(query);
		
		return Response.status(201).entity(JSONUtil.objectToJson(objList)).build();
	}
	
	@POST
	@Path("/getCompListForTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompListForTeacher(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String userPk = (String) map.get("userPk");
		
		String query = "SELECT Distinct ec.primary_key, ec.name, ec.competition_date, ec.organizing_authority, ec.venue, ec.prerequisite, ec.music_pk, ec.details, ec.brochure_url "
						+" FROM eocompetition ec INNER JOIN eocompetition_mapping ecm ON ec.primary_key = ecm.eocompetition_primary_key "
						+" where ec.is_active ='true' and ecm.is_active ='true' and ecm.eoteacheruser_primary_key = '"+userPk+"'";
		
		List<HashMap<String, Object>> objList = DBServices.getNativeQueryResult(query);
		
		return Response.status(201).entity(JSONUtil.objectToJson(objList)).build();
	}
	
	@POST
	@Path("/getCompListForTeacherPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompListForTeacherPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("teacherPk");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("From EOCompetition WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> compList = new HashMap<>();
		for (Object obj : resultSet) {
			EOCompetition eoCompetition = (EOCompetition) obj;

			compList.put("primaryKey", eoCompetition.primaryKey);
			compList.put("name", eoCompetition.name);
			compList.put("competitionDate", eoCompetition.competitionDate);
			
			compList.put("details", eoCompetition.details);
			compList.put("prerequisite", eoCompetition.prerequisite);
			compList.put("organizingAuthority", eoCompetition.organizingAuthority);
			compList.put("venue", eoCompetition.venue);
			compList.put("brochureUrl", eoCompetition.brochureUrl);

		}
		
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(compList)).build();
	}
	
	
	@POST
	@Path("/getCompListForStudByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompListForStudByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String pk = (String) map.get("studentPk");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("From EOCompetition WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> compList = new HashMap<>();
		for (Object obj : resultSet) {
			EOCompetition eoCompetition = (EOCompetition) obj;

			compList.put("primaryKey", eoCompetition.primaryKey);
			compList.put("name", eoCompetition.name);
			compList.put("competitionDate", eoCompetition.competitionDate);
			
			compList.put("details", eoCompetition.details);
			compList.put("prerequisite", eoCompetition.prerequisite);
			compList.put("organizingAuthority", eoCompetition.organizingAuthority);
			compList.put("venue", eoCompetition.venue);
			compList.put("brochureUrl", eoCompetition.brochureUrl);

		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(compList)).build();
	}
	
	
	
	
	/* For Competition-------End----------------------- */
	
	@POST
	@Path("/getProfileImage")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getProfileImage(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String userPk = (String) map.get("userPk");
		String className = (String) map.get("className");
		
		String str = "";
		List<HashMap<String, Object>> objList = new ArrayList<>();
		
		if(!className.equalsIgnoreCase("eomanagement_user")) {
			str = "SELECT eoi.image_url, u.eoimage_primary_key FROM "+className
					+" u LEFT JOIN eoimage eoi ON eoi.primary_key = u.eoimage_primary_key"
					+" WHERE u.primary_key = "+userPk;
			objList = DBServices.getNativeQueryResult(str);
		}else {
			
			str = "SELECT eoi.image_url, u.eoimage FROM "+className
					+" u LEFT JOIN eoimage eoi ON eoi.primary_key = u.eoimage"
					+" WHERE u.primary_key = "+userPk;
			objList = DBServices.getNativeQueryResult(str);
		}
		
		
		return Response.status(201).entity(JSONUtil.objectToJson(objList)).build();
	}
	
	@POST
	@Path("/getColorCodeForTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getColorCodeForTeacher(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String str = "SELECT color_code FROM eoteacher_user WHERE is_active = true";
		List<HashMap<String, Object>> teacherColorList = DBServices.getNativeQueryResult(str);
		List<HashMap<String, Object>> colorList = new ArrayList<>();
		String qry = "";
		
		String color = "(";
		if(teacherColorList != null && teacherColorList.size() > 0){
			int count = 0;
			for(HashMap<String, Object> obj : teacherColorList){
				count++;
				color += "'" + obj.get("color_code") +"'";
				if(count < teacherColorList.size()){
					color += ",";
				}
			}
			color += ")";
			
			qry = "SELECT primary_key, color_code, is_active FROM lkcolor WHERE is_active = true"
					+ " AND color_code NOT IN " +color+" ORDER BY primary_key";
		}else{
			qry = "SELECT primary_key, color_code, is_active FROM lkcolor WHERE is_active = true "
					+ " ORDER BY primary_key";
		}
		colorList = DBServices.getNativeQueryResult(qry);
		return Response.status(201).entity(JSONUtil.objectToJson(colorList)).build();
	}
	
	@POST
	@Path("/getTrialStudents")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTrialStudents(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		

		/*String str = "SELECT n.primary_key, n.enquiry_date, n.email, n.first_name  , n.last_name  ,CONCAT(n.first_name, ' ',n.last_name) as stdfullname, n.phone, n.gender, n.remarks, n.music_pk, "
				+ "  n.next_followup_date,n.address, n.date_of_birth, c.course_name  " 
				+ " FROM eoquery_form n INNER JOIN EOCourses c ON n.eocourses_primary_key=c.primary_key where n.is_active = true ";*/
		
		String str = "SELECT n.primary_key, n.enquiry_date, n.email, n.first_name, n.last_name,n.first_name_jap, n.last_name_jap,"
				+ " CONCAT(n.first_name, ' ',n.last_name) as stdfullname, n.phone, n.gender, n.remarks, n.course_pk, "
				+ " n.next_followup_date,n.address, n.date_of_birth, n.user_name , n.music_pk" 
				+ " FROM eoquery_form n where n.is_convert = true";
		List<HashMap<String, Object>> formList = DBServices.getNativeQueryResult(str);
		List<HashMap<String, Object>> dataList = new ArrayList<>();
		for(HashMap<String, Object> obj : formList){
			HashMap<String , Object> innerMap = new HashMap<>();
			
			innerMap.put("primary_key", obj.get("primary_key"));
			innerMap.put("enquiry_date", obj.get("enquiry_date"));
			innerMap.put("email", obj.get("email"));
			innerMap.put("first_name", obj.get("first_name"));
			innerMap.put("last_name", obj.get("last_name"));
			innerMap.put("firstNameJap", obj.get("first_name_jap"));
			innerMap.put("lastNameJap", obj.get("last_name_jap"));
			innerMap.put("stdfullname", obj.get("stdfullname"));
			innerMap.put("phone", obj.get("phone"));
			innerMap.put("gender", obj.get("gender"));
			innerMap.put("remarks", obj.get("remarks"));
			innerMap.put("music_pk", obj.get("music_pk"));
			innerMap.put("course_pk_list", JSONUtil.jsonToObject(obj.get("course_pk")+"", List.class));
			innerMap.put("next_followup_date", obj.get("next_followup_date"));
			innerMap.put("address", obj.get("address"));
			innerMap.put("date_of_birth", obj.get("date_of_birth"));
			innerMap.put("user_name", obj.get("user_name"));
			dataList.add(innerMap);
		}
		

		return Response.status(201).entity(JSONUtil.objectToJson(dataList)).build();
			
	}
	@POST
	@Path("/updateTrialStudents")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTrialStudents(HashMap<String, Object> map) throws ClassNotFoundException, NoSuchMethodException, 
		SecurityException, InstantiationException, IllegalAccessException, 
		IllegalArgumentException, InvocationTargetException {

		List<Object> studList = (List<Object>) map.get("studList");
		
		String className = (String) map.get("className");
		//Boolean isVisible = (Boolean) map.get("isVisible");
		String qryClassName ="EOQueryForm";
		map.remove("studList");
		
		String studListArr = " ( ";
		int i = 0;
		for (Object st : studList) {
			i++;
			if (i < studList.size()) {
				studListArr = studListArr + st + " , ";
			} else {
				studListArr = studListArr + st;
			}
		}
		studListArr = studListArr + " ) ";
		
		List<HashMap<String, Object>> studDataList = (List<HashMap<String, Object>>) map.get("studDataList");
		for(HashMap<String, Object> obj : studDataList) {
			HashMap<String , Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", obj.get("primaryKey"));
			innerMap.put("isVisible", obj.get("isVisible"));
			String Query = "Update EOStudentUser set isVisible = "+ innerMap.get("isVisible") +" where primaryKey = " + innerMap.get("primaryKey");
			DBServices.updateNativeQueryResult(Query);
		}
      return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
}