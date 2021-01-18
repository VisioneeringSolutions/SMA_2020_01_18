package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.mapping.Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.model.eo.EOGenerateSlip;
import com.vspl.music.model.eo.EOGenerateSlipDetail;
import com.vspl.music.model.eo.EOGenerateSlipStudentDetail;
import com.vspl.music.model.eo.EOQueryForm;
import com.vspl.music.model.eo.EOStudentInvoiceDetail;
import com.vspl.music.model.eo.EOStudentInvoiceEditable;
import com.vspl.music.model.eo.EOStudentInvoiceMain;
import com.vspl.music.model.eo.EOStudentUser;
import com.vspl.music.model.eo.EOTeacherSalary;
import com.vspl.music.model.eo.EOTeacherSalaryDetail;
import com.vspl.music.model.eo.EOTeacherUser;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.JSONUtil;

@Path("/ajaxReport")
public class ReportController {

	public static Logger logger = LoggerFactory.getLogger(ReportController.class);

	@POST
	@Path("/getStudentUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		List<Object> studentObjList = DBServices.get("From EOStudentUser WHERE is_active = true");

		List<HashMap<String, Object>> studentList = new ArrayList<>();
		for (Object obj : studentObjList) {
			EOStudentUser eoStudentUser = (EOStudentUser) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			
			innerMap.put("primaryKey", eoStudentUser.primaryKey + "");
			innerMap.put("studentfullName", eoStudentUser.getFullName());
			innerMap.put("gender", eoStudentUser.gender);
			innerMap.put("enrollmentDate", eoStudentUser.enrollmentDate);
			innerMap.put("email", eoStudentUser.email);
			innerMap.put("studentId", eoStudentUser.studentId);
			innerMap.put("phone", eoStudentUser.phone);

			String batchQry = "SELECT music.music_type, " + " b.batch_name,c.course_name FROM  eobatch b "
					+ " inner join eocourses c on b.eocourses_primary_key = c.primary_key  "
					+ " inner join eostudent_batch_mapping m on CAST(m.batch_pk as bigint) = b.primary_key "
					+ " inner join lkmusic_type music on c.lkmusictype_primary_key = music.primary_key"
					+ " where m.is_active = true and m.student_pk = '" + eoStudentUser.primaryKey + "'";
			List<HashMap<String, Object>> batchList = DBServices.getNativeQueryResult(batchQry);

			if (batchList != null && batchList.size() > 0) {
				int count = 0;
				String batches = "", courses = "";
				
				Set<String> s = new LinkedHashSet<String>();  
				
				for (HashMap<String, Object> m : batchList) {

					
					
					if (count == 0) {
						batches += (String) m.get("batch_name");
						courses += (String) m.get("course_name");
						s.add(((String) m.get("music_type")).trim());

					} else {
						batches += ", " + (String) m.get("batch_name");
						courses += ", " + (String) m.get("course_name");
						s.add(((String) m.get("music_type")).trim());
					}
					count++;

				}
			
				innerMap.put("batchName", batches);
				innerMap.put("courseName", courses);
				innerMap.put("musicType", s);
			}

			studentList.add(innerMap);
		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(studentList)).build();

	}
	
	/* Vishuja added */

	@POST
	@Path("/getStudentDetailsForInvoice")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentDetailsForInvoice(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		List<HashMap<String, Object>> studentList = new ArrayList<>();
		/* List<Object> dataListMain = DBServices.get("FROM EOStudentInvoiceMain"); */
		
		  String query ="SELECT DISTINCT eostudentuser_primary_key FROM eostudent_invoice_main ";
		 
		
		  List<HashMap<String, Object>> dataListMain = DBServices.getNativeQueryResult(query);
		 
		if (dataListMain != null && dataListMain.size() > 0) {
			for (HashMap<String, Object> obj  : dataListMain) {

				/* EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) obj; */
				
				/* String pk = eoStudentInvoiceMain.eoStudentUser.primaryKey+""; */
				String pk = obj.get("eostudentuser_primary_key")+"";
				List<Object> studentObjList = DBServices.get("From EOStudentUser WHERE primaryKey = '"+pk+"'");
				
				for (Object objec : studentObjList) {
					EOStudentUser eoStudentUser = (EOStudentUser) objec;
					HashMap<String, Object> innerMap = new HashMap<>();
					
					innerMap.put("primaryKey", eoStudentUser.primaryKey + "");
					innerMap.put("studentfullName", eoStudentUser.getFullName());
					innerMap.put("gender", eoStudentUser.gender);
					innerMap.put("enrollmentDate", eoStudentUser.enrollmentDate);
					innerMap.put("email", eoStudentUser.email);
					innerMap.put("studentId", eoStudentUser.studentId);
					innerMap.put("phone", eoStudentUser.phone);

					String batchQry = "SELECT music.music_type, " + " b.batch_name,c.course_name FROM  eobatch b "
							+ " inner join eocourses c on b.eocourses_primary_key = c.primary_key  "
							+ " inner join eostudent_batch_mapping m on CAST(m.batch_pk as bigint) = b.primary_key "
							+ " inner join lkmusic_type music on c.lkmusictype_primary_key = music.primary_key"
							+ " where m.is_active = true and m.student_pk = '" + eoStudentUser.primaryKey + "'";
					List<HashMap<String, Object>> batchList = DBServices.getNativeQueryResult(batchQry);

					if (batchList != null && batchList.size() > 0) {
						int count = 0;
						String batches = "", courses = "";
						
						Set<String> s = new LinkedHashSet<String>();  
						
						for (HashMap<String, Object> m : batchList) {

							
							
							if (count == 0) {
								batches += (String) m.get("batch_name");
								courses += (String) m.get("course_name");
								s.add(((String) m.get("music_type")).trim());

							} else {
								batches += ", " + (String) m.get("batch_name");
								courses += ", " + (String) m.get("course_name");
								s.add(((String) m.get("music_type")).trim());
							}
							count++;

						}
					
						innerMap.put("batchName", batches);
						innerMap.put("courseName", courses);
						innerMap.put("musicType", s);
					}

					studentList.add(innerMap);
				}
			}
		}
	

	
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(studentList)).build();

	}

	@POST
	@Path("/getTeacherUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTeacherUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		List<Object> teacherObjList = DBServices.get("From EOTeacherUser WHERE is_active = true ORDER BY firstName");

		List<HashMap<String, Object>> teacherList = new ArrayList<>();
		for (Object obj : teacherObjList) {
			EOTeacherUser eoTeacherUser = (EOTeacherUser) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", eoTeacherUser.primaryKey);
			innerMap.put("teacherFullName", eoTeacherUser.getFullName());
			innerMap.put("teacherId", eoTeacherUser.teacherId);
			innerMap.put("qualification", eoTeacherUser.qualification);
			innerMap.put("experience", eoTeacherUser.experience);
			innerMap.put("profile", eoTeacherUser.profile);
			innerMap.put("email", eoTeacherUser.email);
			innerMap.put("phone", eoTeacherUser.phone);
			innerMap.put("alternatePhone", eoTeacherUser.alternatePhone);
			innerMap.put("addressLine1", eoTeacherUser.addressLine1);
			innerMap.put("addressLine2", eoTeacherUser.addressLine2);
			innerMap.put("joiningDate", eoTeacherUser.joiningDate);

			if (eoTeacherUser.musicCategoryPk != null) {
				List<String> pkObj = (List<String>) JSONUtil.jsonToObject(eoTeacherUser.musicCategoryPk, List.class);

				int count = 0;
				String musicCategory = "";
				for (String str : pkObj) {

					String musicCategoryPk = pkObj.get(count);

					String[] arrOfStr = musicCategoryPk.split("_");
					String musicPk = arrOfStr[0];
					String categoryPk = arrOfStr[1];

					//System.out.println("musicPk:"+musicPk);
					//System.out.println("categoryPk:"+categoryPk);
					String qryStr = "select primary_key , music_type FROM lkmusic_type  where primary_key ="
							+ musicPk;
					//System.out.println("qryStr::"+qryStr);
					List<HashMap<String, Object>> musicList = DBServices.getNativeQueryResult(qryStr);

					String qry = "select primary_key, description FROM lkcategory_type where primary_key ="
							+ categoryPk;
					//System.out.println("qry::"+qry);
					List<HashMap<String, Object>> categoryList = DBServices.getNativeQueryResult(qry);

					if (count == 0) {
						musicCategory += musicList.get(0).get("music_type") + " "
								+ categoryList.get(0).get("description");
					} else {
						musicCategory += ", " + musicList.get(0).get("music_type") + " "
								+ categoryList.get(0).get("description");
					}
					count++;
				}
				innerMap.put("musicCategory", musicCategory);
			}

			teacherList.add(innerMap);
			//System.out.println("teacherList:" + teacherList);

		}
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(teacherList)).build();
	}

	@POST
	@Path("/getStudentRatingByMusicTypeAndPk")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentRatingByMusicTypeAndPk(HashMap<String, Object> map) {

		Integer studentPk = Integer.parseInt((String) map.get("studentPk"));
		Integer musicPk = Integer.parseInt((String) map.get("musicPk"));

		String[] monthArr = DateUtil.janToDecMonthArr;
		String year = map.get("year") != null ? (String) map.get("year") : DateUtil.currentYear() + "";
		HashMap<Object, Object> resultMap = new HashMap<>();
		int count = 0;
		for (String month : monthArr) {
			HashMap<String, Object> monthMap = new HashMap<>();
			if (month.equalsIgnoreCase(DateUtil.currMonth()) && year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
				monthMap = countStudentByPkRating(month, year, studentPk, musicPk);
				resultMap.put(count++, monthMap);
				break;
			}
			monthMap = countStudentByPkRating(month, year, studentPk, musicPk);
			resultMap.put(count++, monthMap);

		}
		return Response.status(200).entity(JSONUtil.objectToJson(resultMap)).build();
	}

	private HashMap<String, Object> countStudentByPkRating(String month, String year, Integer studentPk,
			Integer musicPk) {

		String qryStr = "Select SUM(r.avg_opted_rating) as rating FROM EOSTUDENT_RATING r inner join EODEFINED_SLOT s "
				+ " on r.eodefinedslot_primary_key = s.primary_key" + " where s.lkmusictype_primary_key = " + musicPk
				+ " and r.month = '" + month + "' and r.year = '" + year + "' and r.eostudentuser_primary_key = "
				+ studentPk;

		List<HashMap<String, Object>> ratingList = DBServices.getNativeQueryResult(qryStr);

		HashMap<String, Object> returnMap = new HashMap<>();

		double maxRating = 0.0;
		if (ratingList != null && ratingList.size() > 0) {
			HashMap<String, Object> tmpMap = ratingList.get(0);
			maxRating = tmpMap.get("rating") != null ? ((BigDecimal) tmpMap.get("rating")).doubleValue() : 0.0;

		}
		returnMap.put("maxRating", maxRating);
		returnMap.put("month", month);
		return returnMap;

	}

	@POST
	@Path("/getTeacherRatingByMusicTypeAndPk")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeacherRatingByMusicTypeAndPk(HashMap<String, Object> map) {

		Integer teacherPk = Integer.parseInt((String) map.get("teacherPk"));
		Integer musicPk = Integer.parseInt((String) map.get("musicPk"));

		String[] monthArr = DateUtil.janToDecMonthArr;
		String year = map.get("year") != null ? (String) map.get("year") : DateUtil.currentYear() + "";
		HashMap<Object, Object> resultMap = new HashMap<>();
		int count = 0;
		for (String month : monthArr) {
			HashMap<String, Object> monthMap = new HashMap<>();
			if (month.equalsIgnoreCase(DateUtil.currMonth()) && year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
				monthMap = countTeacherByPkRating(month, year, teacherPk, musicPk);
				resultMap.put(count++, monthMap);
				break;
			}
			monthMap = countTeacherByPkRating(month, year, teacherPk, musicPk);
			resultMap.put(count++, monthMap);

		}
		return Response.status(200).entity(JSONUtil.objectToJson(resultMap)).build();
	}

	private HashMap<String, Object> countTeacherByPkRating(String month, String year, Integer teacherPk,
			Integer musicPk) {

		String qryStr = "Select SUM(r.avg_opted_rating) as rating FROM EOTEACHER_RATING r inner join EODEFINED_SLOT s "
				+ " on r.eodefinedslot_primary_key = s.primary_key" + " where s.lkmusictype_primary_key = " + musicPk
				+ " and r.month = '" + month + "' and r.year = '" + year + "' and r.eoteacheruser_primary_key = "
				+ teacherPk;

		List<HashMap<String, Object>> ratingList = DBServices.getNativeQueryResult(qryStr);

		HashMap<String, Object> returnMap = new HashMap<>();

		double maxRating = 0.0;
		if (ratingList != null && ratingList.size() > 0) {
			HashMap<String, Object> tmpMap = ratingList.get(0);
			maxRating = tmpMap.get("rating") != null ? ((BigDecimal) tmpMap.get("rating")).doubleValue() : 0.0;

		}
		returnMap.put("maxRating", maxRating);
		returnMap.put("month", month);
		return returnMap;

	}

	@POST
	@Path("/getSalaryDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSalaryDetails(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String teacherPk = (String) map.get("teacherPk");
		String year = (String) map.get("year");

		/*List<Object> dataListMain = DBServices.get("FROM EOTeacherSalaryDetail WHERE isActive = true AND eoTeacherUser = "
				+ teacherPk + " AND year ='" + year + "' order by createdDate");*/

		List<HashMap<String, Object>> mainResultList = new ArrayList<>();
		
		List<Object> mainTeacherList = DBServices.get("FROM EOGenerateSlip WHERE isActive = true AND eoTeacherUser = "
				+ teacherPk + " AND year ='" + year + "' order by createdDate");
		
		if (mainTeacherList != null && mainTeacherList.size() > 0) {
			//List<HashMap<String, Object>> tempList = new ArrayList<>();
			for(Object obj : mainTeacherList){
				EOGenerateSlip eoGenerateSlip = (EOGenerateSlip) obj;
				HashMap<String, Object> mainMap = new HashMap<>();
				
				mainMap.put("primaryKey", eoGenerateSlip.primaryKey+"");
				mainMap.put("eoTeacherUser", eoGenerateSlip.eoTeacherUser.primaryKey+"");
				mainMap.put("joiningDate", eoGenerateSlip.eoTeacherUser.joiningDate);
				mainMap.put("teacherfullname", eoGenerateSlip.eoTeacherUser.getFullName());
				mainMap.put("teacherfullnamePdf", eoGenerateSlip.eoTeacherUser.firstNameJap +"_"+eoGenerateSlip.eoTeacherUser.lastNameJap);
				mainMap.put("salaryType", eoGenerateSlip.salaryType);
				mainMap.put("status", eoGenerateSlip.status);
				mainMap.put("transportAmount", eoGenerateSlip.transportAmount);
				mainMap.put("transportDays", eoGenerateSlip.transportDays);
				mainMap.put("amount", eoGenerateSlip.amount);
				mainMap.put("add", eoGenerateSlip.add);
				mainMap.put("sub", eoGenerateSlip.sub);
				mainMap.put("month", eoGenerateSlip.month);
				mainMap.put("year", eoGenerateSlip.year);
				mainMap.put("totalSalary", eoGenerateSlip.totalSalary);
				mainMap.put("teacherRemarks", eoGenerateSlip.teacherRemarks);
				
				List<Object> salaryDetails = DBServices.get("FROM EOGenerateSlipDetail WHERE isActive = true AND eogenerateslip_primary_key ="+eoGenerateSlip.primaryKey+"");
				if(salaryDetails.size() > 0 && salaryDetails != null){
					List<HashMap<String, Object>> coursedetails = new ArrayList<>();
					for(Object detail : salaryDetails){
						
						HashMap<String, Object> tempMap = new HashMap<>();
						EOGenerateSlipDetail eoGenerateSlipDetail = (EOGenerateSlipDetail) detail;
						tempMap.put("primaryKey", eoGenerateSlipDetail.primaryKey);
						tempMap.put("amount", eoGenerateSlipDetail.amount);
						tempMap.put("lkCategoryType", eoGenerateSlipDetail.lkCategoryType.primaryKey);
						tempMap.put("lkClassDuration", eoGenerateSlipDetail.lkClassDuration.primaryKey);
						tempMap.put("lkMusicType", eoGenerateSlipDetail.lkMusicType.primaryKey);
						tempMap.put("description", eoGenerateSlipDetail.lkCategoryType.description);
						tempMap.put("duration", eoGenerateSlipDetail.lkClassDuration.duration);
						tempMap.put("musicType", eoGenerateSlipDetail.lkMusicType.musicType);
						tempMap.put("session", eoGenerateSlipDetail.session);
						tempMap.put("totalMins", eoGenerateSlipDetail.totalMins);
						
						coursedetails.add(tempMap);
					}
					mainMap.put("courseData", coursedetails);
				}
				
				List<Object> studentDataList = DBServices.get("FROM EOGenerateSlipStudentDetail WHERE isActive = true AND eogenerateslip_primary_key ="+eoGenerateSlip.primaryKey+"");
				if(studentDataList.size() > 0 && studentDataList != null){
					List<HashMap<String, Object>> studentData = new ArrayList<>();
					for(Object detail : studentDataList){
						
						HashMap<String, Object> tempMap = new HashMap<>();
						EOGenerateSlipStudentDetail eoGenerateSlipStudentDetail = (EOGenerateSlipStudentDetail) detail;
						tempMap.put("primaryKey", eoGenerateSlipStudentDetail.primaryKey);
						tempMap.put("date", eoGenerateSlipStudentDetail.date);
						tempMap.put("lkCategoryType", eoGenerateSlipStudentDetail.lkCategoryType.primaryKey);
						tempMap.put("lkMusicType", eoGenerateSlipStudentDetail.lkMusicType.primaryKey);
						tempMap.put("musicType", eoGenerateSlipStudentDetail.lkMusicType.musicType);
						tempMap.put("description", eoGenerateSlipStudentDetail.lkCategoryType.description);
						tempMap.put("duration", eoGenerateSlipStudentDetail.duration);
						tempMap.put("day", eoGenerateSlipStudentDetail.day);	
						tempMap.put("endTime", eoGenerateSlipStudentDetail.endTime);
						if(eoGenerateSlipStudentDetail.eoMusicRoom != null){
							tempMap.put("eoMusicRoom", eoGenerateSlipStudentDetail.eoMusicRoom.primaryKey);
							tempMap.put("roomName", eoGenerateSlipStudentDetail.eoMusicRoom.roomName);
						}
						
						tempMap.put("eoSlot", eoGenerateSlipStudentDetail.eoSlot);
						tempMap.put("month", eoGenerateSlipStudentDetail.month);
						tempMap.put("startTime", eoGenerateSlipStudentDetail.startTime);
						tempMap.put("year", eoGenerateSlipStudentDetail.year);
						tempMap.put("eoStudentUser", eoGenerateSlipStudentDetail.eoStudentUser.primaryKey);
						tempMap.put("studFullName", eoGenerateSlipStudentDetail.eoStudentUser.getFullName());
						
						studentData.add(tempMap);
					}
					mainMap.put("studentList", studentData);
				}
				
				mainResultList.add(mainMap);
			}
		}

		return Response.status(201).entity(JSONUtil.objectToJson(mainResultList)).build();

	}

	@POST
	@Path("/getMusicSessionDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMusicSessionDetails(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String year = (String) map.get("year");
		String month = (String) map.get("month");
		String musicPk = (String) map.get("musicPk");

		String qry = "";

		if (month != null && musicPk != null) {
			qry = "Select s.slot_pk, s.month, s.year,m.music_type  FROM eodefined_slot s inner join lkmusic_type m "
					+ " on s.lkmusictype_primary_key = m.primary_key "
					+ " WHERE s.is_active = true AND s.lkmusictype_primary_key =" + musicPk + " AND s.month = '" + month
					+ "' AND s.year = '" + year + "'" + " order by s.date";
		} else if (month != null && musicPk == null) {
			qry = "Select s.slot_pk, s.month, s.year,m.music_type  FROM eodefined_slot s inner join lkmusic_type m "
					+ " on s.lkmusictype_primary_key = m.primary_key " + " WHERE s.is_active = true "
					+ " AND s.month = '" + month + "'  AND s.year = '" + year + "' order by s.date";
		} else if (musicPk != null && month == null) {
			qry = "Select s.slot_pk, s.month, s.year,m.music_type  FROM eodefined_slot s inner join lkmusic_type m "
					+ " on s.lkmusictype_primary_key = m.primary_key " + " WHERE s.is_active = true "
					+ " AND s.year = '" + year + "' AND s.lkmusictype_primary_key =" + musicPk + " order by s.date";

		} else {
			qry = "Select s.slot_pk, s.month, s.year,m.music_type  FROM eodefined_slot s inner join lkmusic_type m "
					+ " on s.lkmusictype_primary_key = m.primary_key " + " WHERE s.is_active = true "
					+ " AND s.year = '" + year + "' order by s.date";
		}

		List<HashMap<String, Object>> musicSessionList = DBServices.getNativeQueryResult(qry);
		HashMap<Object, HashMap<String, Object>> resultMap = new HashMap<>();
		if (musicSessionList != null && musicSessionList.size() > 0) {
			for (HashMap<String, Object> tmpMap : musicSessionList) {
				List<String> slots = (List<String>) JSONUtil.jsonToObject((String) tmpMap.get("slot_pk"), List.class);
				if (resultMap.containsKey((String) tmpMap.get("music_type"))) {
					HashMap<String, Object> objMap = resultMap.get((String) tmpMap.get("music_type"));
					Integer slot45 = (Integer) objMap.get("slot45");
					Integer slot60 = (Integer) objMap.get("slot60");
					objMap.put("slot45", slots.size() == 3 ? slot45 + 1 : slot45);
					objMap.put("slot60", slots.size() == 4 ? slot60 + 1 : slot60);

				} else {
					HashMap<String, Object> objMap = new HashMap<>();
					objMap.put("musicType", (String) tmpMap.get("music_type"));
					objMap.put("slot45", slots.size() == 3 ? 1 : 0);
					objMap.put("slot60", slots.size() == 4 ? 1 : 0);
					resultMap.put((String) tmpMap.get("music_type"), objMap);
				}

			}
		}

		return Response.status(201).entity(JSONUtil.objectToJson(resultMap)).build();

	}

	@POST
	@Path("/getPaymentDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPaymentDetails(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String studentPk = (String) map.get("studentPk");
		String year = (String) map.get("year");

		List<Object> dataListMain = DBServices
				.get("FROM EOStudentInvoiceMain WHERE  eoStudentUser = '" + studentPk
						+ "' AND year ='" + year + "' order by createdDate");

		List<HashMap<String, Object>> resultList = new ArrayList<>();

		if (dataListMain != null && dataListMain.size() > 0) {
			for (Object obj : dataListMain) {

				EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) obj;
				List<EOStudentInvoiceEditable> editableArray = (List<EOStudentInvoiceEditable>) eoStudentInvoiceMain.eoStudentInvoiceEditable;
				List<EOStudentInvoiceDetail> detailArray = (List<EOStudentInvoiceDetail>) eoStudentInvoiceMain.eoStudentInvoiceDetails;
				HashMap<String, Object> MainInnerMap = new HashMap<>();

				MainInnerMap.put("primaryKey", eoStudentInvoiceMain.primaryKey + "");
				MainInnerMap.put("status", eoStudentInvoiceMain.status);
				MainInnerMap.put("eoStudentUser", eoStudentInvoiceMain.eoStudentUser.primaryKey + "");
				MainInnerMap.put("studentfullname", eoStudentInvoiceMain.eoStudentUser.getFullName());
				MainInnerMap.put("month", eoStudentInvoiceMain.month);
				MainInnerMap.put("year", eoStudentInvoiceMain.year);
				MainInnerMap.put("total", eoStudentInvoiceMain.total);
				MainInnerMap.put("depositAmount", eoStudentInvoiceMain.depositAmount);
				MainInnerMap.put("dueAmount", eoStudentInvoiceMain.dueAmount);
				MainInnerMap.put("consumptionTax", eoStudentInvoiceMain.consumptionTax);
				MainInnerMap.put("cancellationAmount", eoStudentInvoiceMain.cancellationAmount);
				MainInnerMap.put("grandTotal", eoStudentInvoiceMain.grandTotal);
				MainInnerMap.put("createdDate", eoStudentInvoiceMain.createdDate);
				MainInnerMap.put("invoiceNo", eoStudentInvoiceMain.invoiceNo);

				List<HashMap<String, Object>> editableList = new ArrayList<>();
				if (editableArray != null && editableArray.size() > 0) {
					for (EOStudentInvoiceEditable eoStudentInvoiceEditable : editableArray) {
						HashMap<String, Object> mainTempMap = new HashMap<>();

						if (eoStudentInvoiceEditable.isActive == true) {
							mainTempMap.put("primaryKey", eoStudentInvoiceEditable.primaryKey + "");
							mainTempMap.put("amount", eoStudentInvoiceEditable.amount);
							mainTempMap.put("description", eoStudentInvoiceEditable.description);

							editableList.add(mainTempMap);
						}

					}
					MainInnerMap.put("editableList", editableList);

				}

				List<HashMap<String, Object>> mainTempList = new ArrayList<>();
				if (detailArray != null && detailArray.size() > 0) {
					for (EOStudentInvoiceDetail eoStudentInvoiceDetail : detailArray) {
						HashMap<String, Object> mainTempMap = new HashMap<>();

						mainTempMap.put("primaryKey", eoStudentInvoiceDetail.primaryKey + "");
						mainTempMap.put("lkMusicType", eoStudentInvoiceDetail.lkMusicType.primaryKey + "");
						if (eoStudentInvoiceDetail.lkClassDuration != null) {
							mainTempMap.put("lkClassDuration", eoStudentInvoiceDetail.lkClassDuration.primaryKey + "");
							mainTempMap.put("sessionDuration", eoStudentInvoiceDetail.lkClassDuration.duration);
						}

						mainTempMap.put("fees", eoStudentInvoiceDetail.fees);
						mainTempMap.put("feeType", eoStudentInvoiceDetail.feeType);
						mainTempMap.put("session", eoStudentInvoiceDetail.session);
						mainTempMap.put("eoCourses", eoStudentInvoiceDetail.eoCourses.primaryKey);
						mainTempMap.put("courseName", eoStudentInvoiceDetail.eoCourses.courseName);

						mainTempList.add(mainTempMap);
					}
					MainInnerMap.put("attributeList", mainTempList);

				}
				resultList.add(MainInnerMap);
			}
		}

		return Response.status(201).entity(JSONUtil.objectToJson(resultList)).build();

	}

	@POST
	@Path("/getOverallTurnOver")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOverallTurnOver(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String year = (String) map.get("year");

		List<HashMap<String, Object>> dataListMain = DBServices.getNativeQueryResult(
				"SELECT SUM(grand_total) as monthly_turn_over, " + "month FROM eostudent_invoice_main where year = '"
						+ year + "' " + " AND status = 'Fix' group by month");

		List<HashMap<String, Object>> mainResultList = new ArrayList<>();
		if (dataListMain != null && dataListMain.size() > 0) {
			boolean flag = false;
			for (String month : DateUtil.janToDecMonthArr) {
				HashMap<String, Object> tempMap = new HashMap<>();
				if (month.equalsIgnoreCase(DateUtil.currMonth())
						&& year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
					for (HashMap<String, Object> obj : dataListMain) {
						if (month.equalsIgnoreCase((String) obj.get("month"))) {

							tempMap.put("month", obj.get("month"));
							tempMap.put("monthlyTurnOver", obj.get("monthly_turn_over"));
							mainResultList.add(tempMap);
							flag = false;
							break;
						} else {
							flag = true;
						}

					}
					if (flag) {
						tempMap.put("month", month);
						tempMap.put("monthlyTurnOver", 0.0);
						mainResultList.add(tempMap);
					}
					break;
				}
				for (HashMap<String, Object> obj : dataListMain) {
					if (month.equalsIgnoreCase((String) obj.get("month"))) {

						tempMap.put("month", obj.get("month"));
						tempMap.put("monthlyTurnOver", obj.get("monthly_turn_over"));
						mainResultList.add(tempMap);
						flag = false;
						break;
					} else {
						flag = true;
					}

				}
				if (flag) {
					tempMap.put("month", month);
					tempMap.put("monthlyTurnOver", 0.0);
					mainResultList.add(tempMap);
				}
			}

		}

		return Response.status(201).entity(JSONUtil.objectToJson(mainResultList)).build();

	}

	@POST
	@Path("/getOverallGrossProfit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOverallGrossProfit(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String year = (String) map.get("year");

		List<HashMap<String, Object>> dataListMain = DBServices.getNativeQueryResult(
				"SELECT SUM(grand_total) as monthly_turn_over, " + "month FROM eostudent_invoice_main where year = '"
						+ year + "' " + "AND status = 'Fix' group by month");

		List<HashMap<String, Object>> salaryList = DBServices.getNativeQueryResult(
				"SELECT SUM( total_salary) as total_expense , " + "month FROM eogenerate_slip where year = '" + year
						+ "' and status = 'Released' group by month");
		
		List<HashMap<String, Object>> expenseList = DBServices.getNativeQueryResult(
				"SELECT SUM(e.amount+ed.amount) AS totalsum, e.month, e.year FROM eoadd_expenses e "
				+" LEFT JOIN (SELECT SUM(amount) amount ,eoaddexpenses_primary_key "
				+" FROM eoadd_expenses_detail GROUP BY eoaddexpenses_primary_key) ed "
				+" ON ed.eoaddexpenses_primary_key = e.primary_key  WHERE year = '"+year+"' GROUP BY e.month, e.year;");

		List<HashMap<String, Object>> resultList = new ArrayList();
		HashMap<Object, Object> incomeMap = new HashMap<>();
		HashMap<Object, Object> salaryMap = new HashMap<>();
		HashMap<Object, Object> expenseMap = new HashMap<>();
		if (dataListMain != null && dataListMain.size() > 0) {
			for (HashMap<String, Object> obj : dataListMain) {
				incomeMap.put(obj.get("month"), obj.get("monthly_turn_over"));
			}
		}

		if (salaryList != null && salaryList.size() > 0) {
			for (HashMap<String, Object> obj : salaryList) {
				salaryMap.put(obj.get("month"), obj.get("total_expense"));
			}
		}
		
		if (expenseList != null && expenseList.size() > 0) {
			for (HashMap<String, Object> obj : expenseList) {
				expenseMap.put(obj.get("month"), obj.get("totalsum"));
			}
		}

		String[] monthArr = DateUtil.janToDecMonthArr;
		for (String month : monthArr) {
			HashMap<String, Object> tmpMap = new HashMap<>();

			if (month.equalsIgnoreCase(DateUtil.currMonth()) && year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
				double income = incomeMap.get(month) != null ? (double) incomeMap.get(month) : 0;
				double salary = salaryMap.get(month) != null ? (double) salaryMap.get(month) : 0;
				double expense = expenseMap.get(month) != null ? (double) expenseMap.get(month) : 0;
				
				tmpMap.put("income", income);
				tmpMap.put("expense", (salary + expense));
				tmpMap.put("salary", (salary));
				tmpMap.put("expensedata", (expense));
				tmpMap.put("profit", income - (salary + expense));
				tmpMap.put("month", month);
				resultList.add(tmpMap);
				break;
			}
			double income = incomeMap.get(month) != null ? (double) incomeMap.get(month) : 0;
			double salary = salaryMap.get(month) != null ? (double) salaryMap.get(month) : 0;
			double expense = expenseMap.get(month) != null ? (double) expenseMap.get(month) : 0;
			tmpMap.put("income", income);
			tmpMap.put("expense", (salary + expense));
			tmpMap.put("salary", (salary));
			tmpMap.put("expensedata", (expense));
			tmpMap.put("profit", income - (salary + expense));
			tmpMap.put("month", month);
			resultList.add(tmpMap);

		}

		return Response.status(201).entity(JSONUtil.objectToJson(resultList)).build();

	}

	@POST
	@Path("/getTurnOverByStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTurnOverByStudent(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String year = (String) map.get("year");
		String studentPk = (String) map.get("studentPk");

		List<HashMap<String, Object>> dataListMain = DBServices.getNativeQueryResult(
				"SELECT SUM(grand_total) as monthly_turn_over, " + "month FROM eostudent_invoice_main where year = '"
						+ year + "' and eostudentuser_primary_key = " + studentPk + " AND status = 'Fix' group by month");

		List<HashMap<String, Object>> mainResultList = new ArrayList<>();
		if (dataListMain != null && dataListMain.size() > 0) {
			boolean flag = false;
			for (String month : DateUtil.janToDecMonthArr) {
				HashMap<String, Object> tempMap = new HashMap<>();
				if (month.equalsIgnoreCase(DateUtil.currMonth())
						&& year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
					for (HashMap<String, Object> obj : dataListMain) {
						if (month.equalsIgnoreCase((String) obj.get("month"))) {

							tempMap.put("month", obj.get("month"));
							tempMap.put("monthlyTurnOver", obj.get("monthly_turn_over"));
							mainResultList.add(tempMap);
							flag = false;
							break;
						} else {
							flag = true;
						}

					}
					if (flag) {
						tempMap.put("month", month);
						tempMap.put("monthlyTurnOver", 0.0);
						mainResultList.add(tempMap);
					}
					break;
				}
				for (HashMap<String, Object> obj : dataListMain) {
					if (month.equalsIgnoreCase((String) obj.get("month"))) {

						tempMap.put("month", obj.get("month"));
						tempMap.put("monthlyTurnOver", obj.get("monthly_turn_over"));
						mainResultList.add(tempMap);
						flag = false;
						break;
					} else {
						flag = true;
					}

				}
				if (flag) {
					tempMap.put("month", month);
					tempMap.put("monthlyTurnOver", 0.0);
					mainResultList.add(tempMap);
				}
			}

		}

		return Response.status(201).entity(JSONUtil.objectToJson(mainResultList)).build();

	}

	@POST
	@Path("/getTurnOverByInstrument")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTurnOverByInstrument(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String year = (String) map.get("year");
		String instrumentPk = (String) map.get("instrumentPk");

		List<HashMap<String, Object>> dataListMain = DBServices.getNativeQueryResult(
				"SELECT SUM(d.fees*d.session) AS monthly_turn_over,d.month FROM eostudent_invoice_detail d "
				+" LEFT JOIN eostudent_invoice_main m ON m.primary_key = d.eostudentinvoicemain_primary_key"
				+" WHERE d.year = '"+year+"' AND lkmusictype_primary_key = "+instrumentPk
				+" AND m.status = 'Fix' GROUP BY d.month ");

		List<HashMap<String, Object>> mainResultList = new ArrayList<>();
		if (dataListMain != null && dataListMain.size() > 0) {
			boolean flag = false;
			for (String month : DateUtil.janToDecMonthArr) {
				HashMap<String, Object> tempMap = new HashMap<>();
				if (month.equalsIgnoreCase(DateUtil.currMonth())
						&& year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
					for (HashMap<String, Object> obj : dataListMain) {
						if (month.equalsIgnoreCase((String) obj.get("month"))) {

							tempMap.put("month", obj.get("month"));
							tempMap.put("monthlyTurnOver", obj.get("monthly_turn_over"));
							mainResultList.add(tempMap);
							flag = false;
							break;
						} else {
							flag = true;
						}

					}
					if (flag) {
						tempMap.put("month", month);
						tempMap.put("monthlyTurnOver", 0.0);
						mainResultList.add(tempMap);
					}
					break;
				}
				for (HashMap<String, Object> obj : dataListMain) {
					if (month.equalsIgnoreCase((String) obj.get("month"))) {

						tempMap.put("month", obj.get("month"));
						tempMap.put("monthlyTurnOver", obj.get("monthly_turn_over"));
						mainResultList.add(tempMap);
						flag = false;
						break;
					} else {
						flag = true;
					}

				}
				if (flag) {
					tempMap.put("month", month);
					tempMap.put("monthlyTurnOver", 0.0);
					mainResultList.add(tempMap);
				}
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(mainResultList)).build();

	}

	@POST
	@Path("/getOverallRating")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOverallRating(HashMap<String, Object> map) {

		String[] monthArr = DateUtil.janToDecMonthArr;
		String year = map.get("year") != null ? (String) map.get("year") : DateUtil.currentYear() + "";
		List<HashMap<String, Object>> studentRatingList = new ArrayList<>();
		List<HashMap<String, Object>> teacherRatingList = new ArrayList<>();
		int count = 0;
		String monthName = "(";
		for (String month : monthArr) {
			if (month.equalsIgnoreCase(DateUtil.currMonth()) && year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
				if (count == 0) {
					monthName += "'" + month + "')";
				} else {
					monthName += ", " + "'" + month + "'" + ")";
				}
				studentRatingList = countStudentOverallRating(monthName, year);
				teacherRatingList = countTeacherOverallRating(monthName, year);
				break;
			}
			if (count == 0) {
				monthName += "'" + month + "'";
			} else {
				monthName += ", " + "'" + month + "'";
			}

			count++;
		}
		if (!year.equalsIgnoreCase(DateUtil.currentYear() + "")) {
			monthName += ")";
			studentRatingList = countStudentOverallRating(monthName, year);
			teacherRatingList = countTeacherOverallRating(monthName, year);
		}

		HashMap<String, Object> resultMap = new HashMap<>();
		resultMap.put("studentRatingList", studentRatingList);
		resultMap.put("teacherRatingList", teacherRatingList);

		return Response.status(200).entity(JSONUtil.objectToJson(resultMap)).build();
	}

	private List<HashMap<String, Object>> countStudentOverallRating(String month, String year) {

		String qryStr = "Select SUM(r.avg_opted_rating) as rating, concat(s.first_name,' ', s.last_name) as student FROM EOSTUDENT_RATING r inner join EOSTUDENT_USER s "
				+ " on r.eostudentuser_primary_key = s.primary_key where  r.month in " + month + " and r.year = '"
				+ year + "'" + " group by student order by rating DESC";

		List<HashMap<String, Object>> ratingList = DBServices.getNativeQueryResult(qryStr);
		return ratingList;

	}

	private List<HashMap<String, Object>> countTeacherOverallRating(String month, String year) {

		String qryStr = "Select SUM(r.avg_opted_rating) as rating, concat(t.first_name,' ', t.last_name) as teacher FROM EOTEACHER_RATING r inner join EOTEACHER_USER t "
				+ " on r.eoteacheruser_primary_key = t.primary_key where  r.month in " + month + " and r.year = '"
				+ year + "'" + " group by teacher order by rating DESC";

		List<HashMap<String, Object>> ratingList = DBServices.getNativeQueryResult(qryStr);
		return ratingList;

	}

	@POST
	@Path("/getnotconvertedlist")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getnotconvertedlist(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		List<Object> notconvertlist = DBServices.get("From EOQueryForm WHERE isConvert = false order by enquiryDate DESC");

		List<HashMap<String, Object>> notconvert = new ArrayList<>();
		
		for (Object obj : notconvertlist) {
			EOQueryForm eoquerylist = (EOQueryForm) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("mail_id", eoquerylist.email);
			innerMap.put("getFullName", eoquerylist.getFullName());
			innerMap.put("phone_nmber", eoquerylist.phone);
			innerMap.put("address", eoquerylist.address);
			innerMap.put("enquiryDate", eoquerylist.enquiryDate);
			innerMap.put("remarks", eoquerylist.remarks);
			innerMap.put("convert_date", eoquerylist.convertDate);
			
			
			List<String> pkObj = (List<String>) JSONUtil.jsonToObject(eoquerylist.musicPk, List.class);
			
			String musicCategory = "";
			Set<String> s = new LinkedHashSet<String>();  
			for (String str : pkObj) {
				int count = 0;
				
				String qryStr = "select primary_key , music_type FROM lkmusic_type  where is_active =  true AND primary_key ="+ str;
				List<HashMap<String, Object>> list = DBServices.getNativeQueryResult(qryStr);
				
				
				if (list != null && list.size() > 0) {
					
					for (HashMap<String, Object> m : list) {

						if (count == 0) {
							
							s.add(((String) m.get("music_type")).trim());

						} else {
							
							s.add(((String) m.get("music_type")).trim());
						}
						count++;

					}
				
				}

			}
			
			innerMap.put("musicType", s);
			
			notconvert.add(innerMap);
			
		}
		
	//System.out.println("notconvertListtttt:" + notconvert);
		
		transaction.commit();
		session.close();
		return Response.status(201).entity(JSONUtil.objectToJson(notconvert)).build();
	}

}
