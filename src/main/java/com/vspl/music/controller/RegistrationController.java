package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.ListIndexBase;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.type.DbTimestampType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.model.eo.EOBatch;
import com.vspl.music.model.eo.EOCourses;
import com.vspl.music.model.eo.EOImage;
import com.vspl.music.model.eo.EOLoginDetails;
import com.vspl.music.model.eo.EOManagementUser;
import com.vspl.music.model.eo.EOMusicRoom;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOQueryForm;
import com.vspl.music.model.eo.EOStudentBatch;
import com.vspl.music.model.eo.EOStudentBatchMapping;
import com.vspl.music.model.eo.EOStudentUser;
import com.vspl.music.model.eo.EOTeacherUser;
import com.vspl.music.model.lk.LKCategoryType;
import com.vspl.music.model.lk.LKMusicType;
import com.vspl.music.services.DBServices;
import com.vspl.music.services.ForgetPasswordMail;
import com.vspl.music.services.StudentRegistrationMail;
import com.vspl.music.services.TeacherRegistrationMail;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.JSONUtil;
import com.vspl.music.util.VSPLUtil;

/**
 * The RegistrationController program implements an application that simply
 * register all type of users.
 *
 * @author Kundan Kumar
 * @since
 */

@Path("/ajaxRegistration")
public class RegistrationController {

	public static Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createStudentUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String className = String.valueOf(map.get("className"));
		String userName = String.valueOf(map.get("userName"));
		String userName2 = String.valueOf(map.get("userName2"));
		map.remove("course");
		map.remove("userName");
		map.remove("userName2");
		//List<Object> phoneObj = DBServices.checkPhoneExists(className, (String) map.get("phone"));

		HashMap<String, Object> loginMap = new HashMap<>();
		loginMap.put("userName", userName);
		loginMap.put("userName2", userName2);
		loginMap.put("password", "f36136ae5e5863d813353c795f15cbf4");
		loginMap.put("className", "EOLoginDetails");
		loginMap.put("createdDate", DateUtil.formatedCurrentDate());

		if (className.equalsIgnoreCase("EOStudentUser")) {
			loginMap.put("role", "EOStudentUser");
			if (map.get("primaryKey") == null) {
				DBServices.create(EOObject.createObject(loginMap));

				EOLoginDetails eoLoginDetails = (EOLoginDetails) EOObject.getLatestObject("EOLoginDetails");
				map.put("eoLoginDetails", eoLoginDetails.primaryKey);
				map.put("createdDate", DateUtil.formatedCurrentDate());
				DBServices.create(EOObject.createObject(map));
				// StudentRegistrationMail.composeMail(mailBody, subjectLine, email);
				if (map.get("eoImage") != null) {
					EOStudentUser eoStudentUser = (EOStudentUser) EOObject.getLatestObject(className);
					EOImage eoImage = (EOImage) EOObject.getObjectByPK("EOImage", (String) map.get("eoImage"));
					eoImage.headerPk = eoStudentUser.primaryKey;
					eoImage.postCreate(eoImage);
				}
				//email
				//System.out.println("email check:"+VSPLUtil.properties.getProperty(VSPLUtil.send_email).trim().equalsIgnoreCase("y"));
				if (VSPLUtil.properties.getProperty(VSPLUtil.send_email).trim().equalsIgnoreCase("y")){
					StudentRegistrationMail.composeMail((String)map.get("firstName"),(String)map.get("email"));
				}
				
			} else {
				map.put("updatedDate", DateUtil.formatedCurrentDate());
				EOObject eoObject = EOObject.getObjectByPK(className, (Integer) map.get("primaryKey"));

				DBServices.update(EOObject.updateObject(eoObject, map));
				EOStudentUser eoStudentUser1 = (EOStudentUser) eoObject;

				String loginPk = eoStudentUser1.eoLoginDetails.primaryKey + "";
				EOObject loginObject = EOObject.getObjectByPK("EOLoginDetails", loginPk);

				HashMap<String, Object> loginDataMap = new HashMap<>();
				loginDataMap.put("userName",eoStudentUser1.eoLoginDetails.userName);

				DBServices.update(EOObject.updateObject(loginObject, loginDataMap));

				if (map.get("eoImage") != null) {
					EOStudentUser eoStudentUser = (EOStudentUser) EOObject.getLatestObject(className);
					EOImage eoImage = (EOImage) EOObject.getObjectByPK("EOImage", (Integer) map.get("eoImage"));
					eoImage.headerPk = eoStudentUser.primaryKey;
					eoImage.postCreate(eoImage);
				}
				// StudentRegistrationMail.composeMail("Testing email", "Registration
				// Succesful", "ashtha11cse@gmail.com");
			}

			// StudentRegistrationMail.composeMail("Testing email", "Registration
			// Succesful", "ashtha11cse@gmail.com");

		} else {
			loginMap.put("role", "EOTeacherUser");
			if (map.get("primaryKey") == null) {
				DBServices.create(EOObject.createObject(loginMap));
				EOLoginDetails eoLoginDetails = (EOLoginDetails) EOObject.getLatestObject("EOLoginDetails");
				map.put("eoLoginDetails", eoLoginDetails.primaryKey);
				map.put("createdDate", DateUtil.formatedCurrentDate());
				DBServices.create(EOObject.createObject(map));
				if (map.get("eoImage") != null) {
					EOTeacherUser eoTeacherUser = (EOTeacherUser) EOObject.getLatestObject(className);
					EOImage eoImage = (EOImage) EOObject.getObjectByPK("EOImage", (String) map.get("eoImage"));
					eoImage.headerPk = eoTeacherUser.primaryKey;
					eoImage.postCreate(eoImage);
				}
				 //TeacherRegistrationMail.composeMail((String)map.get("firstName"),(String)map.get("email"));
			} else {

				map.put("updatedDate", DateUtil.formatedCurrentDate());
				EOObject eoObject = EOObject.getObjectByPK(className, (Integer) map.get("primaryKey"));
				DBServices.update(EOObject.updateObject(eoObject, map));
				EOTeacherUser eoTeacherUser1 = (EOTeacherUser) eoObject;

				String loginPk = eoTeacherUser1.eoLoginDetails.primaryKey + "";
				EOObject loginObject = EOObject.getObjectByPK("EOLoginDetails", loginPk);

				HashMap<String, Object> loginDataMap = new HashMap<>();
				loginDataMap.put("userName", eoTeacherUser1.eoLoginDetails.userName);

				DBServices.update(EOObject.updateObject(loginObject, loginDataMap));

				if (map.get("eoImage") != null) {
					EOTeacherUser eoTeacherUser = (EOTeacherUser) EOObject.getLatestObject(className);
					EOImage eoImage = (EOImage) EOObject.getObjectByPK("EOImage", (Integer) map.get("eoImage"));
					eoImage.headerPk = eoTeacherUser.primaryKey;
					eoImage.postCreate(eoImage);
				}
			}
			//StudentRegistrationMail.composeMail("Testing email", "Registration Succesful", "ashtha11cse@gmail.com");
		}
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}



	@POST
	@Path("/getStudentUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		List<Object> studentObjList = DBServices
				.get("From EOStudentUser WHERE is_active = true AND is_visible = true order by first_name");
		List<HashMap<String, Object>> studentList = new ArrayList<>();

		for (Object obj : studentObjList) {
			EOStudentUser eoStudentUser = (EOStudentUser) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", eoStudentUser.primaryKey);
			innerMap.put("studentfullName", eoStudentUser.getFullName());
			innerMap.put("studentfullNameJapanese", eoStudentUser.getFullNameJapanese());
			innerMap.put("gender", eoStudentUser.gender);
			innerMap.put("enrollmentDate", eoStudentUser.enrollmentDate);
			innerMap.put("email", eoStudentUser.email);
			innerMap.put("studentId", eoStudentUser.studentId);
			innerMap.put("phone", eoStudentUser.phone);
			innerMap.put("registrationAmount", eoStudentUser.registrationAmount);
			innerMap.put("updatedDate", eoStudentUser.updatedDate);
			innerMap.put("studentId", eoStudentUser.studentId);
			if (eoStudentUser.eoImage != null) {
				innerMap.put("imageUrl", eoStudentUser.eoImage.imageUrl);
				innerMap.put("eoImagePk", eoStudentUser.eoImage.primaryKey);
			}
			studentList.add(innerMap);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(studentList)).build();

	}

	@POST
	@Path("/getStudentsUserByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentsUserByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoStudUserPK");

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		List<Object> studentObjList = DBServices.get("From EOStudentUser WHERE primaryKey=" + pk);
		HashMap<String, Object> studentList = new HashMap<>();
		for (Object obj : studentObjList) {
			EOStudentUser eoStudentUser = (EOStudentUser) obj;

			studentList.put("primaryKey", eoStudentUser.primaryKey);
			studentList.put("firstName", eoStudentUser.firstName);
			studentList.put("lastName", eoStudentUser.lastName);
			studentList.put("firstNameJap", eoStudentUser.firstNameJap);
			studentList.put("lastNameJap", eoStudentUser.lastNameJap);
			studentList.put("studentId", eoStudentUser.studentId);
			studentList.put("gender", eoStudentUser.gender);
			studentList.put("dateOfBirth", eoStudentUser.dateOfBirth);
			studentList.put("enrollmentDate", eoStudentUser.enrollmentDate);
			studentList.put("email", eoStudentUser.email);
			studentList.put("illness", eoStudentUser.illness);
			studentList.put("prescribedMedication", eoStudentUser.prescribedMedication);
			studentList.put("phone", eoStudentUser.phone);
			studentList.put("registrationAmount", eoStudentUser.registrationAmount);
			studentList.put("alternateEmail", eoStudentUser.alternateEmail);
			studentList.put("alternatePhone", eoStudentUser.alternatePhone);
			studentList.put("addressLine1", eoStudentUser.addressLine1);
			studentList.put("addressLine2", eoStudentUser.addressLine2);

			if (eoStudentUser.eoImage != null) {
				studentList.put("imageUrl", eoStudentUser.eoImage.imageUrl);
				studentList.put("eoImage", eoStudentUser.eoImage.primaryKey);
			}
			studentList.put("guardianTitle", eoStudentUser.guardianTitle);
			studentList.put("guardianFirstName", eoStudentUser.guardianFirstName);
			studentList.put("guardianLastName", eoStudentUser.guardianLastName);
			studentList.put("relationship", eoStudentUser.relationship);
			studentList.put("guardianEmail", eoStudentUser.guardianEmail);
			studentList.put("guardianPhone", eoStudentUser.guardianPhone);
			studentList.put("guardianAddressLine1", eoStudentUser.guardianAddressLine1);
			studentList.put("guardianAddressLine2", eoStudentUser.guardianAddressLine2);		
			
			String qry = "SELECT l.user_name FROM eostudent_user s LEFT JOIN eologin_details l ON"
					+ " s.eologindetails_primary_key = l.primary_key WHERE"
					+ " s.primary_key = "+eoStudentUser.primaryKey+"";
			List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(qry);
			studentList.put("userName", dataList.get(0).get("user_name"));
			
			if(eoStudentUser.musicPk != null){
				List<Object> pkObj = (List<Object>) JSONUtil.jsonToObject(eoStudentUser.musicPk, List.class);

				//List<HashMap<String, Object>> finalMap = new ArrayList<>();
				
				
				String str = "SELECT batch_pk FROM eostudent_batch_mapping WHERE student_pk = '"+eoStudentUser.primaryKey+"' AND is_active = true";
				List<HashMap<String, Object>> batchList = DBServices.getNativeQueryResult(str);
				
				if(batchList != null && batchList.size() > 0){
					String batchPk = "(";
					int count = 0;
					for(HashMap<String, Object> newObj : batchList){
						batchPk += newObj.get("batch_pk");
						count++;
						if(count < batchList.size()){
							batchPk += ",";
						}
					}
					batchPk += ")";
					
					String strQry = "SELECT primary_key AS batchPk , eocourses_primary_key "
							+ " FROM eobatch WHERE primary_key IN "+batchPk +" AND is_active = true";
					List<HashMap<String, Object>> courseList = DBServices.getNativeQueryResult(strQry);
					
					
					if(courseList != null && courseList.size() > 0){
						String coursePk = "(";
						int count2 = 0;
						for(HashMap<String, Object> tempObj : courseList){
							coursePk += tempObj.get("eocourses_primary_key");
							count2++;
							if(count2 < courseList.size()){
								coursePk += ",";
							}
						}
						coursePk += ")";
						String Qry = "SELECT primary_key, course_name, lkcategorytype_primary_key, lkmusictype_primary_key "
								+ " FROM eocourses WHERE primary_key IN "+coursePk+" AND is_active = true";
						List<HashMap<String, Object>> courseListFinal = DBServices.getNativeQueryResult(Qry);
						studentList.put("musicCategoryList", courseListFinal);
						studentList.put("batchAndCourse", courseList);
					}
					
				}
				/*for(Object str : pkObj){
					
					String qryStr = "select primary_key , music_type FROM lkmusic_type  where is_active =  true AND primary_key ="+str;
					List<HashMap<String, Object>> musicList = DBServices.getNativeQueryResult(qryStr);

					HashMap<String, Object> tempMap = new HashMap<>();
					tempMap.put("primaryKey", musicList.get(0).get("primary_key") + "");
					tempMap.put("musicType", musicList.get(0).get("music_type") + "");
					finalMap.add(tempMap);
				}*/
				
			}

		}

		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(studentList)).build();
	}

	@POST
	@Path("/getPhoneDetailsByUserPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPhoneDetailsByUserPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String phone = (String) map.get("phone");
		String className1 = "EOStudentUser";
		String className2 = "EOTeacherUser";
		List<Object> phoneObj = DBServices.checkPhoneExists(className1, phone);

		HashMap<String, Object> pkMap = new HashMap<>();
		if (phoneObj != null) {
			for (Object obj : phoneObj) {
				EOStudentUser eoStudentUser = (EOStudentUser) obj;
				pkMap.put("primaryKey", eoStudentUser.primaryKey);
				pkMap.put("className", "EOStudentUser");
			}
		}
		if (phoneObj == null) {
			phoneObj = DBServices.checkPhoneExists(className2, phone);
			if (phoneObj != null) {
				for (Object obj : phoneObj) {
					EOTeacherUser eoTeacherUser = (EOTeacherUser) obj;
					pkMap.put("primaryKey", eoTeacherUser.primaryKey);
					pkMap.put("className", "EOTeacherUser");
				}
			}
			else{
				pkMap.put("primaryKey", "phone not matched");
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(pkMap)).build();
	}
	
	@POST
	@Path("/validateUserName")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validateUserName(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String userName = (String) map.get("userName");
		String response = "";
		List<Object> userNameObj = DBServices.checkUserNameExists(userName);
		if(userNameObj != null){
			response = "user name exists";
		}else{
			response = "user name doesn't exists";
		}
		return Response.status(201).entity(JSONUtil.objectToJson(response)).build();		
	}
	
	
	@POST
	@Path("/updateDeleteReasonByStdPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDeleteReasonByStdPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String reason = (String) map.get("reason");
		String userPk = (String) map.get("userPk");
		String className = (String) map.get("className");

		String query = "";

		if (className.equalsIgnoreCase("EOStudentUser")) {
			query = "SELECT s.eologindetails_primary_key, log.primary_key FROM eostudent_user s , eologin_details log"
					+ " WHERE s.primary_key = " + userPk + " AND s.eologindetails_primary_key = log.primary_key";
		}

		if (className.equalsIgnoreCase("EOTeacherUser")) {
			query = "SELECT s.eologindetails_primary_key, log.primary_key FROM eoteacher_user s , eologin_details log"
					+ " WHERE s.primary_key = " + userPk + " AND s.eologindetails_primary_key = log.primary_key";
		}

		List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(query);
		BigInteger loginPk = null;
		if (dataList.size() > 0 && dataList != null) {
			loginPk = (BigInteger) dataList.get(0).get("eologindetails_primary_key");
		}

		if (className.equalsIgnoreCase("EOStudentUser")) {
			String qry = "UPDATE EOStudentUser SET is_active = false , removingReason ='" + reason
					+ "' WHERE primaryKey =" + userPk;
			DBServices.updateNativeQueryResult(qry);
		}
		if (className.equalsIgnoreCase("EOTeacherUser")) {
			String qry = "UPDATE EOTeacherUser SET is_active = false , removingReason ='" + reason
					+ "' WHERE primaryKey =" + userPk;
			DBServices.updateNativeQueryResult(qry);
		}

		String str = "UPDATE EOLoginDetails SET is_active = false WHERE primaryKey =" + loginPk;
		DBServices.updateNativeQueryResult(str);
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getTeacherUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTeacherUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
	
		
		List<Object> teacherObjList = DBServices.get("From EOTeacherUser WHERE is_active = true order by first_name ");
		
		List<HashMap<String, Object>> teacherList = new ArrayList<>();
		for (Object obj : teacherObjList) {
			EOTeacherUser eoTeacherUser = (EOTeacherUser) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", eoTeacherUser.primaryKey);
			innerMap.put("teacherFullName", eoTeacherUser.getFullName());
			innerMap.put("teacherFullNameJapanese", eoTeacherUser.getFullNameJapanese());
			innerMap.put("gender", eoTeacherUser.gender);
			innerMap.put("joiningDate", eoTeacherUser.joiningDate);
			innerMap.put("email", eoTeacherUser.email);
			innerMap.put("phone", eoTeacherUser.phone);
			innerMap.put("updatedDate", eoTeacherUser.updatedDate); // for sorting
			innerMap.put("teacherId", eoTeacherUser.teacherId);
			if (eoTeacherUser.eoImage != null) {
				innerMap.put("imageUrl", eoTeacherUser.eoImage.imageUrl);
				innerMap.put("eoImagePk", eoTeacherUser.eoImage.primaryKey);
			}
			teacherList.add(innerMap);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(teacherList)).build();
	}

	@POST
	@Path("/getTeacherUserByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTeacherUserByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoTeacherPK");
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		List<Object> teacherObjList = DBServices.get("From EOTeacherUser WHERE primaryKey=" + pk);

		HashMap<String, Object> teacherList = new HashMap<>();
		for (Object obj : teacherObjList) {
			EOTeacherUser eoTeacherUser = (EOTeacherUser) obj;

			teacherList.put("primaryKey", eoTeacherUser.primaryKey);
			teacherList.put("title", eoTeacherUser.title);
			teacherList.put("firstName", eoTeacherUser.firstName);
			teacherList.put("lastName", eoTeacherUser.lastName);
			teacherList.put("firstNameJap", eoTeacherUser.firstNameJap);
			teacherList.put("lastNameJap", eoTeacherUser.lastNameJap);
			teacherList.put("teacherId", eoTeacherUser.teacherId);
			teacherList.put("gender", eoTeacherUser.gender);
			teacherList.put("qualification", eoTeacherUser.qualification);
			teacherList.put("experience", eoTeacherUser.experience);
			teacherList.put("profile", eoTeacherUser.profile);
			teacherList.put("awards", eoTeacherUser.awards);
			teacherList.put("email", eoTeacherUser.email);
			teacherList.put("phone", eoTeacherUser.phone);
			teacherList.put("alternateEmail", eoTeacherUser.alternateEmail);
			teacherList.put("alternatePhone", eoTeacherUser.alternatePhone);
			teacherList.put("addressLine1", eoTeacherUser.addressLine1);
			teacherList.put("addressLine2", eoTeacherUser.addressLine2);
			teacherList.put("joiningDate", eoTeacherUser.joiningDate);
			teacherList.put("colorCode", eoTeacherUser.colorCode);
			teacherList.put("courseArray", eoTeacherUser.courseArray);
			teacherList.put("salaryType", eoTeacherUser.salaryType);

			if (eoTeacherUser.eoImage != null) {
				teacherList.put("imageUrl", eoTeacherUser.eoImage.imageUrl);
				teacherList.put("eoImage", eoTeacherUser.eoImage.primaryKey);
			}
			
			
			String strqry = "SELECT l.user_name FROM eoteacher_user s LEFT JOIN eologin_details l ON"
					+ " s.eologindetails_primary_key = l.primary_key WHERE"
					+ " s.primary_key = "+eoTeacherUser.primaryKey+"";
			List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(strqry);
			teacherList.put("userName", dataList.get(0).get("user_name"));
			
			if(eoTeacherUser.musicCategoryPk != null){
				/*List<String> pkObj = (List<String>) JSONUtil.jsonToObject(eoTeacherUser.musicCategoryPk, List.class);

				List<HashMap<String, Object>> finalMap = new ArrayList<>();
				int count = 0;
				for (String str : pkObj) {

					String musicCategoryPk = pkObj.get(count);

					String[] arrOfStr = musicCategoryPk.split("_");
					String musicPk = arrOfStr[0];
					String categoryPk = arrOfStr[1];
				
					String qryStr = "select primary_key , music_type FROM lkmusic_type  where is_active =  true AND primary_key ="+musicPk;
					List<HashMap<String, Object>> musicList = DBServices.getNativeQueryResult(qryStr);
		 		
					String qry = "select primary_key, category_type FROM lkcategory_type where is_active =  true AND primary_key ="+categoryPk;
					List<HashMap<String, Object>> categoryList = DBServices.getNativeQueryResult(qry);

					HashMap<String, Object> tempMap = new HashMap<>();
					tempMap.put("musicPk", musicList.get(0).get("primary_key"));
					tempMap.put("categoryPk", categoryList.get(0).get("primary_key"));
					
					tempMap.put("musicCategory", musicList.get(0).get("music_type") + " " + categoryList.get(0).get("category_type"));
					finalMap.add(tempMap);
					count++;
				}
				teacherList.put("musicCategoryList", finalMap);*/
			}
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(teacherList)).build();
	}

	@POST
	@Path("/createCourse")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createCourse(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String className = (String) map.get("className");
		String skip = (String) map.get("skip");
		map.remove("skip");
		String eoBatchPk = "";
		String studentPk = "";
		if (map.get("primaryKey") == null) {
			map.put("createdDate", DateUtil.formatedCurrentDate());
			DBServices.create(EOObject.createObject(map));
		}
		else{
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			EOObject eoObject = EOObject.getObjectByPK(className, (Integer) map.get("primaryKey"));
			DBServices.update(EOObject.updateObject(eoObject, map));
		}

		if (className.equalsIgnoreCase("EOBatch") && skip.equalsIgnoreCase("skip")) {
			EOBatch eobatch = (EOBatch) EOObject.getLatestObject(className);
			eoBatchPk = eobatch.primaryKey + "";

			EOStudentUser eoStudentUser = (EOStudentUser) EOObject.getLatestObject("EOStudentUser");
			studentPk = eoStudentUser.primaryKey + "";

			HashMap<String, Object> finalMap = new HashMap<>();

			finalMap.put("eoBatchPk", eoBatchPk);
			finalMap.put("studentPk", studentPk);
			return Response.status(201).entity(finalMap).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}

	}

	@POST
	@Path("/getCourseList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCourseList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = " SELECT c.primary_key, c.course_name, c.lkcategorytype_primary_key, c.lkmusictype_primary_key, "
					+" c.course_code,c.fees, m.primary_key as musicPk ,m.music_type, t.primary_key as categoryPk,t.description, t.category_type, "
					+" lkd.duration FROM eocourses c LEFT JOIN lkcategory_type t on t.primary_key = c.lkcategorytype_primary_key"
					+" LEFT JOIN lkmusic_type m on m.primary_key = c.lkmusictype_primary_key"
					+" LEFT JOIN lkclass_duration lkd ON lkd.primary_key = CAST(c.lkclassduration_primary_key as bigint)"
					+" where c.is_active = true ORDER BY c.course_name";
		List<HashMap<String, Object>> courseList = DBServices.getNativeQueryResult(str);

		return Response.status(201).entity(JSONUtil.objectToJson(courseList)).build();
	}

	@POST
	@Path("/getCourseByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCourseByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoCoursePK");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("From EOCourses WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> courseList = new HashMap<>();
		for (Object obj : resultSet) {
			EOCourses eoCourses = (EOCourses) obj;

			courseList.put("primaryKey", eoCourses.primaryKey);
			courseList.put("courseCode", eoCourses.courseCode);
			courseList.put("courseName", eoCourses.courseName);
			courseList.put("details", eoCourses.details);
			courseList.put("startDate", eoCourses.startDate);
			courseList.put("session", eoCourses.session);
			courseList.put("lkClassDuration", eoCourses.lkClassDuration.primaryKey + "");
			courseList.put("feeType", eoCourses.feeType);
			courseList.put("fees", eoCourses.fees);
			courseList.put("lkMusicType", eoCourses.lkMusicType.primaryKey + "");
			courseList.put("lkCategoryType", eoCourses.lkCategoryType.primaryKey + "");
			courseList.put("url", eoCourses.url);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(courseList)).build();
	}

	@POST
	@Path("/deleteCourseByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteCourseByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String reason = (String) map.get("reason");
		String coursePk = (String) map.get("coursePk");

		List<Object> obj = DBServices.checkCourse("EOBatch", coursePk);
		if(obj != null && obj.size() > 0){
			return Response.status(201).entity(JSONUtil.objectToJson("failure")).build();		
		}
		else{
			String qry = "UPDATE EOCourses SET is_active = false , removingReason ='"+reason+"' WHERE primaryKey ="+coursePk;
			DBServices.updateNativeQueryResult(qry);
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}
	}

	@POST
	@Path("/getMusicRoomList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicRoomList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = "select primary_key, room_name, room_id, max_student,details from eomusic_room where is_active= true";
		List<HashMap<String, Object>> musicRoomList = DBServices.getNativeQueryResult(str);

		return Response.status(201).entity(JSONUtil.objectToJson(musicRoomList)).build();
	}

	@POST
	@Path("/getMusicRoomByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicRoomByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoMusicRoomPK");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("FROM EOMusicRoom WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> musicRoomList = new HashMap<>();
		for (Object obj : resultSet) {
			EOMusicRoom eoMusicRoom = (EOMusicRoom) obj;

			musicRoomList.put("primaryKey", eoMusicRoom.primaryKey);
			musicRoomList.put("roomName", eoMusicRoom.roomName);
			musicRoomList.put("roomId", eoMusicRoom.roomId);
			musicRoomList.put("details", eoMusicRoom.details);
			musicRoomList.put("maxStudent", eoMusicRoom.maxStudent);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(musicRoomList)).build();
	}

	@POST
	@Path("/deleteMusicRoomByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteMusicRoomByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String reason = (String) map.get("reason");
		String eoMusicRoomPK = (String) map.get("eoMusicRoomPK");
		
		String qry = "UPDATE EOMusicRoom SET is_active = false , removingReason ='"+reason+"' WHERE primaryKey ="+eoMusicRoomPK;
		DBServices.updateNativeQueryResult(qry);
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getBatchByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBatchByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoBatchPK");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("FROM EOBatch WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> batchList = new HashMap<>();
		for (Object obj : resultSet) {
			EOBatch eoBatch = (EOBatch) obj;

			batchList.put("primaryKey", eoBatch.primaryKey);
			batchList.put("batchName", eoBatch.batchName);
			batchList.put("startDate", eoBatch.startDate);
			batchList.put("status", eoBatch.status);
			batchList.put("batchId", eoBatch.batchId);
			batchList.put("eoCourses", eoBatch.eoCourses.primaryKey + "");
			batchList.put("batchType", eoBatch.batchType);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(batchList)).build();
	}

	@POST
	@Path("/getBatchList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBatchList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = "select b.primary_key, b.batch_name, b.batch_id ,b.start_date, b.status, b.eocourses_primary_key, "
				+ " b.batch_type,c.primary_key as coursePk ,c.course_code, c.course_name FROM eobatch b "
				+ " LEFT JOIN eocourses c on b.eocourses_primary_key = c.primary_key where  b.is_active= true";

		List<HashMap<String, Object>> batchList = DBServices.getNativeQueryResult(str);

		return Response.status(201).entity(JSONUtil.objectToJson(batchList)).build();
	}

	@POST
	@Path("/deleteBatchByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBatchByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String reason = (String) map.get("reason");
		String batchPk = (String) map.get("batchPk");
		String className = (String) map.get("className");

		List<Object> objList = DBServices.checkBatchExists("EOStudentBatch", batchPk);

		if (objList != null && objList.size() > 0) {
			return Response.status(201).entity(JSONUtil.objectToJson("failure")).build();
		}
		else{
			if(className.equalsIgnoreCase("EOBatch")){
				String qry = "UPDATE EOBatch SET is_active = false , removingReason ='"+reason+"' WHERE primaryKey ="+batchPk;
				DBServices.updateNativeQueryResult(qry);
			}
			if(className.equalsIgnoreCase("EOStudentBatch")){
				String qry = "UPDATE EOStudentBatch SET is_active = false , removingReason ='"+reason+"' WHERE primaryKey ="+batchPk;
				DBServices.updateNativeQueryResult(qry);
			}
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}
	}

	

	@POST
	@Path("/getBatchesForStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBatchesForStudent(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		List<Object> batchList = DBServices.get("FROM EOStudentBatch WHERE isActive = true");

		List<HashMap<String, Object>> finaList = new ArrayList<>();
		for (Object obj : batchList) {
			HashMap<String, Object> studentBatchList = new HashMap<>();
			EOStudentBatch eoStudentBatch = (EOStudentBatch) obj;

			studentBatchList.put("primaryKey", eoStudentBatch.primaryKey);
			studentBatchList.put("batchName", eoStudentBatch.eoBatch.batchName);
			studentBatchList.put("batchStatus", eoStudentBatch.eoBatch.status);
			studentBatchList.put("eoBatch", eoStudentBatch.eoBatch.primaryKey);
			studentBatchList.put("batchName", eoStudentBatch.eoBatch.batchName);
			studentBatchList.put("batchType", eoStudentBatch.batchType);
			studentBatchList.put("batchId", eoStudentBatch.eoBatch.batchId);
			List<Object> eoStudentBatchMappingList = new ArrayList<>();
			if (eoStudentBatch.eoStudentBatchMapping != null && eoStudentBatch.eoStudentBatchMapping.size() > 0) {

				for (EOStudentBatchMapping studentBatchMapObj : eoStudentBatch.eoStudentBatchMapping) {
					if (studentBatchMapObj.isActive) {
						eoStudentBatchMappingList.add(studentBatchMapObj);
					}
				}

			}
			studentBatchList.put("eoStudentBatchMapping", eoStudentBatchMappingList);

			List<String> pkObj = (List<String>) JSONUtil.jsonToObject(eoStudentBatch.studentsPk, List.class);
			List<HashMap<String, Object>> innerMap = new ArrayList<>();

			int count = 0;
			for (String str : pkObj) {

				String stdPk = pkObj.get(count);

				String query = "SELECT primary_key , CONCAT(first_name, ' ',last_name) as stdfullname FROM eostudent_user"
						+ " WHERE is_active = true AND primary_key = " + stdPk;

				HashMap<String, Object> tempMap = DBServices.getNativeQueryResult(query).get(0);
				HashMap<String, Object> finalMap = new HashMap<>();
				finalMap.put("primaryKey", tempMap.get("primary_key"));
				finalMap.put("studentfullName", tempMap.get("stdfullname"));

				innerMap.add(finalMap);
				count++;
			}
			studentBatchList.put("eoBatchList", innerMap);
			finaList.add(studentBatchList);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(finaList)).build();

	}

	@POST
	@Path("/getMusicAndCategoryType")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicAndCategoryType(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = "select primary_key , music_type FROM lkmusic_type  where is_active =  true";
		List<HashMap<String, Object>> musicList = DBServices.getNativeQueryResult(str);

		String qry = "select primary_key,description, category_type FROM lkcategory_type where is_active =  true";
		List<HashMap<String, Object>> categoryList = DBServices.getNativeQueryResult(qry);

		List<HashMap<String, Object>> finalMap = new ArrayList<>();
		for (HashMap<String, Object> obj1 : musicList) {
			for (HashMap<String, Object> obj2 : categoryList) {
				HashMap<String, Object> tempMap = new HashMap<>();
				tempMap.put("musicPk", obj1.get("primary_key"));
				tempMap.put("categoryPk", obj2.get("primary_key"));

				tempMap.put("musicCategory", obj1.get("music_type") + " " + obj2.get("description"));
				finalMap.add(tempMap);
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(finalMap)).build();
	}

	@POST
	@Path("/updateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Map<String, Object> tmpMap = new HashMap<>(map);
		final String className = String.valueOf(map.get("className"));
		final Integer pk = (Integer) map.get("primaryKey");

		map.remove("loginUserPk");
		tmpMap.remove("loginUserPk");

		EOObject eoObject = EOObject.updateObject(EOObject.getObjectByPK(className, pk), map);
		DBServices.update(eoObject);
		eoObject.updateUserDetail(tmpMap);

		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	/* login profile starts------------------------- */

	@POST
	@Path("/updateLoginUserData")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateLoginUserData(HashMap<String, String> map) {
		String usrName = map.get("usrName");
		String userKey = map.get("userKey");
		String url = "failure";
		boolean isEmail = (usrName.indexOf('@') != -1) ? true : false;
		String qryStr = "", userInfoQuery = "";

		List<HashMap<String, Object>> list = new ArrayList<>();

		if (!isEmail) {
			userInfoQuery = "Select l.role from eologin_details l where l.user_name= '" + usrName + "' ";

			List<HashMap<String, Object>> objList = null;
			objList = DBServices.getNativeQueryResult(userInfoQuery);

			if (objList.size() > 0) {
				HashMap<String, Object> dataMap = objList.get(0);
				String role = String.valueOf(dataMap.get("role"));

				if (role.equalsIgnoreCase("EOTeacherUser")) { // teacher login

					// for teacher----------
					qryStr = "Select t.primary_key, t.email, t.first_name, t.last_name, t.gender,t.address_line_1,t.address_line_2, "
							+ "concat(t.first_name,'',t.last_name) as full_name, t.phone, t.title,t.eoimage_primary_key, "
							+ "l.user_name,l.password, l.role ,eoi.image_url From eoteacher_user t "
							+ "INNER JOIN  eologin_details l  ON t.eologindetails_primary_key= l.primary_key "
							+ "LEFT JOIN eoimage eoi ON t.eoimage_primary_key = eoi.primary_key WHERE "
							+ " t.primary_key =" + userKey;

				}

				if (role.equalsIgnoreCase("EOStudentUser")) { // student login

					// for student
					qryStr = "Select s.primary_key, s.email,s.gender, s.first_name, s.last_name,s.address_line_1,s.address_line_2, "
							+ "concat(s.first_name,'',s.last_name) as full_name, s.phone,s.eoimage_primary_key, "
							+ "l.user_name,l.password, l.role,eoi.image_url From eostudent_user s "
							+ "INNER JOIN  eologin_details l  ON s.eologindetails_primary_key= l.primary_key "
							+ "LEFT JOIN eoimage eoi ON s.eoimage_primary_key = eoi.primary_key WHERE "
							+ " s.primary_key = " + userKey;
				}

			}
		} else {
			qryStr = "SELECT s.primary_key, s.email, s.first_name,s.address_1,s.address_2,s.gender,s.last_name,"
					+ "s.phone,s.prefix,s.eoimage, concat(s.first_name, ' ',  s.last_name) as full_name, eoi.image_url, "
					+ " s.password,s.postal_code,s.security_answer,s.lksecurityquestion_primary_key"
					+ " FROM eomanagement_user s LEFT JOIN eoimage eoi ON s.eoimage = eoi.primary_key WHERE ";
			if (isEmail) {
				qryStr += " s.email = '" + usrName + "'";

			} else {
				if (usrName.matches("^[a-zA-Z0-9]+$")) {
					qryStr += " s.username = '" + usrName + "'";
				} else {
					qryStr += " s.phone = '" + usrName + "'";

				}
			}

		}

		list = DBServices.getNativeQueryResult(qryStr);
		return Response.status(201).entity(JSONUtil.objectToJson(list)).build();
	}

	@POST
	@Path("/getManagementUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getManagementUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("From EOManagementUser").list()) {
			resultSet.add(obj);
		}
		List<HashMap<String, Object>> managementList = new ArrayList<>();
		for (Object obj : resultSet) {
			EOManagementUser eoManagementUser = (EOManagementUser) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", eoManagementUser.primaryKey);
			innerMap.put("gender", eoManagementUser.gender);
			innerMap.put("email", eoManagementUser.email);
			innerMap.put("phone", eoManagementUser.phone);
			if (eoManagementUser.eoImage != null) {
				innerMap.put("imageUrl", eoManagementUser.eoImage.imageUrl);
				innerMap.put("eoImagePk", eoManagementUser.eoImage.primaryKey);
			}
			managementList.add(innerMap);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(managementList)).build();
	}

	@POST
	@Path("/updateProfileObject")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfileObject(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String className = (String) map.get("className");
		String phone = (String) map.get("phone");
		map.remove("imageUrl");
		map.remove("phone");

		EOObject eoObject = EOObject.updateObject(EOObject.getObjectByPK(className, (Integer) map.get("primaryKey")),
				map);

		DBServices.update(eoObject);

		return Response.status(201).entity(JSONUtil.objectToJson("success")).build();
	}
	
	
	public static String createBatchesForStudent(HashMap<String, Object> map) throws ClassNotFoundException,
		NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, 
		IllegalArgumentException, InvocationTargetException{

		String primaryKey = (String) map.get("primaryKey");
		String batchPk = (String) map.get("eoBatch");
		String className = (String) map.get("className");

		List<HashMap<String, Object>> studentBatchMappingList = (List<HashMap<String, Object>>) map
				.get("eoStudentBatchMapping");
		map.remove("eoStudentBatchMapping");
		
		System.out.println("creating student batch and mapping");
		
		//List<Object> objList = DBServices.checkBatchExists(className, batchPk);
		
		if(primaryKey == null ){
		/*if(primaryKey == null && objList == null){*/
			map.put("createdDate", DateUtil.formatedCurrentDate());
			DBServices.create(EOObject.createObject(map));
			EOStudentBatch eoStudentBatch = (EOStudentBatch) EOObject.getLatestObject(className);
			for (HashMap<String, Object> sbMap : studentBatchMappingList) {
				sbMap.put("batchPk", batchPk);
				sbMap.put("eoStudentBatch", eoStudentBatch.primaryKey);
				DBServices.create(EOObject.createObject(sbMap));
			}
		}
		else{
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			EOStudentBatch eoStudentBatch= (EOStudentBatch)DBServices.getObjectBycolumnName(className, "eoBatch" , batchPk);
			DBServices.update(EOObject.updateObject(eoStudentBatch, map));
			for (HashMap<String, Object> sbMap : studentBatchMappingList) {
				if (sbMap.get("primaryKey") != null) {
					EOStudentBatchMapping eostdBatMap = (EOStudentBatchMapping) EOObject
							.getObjectByPK("EOStudentBatchMapping", (Integer) sbMap.get("primaryKey"));
					DBServices.update(EOObject.updateObject(eostdBatMap, sbMap));
				} else {
					sbMap.put("batchPk", batchPk);
					sbMap.put("eoStudentBatch", eoStudentBatch.primaryKey);
					DBServices.create(EOObject.createObject(sbMap));
				}
			}
		}
		return "Success";

	}

	@POST
	@Path("/createBatchAndStudentBatch")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createBatchAndStudentBatch(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String response = this.createBatches(map);

		return Response.status(201).entity(JSONUtil.objectToJson(response)).build();

	}
	
	public static String createBatches(HashMap<String, Object> map) throws ClassNotFoundException,
		NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, 
		IllegalArgumentException, InvocationTargetException{
		
		List<HashMap<String, Object>> coursePkList = (List<HashMap<String, Object>>) map.get("courseList");
		String type = (String) map.get("type");
		String stdPk = (String) map.get("studentPk");
		//String formType = (String) map.get("formType");
		String studentPk = "";
		if(type.equalsIgnoreCase("create")){
			String query = "SELECT max(primary_key) as primary_key from eostudent_user where is_active = true";
			studentPk = DBServices.getNativeQueryResult(query).get(0).get("primary_key") + "";
			
			/*if(formType.equalsIgnoreCase("formType")){
				String query = "SELECT max(primary_key) as primary_key from eoquery_form where is_active = true";
				studentPk = DBServices.getNativeQueryResult(query).get(0).get("primary_key") + "";
				System.out.println("studentPk:"+studentPk);
			}else{
				String query = "SELECT max(primary_key) as primary_key from eostudent_user where is_active = true";
				studentPk = DBServices.getNativeQueryResult(query).get(0).get("primary_key") + "";
			}*/
			
		}else{
			studentPk = stdPk;
		}
		

		for (Object obj : coursePkList) {
			String qry = "SELECT MAX(batch_seq) as batch_seq FROM eobatch WHERE is_active = true";
			String str = "SELECT MAX(batch_id_seq) as batch_id_seq FROM eobatch WHERE is_active = true";
			List<HashMap<String, Object>> batchSeq = DBServices.getNativeQueryResult(qry);
			List<HashMap<String, Object>> batchIdSeq = DBServices.getNativeQueryResult(str);

			String batchId = "";
			String batchName = "";
			HashMap<String, Object> batchMap = new HashMap<>();
			
			if(batchSeq.get(0).get("batch_seq") != null && batchIdSeq.get(0).get("batch_id_seq") != null){
				int batchIdSeqValue =  (int)(double)batchIdSeq.get(0).get("batch_id_seq");
				batchId = "A"+ (batchIdSeqValue + 1);
				int batchValue =  (int)(double)batchSeq.get(0).get("batch_seq");
				batchName = "SMA-B-"+ (batchValue + 1);
				batchMap.put("batchIdSeq", batchIdSeqValue + 1);
				batchMap.put("batchSeq", batchValue + 1);
			}
			
			else{
				batchId = "A1";
				batchName = "SMA-B-1";
				batchMap.put("batchIdSeq", 1);
				batchMap.put("batchSeq", 1);
			}
			batchMap.put("startDate", DateUtil.formatedCurrentDate());
			batchMap.put("batchName", batchName);
			batchMap.put("status", "Active");
			batchMap.put("eoCourses", obj);
			batchMap.put("batchId", batchId);
			batchMap.put("batchType", "Individual");
			batchMap.put("className", "EOBatch");
			/*if(formType.equalsIgnoreCase("query")){
				batchMap.put("isActive", false);
			}*/
			
			batchMap.put("createdDate", DateUtil.formatedCurrentDate());
			DBServices.create(EOObject.createObject(batchMap));

			String batchQry = "SELECT MAX(primary_key) batchpk FROM eobatch WHERE is_active = true";
			List<HashMap<String, Object>> batchPk = DBServices.getNativeQueryResult(batchQry);

			HashMap<String, Object> studentBatch = new HashMap<>();
			List<String> studentPkList = new ArrayList<>();
			studentPkList.add(studentPk + "");
			studentBatch.put("studentsPk", JSONUtil.objectToJson(studentPkList));
			studentBatch.put("eoBatch", batchPk.get(0).get("batchpk") + "");
			studentBatch.put("batchType", "Individual");
			studentBatch.put("className", "EOStudentBatch");

			HashMap<String, Object> tempMap = new HashMap<>();
			tempMap.put("studentPk", studentPk + "");
			tempMap.put("isActive", true);
			/*if(formType.equalsIgnoreCase("query")){
				batchMap.put("isActive", false);
			}else{
				tempMap.put("isActive", true);
			}*/
			tempMap.put("className", "EOStudentBatchMapping");

			List<HashMap<String, Object>> mappingList = new ArrayList<>();
			mappingList.add(tempMap);
			studentBatch.put("eoStudentBatchMapping", mappingList);

			createBatchesForStudent(studentBatch);
		}
		return "Success";
	}
	
	@POST
	@Path("/deleteBatchAndStudentBatch")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBatchAndStudentBatch(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<String> batchArray = (List<String>)map.get("batchArray");
		if(batchArray != null && batchArray.size() > 0){
			String batchPk = "(";
			int count = 0;
			for(String newObj : batchArray){
				batchPk += "'"+newObj+"'";
				count++;
				if(count < batchArray.size()){
					batchPk += ",";
				}
			}
			batchPk += ")";
			
			System.out.println("batchPk:::"+batchPk);
			
			DBServices.updateNativeQueryResult("UPDATE EOBatch SET is_active= false WHERE primary_key IN "+batchPk);
			DBServices.updateNativeQueryResult("UPDATE EOStudentBatch SET is_active= false WHERE eobatch_primary_key IN "+batchPk);
			DBServices.updateNativeQueryResult("UPDATE EOStudentBatchMapping SET is_active= false WHERE batch_pk IN "+batchPk);
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getMaxPrimaryKey")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMaxPrimaryKey(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String className = (String)map.get("className");
		HashMap<String, Object> dataMap = DBServices.getMaxPrimaryKey(className);
		return Response.status(201).entity(JSONUtil.objectToJson(dataMap)).build();
		
	}
	
	@POST
	@Path("/getMusicTypeList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicTypeList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = "select primary_key, music_type, is_active from lkmusic_type where is_active= true order by music_type";
		List<HashMap<String, Object>> musicRoomList = DBServices.getNativeQueryResult(str);
		List<HashMap<String, Object>> finalList = new ArrayList<>();
		for(HashMap<String,Object> obj : musicRoomList){
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", obj.get("primary_key"));
			innerMap.put("musicType", obj.get("music_type"));
			innerMap.put("isActive", obj.get("is_active"));
			
			finalList.add(innerMap);
		}
		

		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}
	/*vishuja*/
	@POST
	@Path("/createMusicType")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createMusicType(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//String className = (String) map.get("className");
		//map.put("isActive", true);
		System.out.println("classname:::"+map);
		if (map.get("primaryKey") == null) {
			map.put("className", "LKMusicType");
			map.put("isActive", true);
			map.put("createdDate", DateUtil.formatedCurrentDate());
			DBServices.create(EOObject.createObject(map));
		}
		else{
			map.put("className", "LKMusicType");
			map.put("isActive", true);
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			EOObject eoObject = EOObject.getObjectByPK("LKMusicType", (Integer) map.get("primaryKey"));
			DBServices.update(EOObject.updateObject(eoObject, map));
		}
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();

	}
	
	/*vishuja code*/
	@POST
	@Path("/getMusicTypeByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicTypeByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoMusicTypePK");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("FROM LKMusicType WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> musicTypeList = new HashMap<>();
		for (Object obj : resultSet) {
			LKMusicType lkMusicType = (LKMusicType) obj;

			musicTypeList.put("primaryKey", lkMusicType.primaryKey);
			musicTypeList.put("musicType", lkMusicType.musicType);
			musicTypeList.put("isActive", lkMusicType.isActive);
			
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(musicTypeList)).build();
	}
	

	
	@POST
	@Path("/deleteMusicTypeByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteMusicTypeByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String reason = (String) map.get("reason");
		
		String eoMusicTypePK = (String) map.get("eoMusicTypePK");
		
		String qryStr ="select lkmusictype_primary_key  From eocourses where is_active = true and  lkmusictype_primary_key ="+ eoMusicTypePK;
		List<HashMap<String, Object>> list = DBServices.getNativeQueryResult(qryStr);
		
		System.out.println("listvaluee:::"+list);
		
		if (list != null && list.size() > 0) {
			return Response.status(201).entity(JSONUtil.objectToJson("failure")).build();
		}else{
			 String qry = "UPDATE LKMusicType SET is_active = false , removingReason ='"+reason+"' WHERE primaryKey ="+eoMusicTypePK;
			  DBServices.updateNativeQueryResult(qry);
			  return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}
		
		
	}
	
	/*vishuja code*/
	
	@POST
	@Path("/getMusicCategoryList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicCategoryList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = "select primary_key, category_type, is_active, description from lkcategory_type where is_active= true ORDER by description";
		List<HashMap<String, Object>> musicRoomList = DBServices.getNativeQueryResult(str);
		List<HashMap<String, Object>> finalList = new ArrayList<>();
		for(HashMap<String,Object> obj : musicRoomList){
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", obj.get("primary_key"));
			innerMap.put("categoryType", obj.get("category_type"));
			innerMap.put("isActive", obj.get("is_active"));
			innerMap.put("description", obj.get("description"));
			
			finalList.add(innerMap);
		}
		

		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}
	
	@POST
	@Path("/createMusicCategory")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createMusicCategory(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//String className = (String) map.get("className");
		//map.put("isActive", true);
		System.out.println("classname:::"+map);
		if (map.get("primaryKey") == null) {
			map.put("className", "LKCategoryType");
			map.put("isActive", true);
			map.put("createdDate", DateUtil.formatedCurrentDate());
			DBServices.create(EOObject.createObject(map));
		}
		else{
			map.put("className", "LKCategoryType");
			map.put("isActive", true);
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			EOObject eoObject = EOObject.getObjectByPK("LKCategoryType", (Integer) map.get("primaryKey"));
			DBServices.update(EOObject.updateObject(eoObject, map));
		}
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();

	}
	
	@POST
	@Path("/getMusicCategoryByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicCategoryByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String pk = (String) map.get("eoMusicCategoryPK");

		List<Object> resultSet = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		for (Object obj : session.createQuery("FROM LKCategoryType WHERE primaryKey=" + pk).list()) {
			resultSet.add(obj);
		}
		HashMap<String, Object> musicTypeList = new HashMap<>();
		for (Object obj : resultSet) {
			LKCategoryType lkMusicType = (LKCategoryType) obj;

			musicTypeList.put("primaryKey", lkMusicType.primaryKey);
			musicTypeList.put("categoryType", lkMusicType.categoryType);
			musicTypeList.put("description", lkMusicType.description);
			musicTypeList.put("isActive", lkMusicType.isActive);
			
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(musicTypeList)).build();
	}
	
	@POST
	@Path("/deleteMusicCategoryByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteMusicCategoryByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String reason = (String) map.get("reason");
		
		String eoMusicCategoryPK = (String) map.get("eoMusicCategoryPK");
		
		String qryStr ="select lkcategorytype_primary_key  From eocourses where is_active = true and  lkcategorytype_primary_key ="+ eoMusicCategoryPK;
		List<HashMap<String, Object>> list = DBServices.getNativeQueryResult(qryStr);
		
		if (list != null && list.size() > 0) {
			return Response.status(201).entity(JSONUtil.objectToJson("failure")).build();
		}else{
			 String qry = "UPDATE LKCategoryType SET is_active = false , removingReason ='"+reason+"' WHERE primaryKey ="+eoMusicCategoryPK;
			  DBServices.updateNativeQueryResult(qry);
			  return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}
		
		
	}
	
	@POST
	@Path("/getTrialStudentUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTrialStudentUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<Object> studentObjList = DBServices
				.get("From EOStudentUser WHERE is_active = true AND is_trial = true  order by updated_date desc ");
		List<HashMap<String, Object>> studentList = new ArrayList<>();

		for (Object obj : studentObjList) {
			EOStudentUser eoStudentUser = (EOStudentUser) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", eoStudentUser.primaryKey);
			innerMap.put("studentfullName", eoStudentUser.getFullName());
			innerMap.put("studentfullNameJapanese", eoStudentUser.getFullNameJapanese());
			innerMap.put("gender", eoStudentUser.gender);
			innerMap.put("enrollmentDate", eoStudentUser.enrollmentDate);
			innerMap.put("email", eoStudentUser.email);
			innerMap.put("studentId", eoStudentUser.studentId);
			innerMap.put("phone", eoStudentUser.phone);
			innerMap.put("isVisible", eoStudentUser.isVisible);
			innerMap.put("registrationAmount", eoStudentUser.registrationAmount);
			innerMap.put("updatedDate", eoStudentUser.updatedDate);
			innerMap.put("studentId", eoStudentUser.studentId);
			if (eoStudentUser.eoImage != null) {
				innerMap.put("imageUrl", eoStudentUser.eoImage.imageUrl);
				innerMap.put("eoImagePk", eoStudentUser.eoImage.primaryKey);
			}
			studentList.add(innerMap);
		}
		return Response.status(201).entity(JSONUtil.objectToJson(studentList)).build();

	}

	


}
