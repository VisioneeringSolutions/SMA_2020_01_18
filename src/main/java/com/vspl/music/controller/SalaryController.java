package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.print.DocFlavor.STRING;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DbTimestampType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.handlers.handler_base;
import com.sun.mail.imap.CopyUID;
import com.vspl.music.model.eo.EODefinedSlot;
import com.vspl.music.model.eo.EOGenerateSlip;
import com.vspl.music.model.eo.EOGenerateSlipDetail;
import com.vspl.music.model.eo.EOGenerateSlipStudentDetail;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOStudentUser;
import com.vspl.music.model.eo.EOTeacherSalary;
import com.vspl.music.model.eo.EOTeacherSalaryDetail;
import com.vspl.music.model.eo.EOTimeSlot;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.JSONUtil;

/**
 * The salary controller program implements an application that Manages Salary
 * of the teacher
 *
 * @author Kundan Kumar
 * @since
 */
@Path("/ajaxSalary")
public class SalaryController {
	public static Logger logger = LoggerFactory.getLogger(SalaryController.class);

	@POST
	@Path("/createSalary")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTimeSlot(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<HashMap<String, Object>> courseData = (List<HashMap<String,Object>>) map.get("courseData");
		map.remove("courseData");
		
		if (map.get("primaryKey") == null) {
			map.put("createdDate", DateUtil.formatedCurrentDate());
			map.put("className", "EOTeacherSalary");
			DBServices.create(EOObject.createObject(map));
			
			if(courseData != null && courseData.size() > 0){
				EOTeacherSalary eoTeacherSalary = (EOTeacherSalary)EOObject.getLatestObject("EOTeacherSalary");
				for(HashMap<String, Object> obj : courseData){
					HashMap<String, Object> tempMap = new HashMap<>();
					tempMap.put("createdDate", DateUtil.formatedCurrentDate());
					tempMap.put("eoTeacherSalary", eoTeacherSalary.primaryKey);
					tempMap.put("lkClassDuration", obj.get("lkClassDuration"));
					tempMap.put("lkMusicType", obj.get("lkMusicType"));
					tempMap.put("lkCategoryType", obj.get("lkCategoryType"));
					tempMap.put("amount", obj.get("amount"));
					tempMap.put("eoCourses", obj.get("eoCourses"));
					tempMap.put("className", "EOTeacherSalaryDetail");
					DBServices.create(EOObject.createObject(tempMap));
				}
			}
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		} else {
			String teacherSalaryPk = (String) map.get("primaryKey");
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			map.put("className", "EOTeacherSalary");
			EOObject eoObject = EOObject.getObjectByPK("EOTeacherSalary", (String) map.get("primaryKey"));
			DBServices.update(EOObject.updateObject(eoObject, map));
			
			if(courseData != null && courseData.size() > 0){
				for(HashMap<String, Object> obj : courseData){
					
					HashMap<String, Object> tempMap = new HashMap<>();
					tempMap.put("eoTeacherSalary", teacherSalaryPk);
					tempMap.put("lkClassDuration", obj.get("lkClassDuration"));
					tempMap.put("lkMusicType", obj.get("lkMusicType"));
					tempMap.put("lkCategoryType", obj.get("lkCategoryType"));
					tempMap.put("amount", obj.get("amount"));
					tempMap.put("eoCourses", obj.get("eoCourses"));
					tempMap.put("className", "EOTeacherSalaryDetail");
					if(obj.get("primaryKey") != null){
						//update
						tempMap.put("primaryKey", obj.get("primaryKey"));
						tempMap.put("updatedDate", DateUtil.formatedCurrentDate());
						EOObject eoObject2 = EOObject.getObjectByPK("EOTeacherSalaryDetail", obj.get("primaryKey")+"");
						DBServices.update(EOObject.updateObject(eoObject2, tempMap));
					}else{
						//create
						tempMap.put("createdDate", DateUtil.formatedCurrentDate());
						DBServices.create(EOObject.createObject(tempMap));
					}
				}
			}
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}
	}

	@POST
	@Path("/getTeacherForSalary")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTeacherForSalary(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String str = "SELECT tu.primary_key as teacherpk , CONCAT(tu.first_name, ' ', tu.last_name) as teacherfullname,"
					+" tu.joining_date, tu.salary_type, tu.course_array FROM eoteacher_user tu WHERE tu.is_active = true";
		
		//System.out.println("str::"+str);
		List<HashMap<String, Object>> teacherList = DBServices.getNativeQueryResult(str);
		//System.out.println("teacherList::"+JSONUtil.objectToJson(teacherList));
		List<HashMap<String, Object>> dataList = new ArrayList<>();
		
		for(HashMap<String, Object> obj : teacherList){
			HashMap<String, Object> innerMap = new HashMap<>();
			
			innerMap.put("eoTeacherUser", obj.get("teacherpk")+"");
			innerMap.put("teacherfullname", obj.get("teacherfullname"));
			innerMap.put("joiningDate", obj.get("joining_date"));
			innerMap.put("salaryType", obj.get("salary_type"));
			
			List<Object> coursePkList = (List<Object>)JSONUtil.jsonToObject(obj.get("course_array")+"", List.class);
			if(coursePkList != null && coursePkList.size() > 0 && (obj.get("salary_type")+"").equalsIgnoreCase("Per session")){
				List<HashMap<String, Object>> coursedetails = new ArrayList<>();
				for(Object strPk : coursePkList){
					HashMap<String, Object> tempMap = new HashMap<>();
					String courseQry = "SELECT c.primary_key, c.lkcategorytype_primary_key, c.lkclassduration_primary_key, "
							+ "c.lkmusictype_primary_key, cd.duration, ct.description, mt.music_type"
							+" FROM eocourses c LEFT JOIN lkcategory_type ct ON ct.primary_key = c.lkcategorytype_primary_key"
							+" LEFT JOIN lkclass_duration cd ON cd.primary_key = c.lkclassduration_primary_key"
							+" LEFT JOIN lkmusic_type mt ON mt.primary_key = c.lkmusictype_primary_key"
							+" WHERE c.primary_key = "+strPk;
					
					//System.out.println("courseQry::"+courseQry);
					
					List<HashMap<String, Object>> courseData = DBServices.getNativeQueryResult(courseQry);
					//System.out.println("courseData:;"+courseData);
					tempMap.put("eoCourses",courseData.get(0).get("primary_key"));
					tempMap.put("description", courseData.get(0).get("description"));
					tempMap.put("duration", courseData.get(0).get("duration"));
					tempMap.put("musicType",courseData.get(0).get("music_type"));
					tempMap.put("lkMusicType",courseData.get(0).get("lkmusictype_primary_key"));
					tempMap.put("lkCategoryType", courseData.get(0).get("lkcategorytype_primary_key"));
					tempMap.put("lkClassDuration", courseData.get(0).get("lkclassduration_primary_key"));
				
					coursedetails.add(tempMap);
				}
				innerMap.put("courseData", coursedetails);
			}
			dataList.add(innerMap);
		}
		
		List<Object> mainTeacherList = DBServices.get("FROM EOTeacherSalary WHERE isActive = true");
		List<HashMap<String, Object>> tempList = new ArrayList<>();
		for(Object obj : mainTeacherList){
			EOTeacherSalary eoTeacherSalary = (EOTeacherSalary) obj;
			HashMap<String, Object> mainMap = new HashMap<>();
			
			mainMap.put("primaryKey", eoTeacherSalary.primaryKey+"");
			mainMap.put("eoTeacherUser", eoTeacherSalary.eoTeacherUser.primaryKey+"");
			mainMap.put("joiningDate", eoTeacherSalary.eoTeacherUser.joiningDate);
			mainMap.put("teacherfullname", eoTeacherSalary.eoTeacherUser.getFullName());
			mainMap.put("salaryType", eoTeacherSalary.salaryType);
			mainMap.put("status", eoTeacherSalary.status);
			mainMap.put("transportAmount", eoTeacherSalary.transportAmount);
			mainMap.put("amount", eoTeacherSalary.amount);
			
			List<Object> salaryDetails = DBServices.get("FROM EOTeacherSalaryDetail WHERE isActive = true AND eoteachersalary_primary_key ="+eoTeacherSalary.primaryKey+"");
			if(salaryDetails.size() > 0 && salaryDetails != null){
				List<HashMap<String, Object>> coursedetails = new ArrayList<>();
				for(Object detail : salaryDetails){
					
					HashMap<String, Object> tempMap = new HashMap<>();
					EOTeacherSalaryDetail eoTeacherSalaryDetail = (EOTeacherSalaryDetail) detail;
					tempMap.put("primaryKey", eoTeacherSalaryDetail.primaryKey);
					tempMap.put("amount", eoTeacherSalaryDetail.amount);
					tempMap.put("lkCategoryType", eoTeacherSalaryDetail.lkCategoryType.primaryKey);
					tempMap.put("lkClassDuration", eoTeacherSalaryDetail.lkClassDuration.primaryKey);
					tempMap.put("lkMusicType", eoTeacherSalaryDetail.lkMusicType.primaryKey);
					tempMap.put("description", eoTeacherSalaryDetail.lkCategoryType.description);
					tempMap.put("duration", eoTeacherSalaryDetail.lkClassDuration.duration);
					tempMap.put("musicType", eoTeacherSalaryDetail.lkMusicType.musicType);
					tempMap.put("eoCourses", eoTeacherSalaryDetail.eoCourses.primaryKey);
					
					coursedetails.add(tempMap);
				}
				mainMap.put("courseData", coursedetails);
			}
			tempList.add(mainMap);
		}
		List<HashMap<String, Object>> finalList = new ArrayList<>();
		if (tempList != null && tempList.size() > 0) {
			finalList = tempList;
		}
		
		if(tempList != null && tempList.size() > 0){
			
			if(dataList != null && dataList.size() > 0){
				for(HashMap<String, Object> list : dataList){
					boolean flag = true;
					for(HashMap<String, Object> mainList : tempList){
						
						if(mainList.get("eoTeacherUser").equals(list.get("eoTeacherUser"))){
							flag = false;
							break;
						}
					}
					if(flag){
						finalList.add(list);
					}
				}
			}
		}
		
		if (tempList != null && tempList.size() > 0) {
			if (dataList != null && dataList.size() > 0) {
				for (HashMap<String, Object> list : dataList) {
					for (HashMap<String, Object> mainList : tempList) {
						if (mainList.get("eoTeacherUser").equals(list.get("eoTeacherUser"))) {
							if (list.get("courseData") != null
									&& ((List<HashMap<String, Object>>) list.get("courseData")).size() > 0
									&& (List<HashMap<String, Object>>) mainList.get("courseData") != null
									&& ((List<HashMap<String, Object>>) mainList.get("courseData")).size() > 0) {
								List<HashMap<String, Object>> course = new ArrayList<>((List<HashMap<String, Object>>) mainList.get("courseData"));
								for (HashMap<String, Object> courseList : (List<HashMap<String, Object>>) list.get("courseData")) {
									boolean flag = true;
									for (HashMap<String, Object> courseDataList : course) {
										if ((courseList.get("lkMusicType")+"_"+courseList.get("lkCategoryType"))
												.equalsIgnoreCase(courseDataList.get("lkMusicType")+"_"+courseDataList.get("lkCategoryType"))) {
											flag = false;
											break;
											
										}
									}
									if(flag){
										((List<HashMap<String, Object>>) mainList.get("courseData")).add(courseList);
									}
								}
							}
						}
					}
				}
			}
		} 
		if(tempList == null || tempList.size() == 0){
			finalList = dataList;
		}
		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}

	@POST
	@Path("/createSalaryByMonthForSlip")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createSalaryByMonthForSlip(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		
		List<HashMap<String, Object>> courseData = (List<HashMap<String,Object>>) map.get("courseData");
		map.remove("courseData");
		
		List<HashMap<String, Object>> studentList = (List<HashMap<String,Object>>) map.get("studentList");
		map.remove("studentList");
		map.remove("isViewPdf");
		map.remove("startDate");
		map.remove("endDate");
		if (map.get("primaryKey") == null) {
			System.out.println("create generate slip table");
			map.put("createdDate", DateUtil.formatedCurrentDate());
			map.put("className", "EOGenerateSlip");
			DBServices.create(EOObject.createObject(map));
			
			EOGenerateSlip eoGenerateSlip = (EOGenerateSlip)EOObject.getLatestObject("EOGenerateSlip");
			if(courseData != null && courseData.size() > 0){
				
				for(HashMap<String, Object> obj : courseData){
					if(obj.get("totalMins") != null){
						HashMap<String, Object> tempMap = new HashMap<>();
						tempMap.put("createdDate", DateUtil.formatedCurrentDate());
						tempMap.put("eoGenerateSlip", eoGenerateSlip.primaryKey);
						tempMap.put("lkClassDuration", obj.get("lkClassDuration"));
						tempMap.put("lkMusicType", obj.get("lkMusicType"));
						tempMap.put("lkCategoryType", obj.get("lkCategoryType"));
						tempMap.put("amount", obj.get("amount"));
						tempMap.put("totalMins", obj.get("totalMins"));
						tempMap.put("session", obj.get("session"));
						tempMap.put("cancellationAmount", obj.get("cancellationAmount"));
						tempMap.put("className", "EOGenerateSlipDetail");
						DBServices.create(EOObject.createObject(tempMap));
					}
				}
			}
			
			if(studentList != null && studentList.size() > 0){
				for(HashMap<String, Object> obj : studentList){
					obj.put("className", "EOGenerateSlipStudentDetail");
					obj.put("createdDate", DateUtil.formatedCurrentDate());
					obj.put("eoGenerateSlip",eoGenerateSlip.primaryKey);
					DBServices.create(EOObject.createObject(obj));
				}
			}
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		} else {
			System.out.println("Update generate slip");
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			map.put("className", "EOGenerateSlip");
			EOObject eoObject = EOObject.getObjectByPK("EOGenerateSlip", (String) map.get("primaryKey"));
			DBServices.update(EOObject.updateObject(eoObject, map));
			
			if(courseData != null && courseData.size() > 0){
				for(HashMap<String, Object> obj : courseData){
					EOObject eoObject2 = EOObject.getObjectByPK("EOGenerateSlipDetail", obj.get("primaryKey")+"");
					HashMap<String, Object> tempMap = new HashMap<>();
					tempMap.put("createdDate", DateUtil.formatedCurrentDate());
					tempMap.put("primaryKey", obj.get("primaryKey"));
					tempMap.put("eoTeacherSalary", map.get("primaryKey"));
					tempMap.put("lkClassDuration", obj.get("lkClassDuration"));
					tempMap.put("lkMusicType", obj.get("lkMusicType"));
					tempMap.put("lkCategoryType", obj.get("lkCategoryType"));
					tempMap.put("amount", obj.get("amount"));
					tempMap.put("className", "EOGenerateSlipDetail");
					DBServices.update(EOObject.updateObject(eoObject2, tempMap));
				}
			}
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}
	}

	@POST
	@Path("/getTeacherForSalaryByMonth")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTeacherForSalaryByMonth(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String month = (String) map.get("month");
		String year = (String) map.get("year");
		String startDate = (String) map.get("startDate");
		String endDate = (String) map.get("endDate");
		
		List<Object> dataList = DBServices.get("FROM EOTeacherSalary WHERE isActive = true");

		List<HashMap<String, Object>> resultList = new ArrayList<>();
		if (dataList != null && dataList.size() > 0) {
			for (Object obj2 : dataList) {

				EOTeacherSalary eoTeacherSalary = (EOTeacherSalary) obj2;
				HashMap<String, Object> innerMap = new HashMap<>();

				long pk = eoTeacherSalary.eoTeacherUser.primaryKey;
				innerMap.put("eoTeacherSalaryPk", eoTeacherSalary.primaryKey+"");
				innerMap.put("eoTeacherUser", eoTeacherSalary.eoTeacherUser.primaryKey+"");
				innerMap.put("joiningDate", eoTeacherSalary.eoTeacherUser.joiningDate);
				innerMap.put("teacherfullname", eoTeacherSalary.eoTeacherUser.getFullName());
				innerMap.put("teacherfullnamePdf", eoTeacherSalary.eoTeacherUser.getFullNameJapanese());
				innerMap.put("salaryType", eoTeacherSalary.salaryType);
				innerMap.put("salStatus", eoTeacherSalary.status);
				innerMap.put("transportAmount", eoTeacherSalary.transportAmount);
				innerMap.put("amount", eoTeacherSalary.amount);
				
				String strQry = "SELECT d.primary_key, d.date, d.day, d.end_time, d.slot_pk, d.month, d.start_time, d.year,"
						       +" d.eostudentuser_primary_key, d.eoteacheruser_primary_key, d.lkmusictype_primary_key,"
						       +" CONCAT(s.first_name, ' ' ,s.last_name) studfullname,lkm.music_type,d.lkcategorytype_primary_key,"
						       +" lkt.description,m.room_name,d.eoroom_primary_key FROM eodefined_slot d LEFT JOIN eostudent_user s ON "
						       +" s.primary_key = d.eostudentuser_primary_key LEFT JOIN lkmusic_type lkm ON"
						       +" lkm.primary_key = d.lkmusictype_primary_key LEFT JOIN lkcategory_type lkt ON "
						       +" lkt.primary_key = d.lkcategorytype_primary_key "
						       +" LEFT JOIN eomusic_room m ON m.primary_key = d.eoroom_primary_key"
						       +" WHERE d.eoteacheruser_primary_key = "+eoTeacherSalary.eoTeacherUser.primaryKey
							   +" AND month = '"+month+"' AND year = '"+year+"'";
				//System.out.println("strQry:"+strQry);
				List<HashMap<String, Object>> studentDataList = DBServices.getNativeQueryResult(strQry);
		
				if(studentDataList.size() > 0 && studentDataList != null){
					List<HashMap<String, Object>> studentList = new ArrayList<>();
					for(HashMap<String, Object> stdObj : studentDataList){
						
						HashMap<String, Object> tempMap = new HashMap<>();
						tempMap.put("date",stdObj.get("date"));
						tempMap.put("day",stdObj.get("day"));
						tempMap.put("endTime",stdObj.get("end_time"));
						tempMap.put("eoSlot",stdObj.get("slot_pk"));
						tempMap.put("duration",15*((List<Object>) JSONUtil.jsonToObject(stdObj.get("slot_pk")+"", List.class)).size());
						tempMap.put("month",stdObj.get("month"));
						tempMap.put("startTime",stdObj.get("start_time"));
						tempMap.put("year",stdObj.get("year"));
						tempMap.put("eoStudentUser",stdObj.get("eostudentuser_primary_key"));
						tempMap.put("eoTeacherUser",stdObj.get("eoteacheruser_primary_key"));
						tempMap.put("lkMusicType",stdObj.get("lkmusictype_primary_key"));
						tempMap.put("studFullName",stdObj.get("studfullname"));
						tempMap.put("musicType",stdObj.get("music_type"));
						tempMap.put("lkCategoryType",stdObj.get("lkcategorytype_primary_key"));
						tempMap.put("description",stdObj.get("description"));
						tempMap.put("roomName",stdObj.get("room_name"));
						tempMap.put("eoMusicRoom",stdObj.get("eoroom_primary_key"));
						tempMap.put("sortingKey",stdObj.get("date")+"-"+stdObj.get("start_time")+"-"+stdObj.get("end_time"));
						
						studentList.add(tempMap);
					}
					innerMap.put("studentList", studentList);
				}
				
				List<Object> salaryDetails = DBServices.get("FROM EOTeacherSalaryDetail WHERE isActive = true AND eoteachersalary_primary_key ="+eoTeacherSalary.primaryKey+"");
				if(salaryDetails.size() > 0 && salaryDetails != null){
					List<HashMap<String, Object>> coursedetails = new ArrayList<>();
					for(Object detail : salaryDetails){
						
						HashMap<String, Object> tempMap = new HashMap<>();
						EOTeacherSalaryDetail eoTeacherSalaryDetail = (EOTeacherSalaryDetail) detail;
						tempMap.put("primaryKey", eoTeacherSalaryDetail.primaryKey);
						tempMap.put("amount", eoTeacherSalaryDetail.amount);
						tempMap.put("lkCategoryType", eoTeacherSalaryDetail.lkCategoryType.primaryKey);
						tempMap.put("lkClassDuration", eoTeacherSalaryDetail.lkClassDuration.primaryKey);
						tempMap.put("lkMusicType", eoTeacherSalaryDetail.lkMusicType.primaryKey);
						tempMap.put("description", eoTeacherSalaryDetail.lkCategoryType.description);
						tempMap.put("duration", eoTeacherSalaryDetail.lkClassDuration.duration);
						tempMap.put("musicType", eoTeacherSalaryDetail.lkMusicType.musicType);
						tempMap.put("musicType", eoTeacherSalaryDetail.lkMusicType.musicType);
						tempMap.put("eoCourses", eoTeacherSalaryDetail.eoCourses != null ? eoTeacherSalaryDetail.eoCourses.primaryKey : "");
						
						String str = "SELECT distinct(a.eotimeslot_primary_key) FROM eotime_slot_allocation a"
								+" LEFT JOIN eotimeslot t ON t.primary_key = a.eotimeslot_primary_key WHERE "
								+" eoteacheruser_primary_key =" +pk+" AND a.status != 'Requested' AND a.teacher_cancellation != true"
								+" AND lkcategorytype_primary_key ="+eoTeacherSalaryDetail.lkCategoryType.primaryKey
								+" AND lkmusictype_primary_key ="+eoTeacherSalaryDetail.lkMusicType.primaryKey
								+" AND t.date BETWEEN '"+startDate+"' AND '"+endDate+"' AND a.is_teacher_paid = true";
						//System.out.println("str::"+str);
						List<HashMap<String, Object>> timeData = DBServices.getNativeQueryResult(str);
						
						double session = 0;
						double totalMins = 0;
						double totalAmount = 0;
						if(timeData != null && timeData.size() > 0){
							String sessionDurationArr [] = ((String) eoTeacherSalaryDetail.lkClassDuration.duration+"").split(" ");
							double sessionDuration = Double.parseDouble(sessionDurationArr[0]);
							//System.out.println("sessionDuration:"+sessionDuration);
							totalMins = 15 * timeData.size();
							session = totalMins/sessionDuration;
							totalAmount = session * eoTeacherSalaryDetail.amount;
							
							tempMap.put("totalMins", totalMins);
							tempMap.put("session", session);
							tempMap.put("totalSalary", Math.round(totalAmount));
						}
						
						String qry ="SELECT  t.date, a.status, a.cancellation_percentage, c.course_name, c.primary_key as coursepk"
							+" FROM eotime_slot_allocation a "
							+" LEFT JOIN eotimeslot t ON eotimeslot_primary_key = t.primary_key"
							+" LEFT JOIN eobatch b ON b.primary_key = a.eobatch_primary_Key"
							+" LEFT JOIN eocourses c ON c.primary_key = b.eocourses_primary_Key"
							+" WHERE a.eoteacheruser_primary_key = "+eoTeacherSalary.eoTeacherUser.primaryKey+" AND t.date BETWEEN '"+startDate+"' AND '"+endDate+"'"
							+" AND a.status = 'Cancelled' AND a.teacher_cancellation = false AND a.is_teacher_paid = true "
							+" AND c.primary_key = "+(eoTeacherSalaryDetail.eoCourses != null ? eoTeacherSalaryDetail.eoCourses.primaryKey : null);
						//System.out.println("qry::"+qry);
						List<HashMap<String, Object>> cancelledData = DBServices.getNativeQueryResult(qry);
						if(cancelledData != null && cancelledData.size() > 0){
							float cancellationPercentage = 0;
							for(HashMap<String, Object> obj1 : cancelledData){
								cancellationPercentage += Float.valueOf(obj1.get("cancellation_percentage")+"");
							}
							float cancellationAvg = cancellationPercentage/ cancelledData.size();
							
							float duration = Float.valueOf((eoTeacherSalaryDetail.lkClassDuration.duration+"").split(" ")[0]);
							float totalCancelledAmount = (Float.valueOf((eoTeacherSalaryDetail.amount)+"")/duration)*(15*cancelledData.size());
							float actualCancellationAMount = (float) (totalCancelledAmount *(cancellationAvg * 0.01));
							tempMap.put("cancellationAmount", Math.round(actualCancellationAMount));
						}else{
							tempMap.put("cancellationAmount", 0);
						}
						coursedetails.add(tempMap);
					}
					innerMap.put("courseData", coursedetails);
				}				
				List<HashMap<String, Object>> transportDays = new ArrayList<>();
				 
				String qr = "SELECT  distinct(t.date) FROM eotime_slot_allocation a "
							+" LEFT JOIN eotimeslot t ON eotimeslot_primary_key = t.primary_key"
							+" WHERE a.eoteacheruser_primary_key = "+pk+" AND t.date BETWEEN '"+startDate+"' AND '"+endDate+"'"
							+" AND a.status != 'Requested' AND a.status != 'Cancelled' AND a.teacher_cancellation = false AND a.is_teacher_paid = true" ;
				transportDays = DBServices.getNativeQueryResult(qr);
				int val = transportDays.size();
				innerMap.put("transportDays", val);
				resultList.add(innerMap);
			}	
		}	
		
		/*List<Object> dataListMain = DBServices.get
				("FROM EOTeacherSalaryDetail WHERE isActive = true ");*/

		List<HashMap<String, Object>> mainResultList = new ArrayList<>();
		
		List<Object> mainTeacherList = DBServices.get("FROM EOGenerateSlip WHERE isActive = true AND month='"+month+"' AND year='"+year+"'");
		
		if (mainTeacherList != null && mainTeacherList.size() > 0) {
			//List<HashMap<String, Object>> tempList = new ArrayList<>();
			for(Object obj : mainTeacherList){
				EOGenerateSlip eoGenerateSlip = (EOGenerateSlip) obj;
				HashMap<String, Object> mainMap = new HashMap<>();
				
				mainMap.put("primaryKey", eoGenerateSlip.primaryKey+"");
				mainMap.put("eoTeacherUser", eoGenerateSlip.eoTeacherUser.primaryKey+"");
				mainMap.put("joiningDate", eoGenerateSlip.eoTeacherUser.joiningDate);
				mainMap.put("teacherfullname", eoGenerateSlip.eoTeacherUser.getFullName());
				mainMap.put("teacherfullnamePdf", eoGenerateSlip.eoTeacherUser.getFullNameJapanese());
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
				mainMap.put("addDesc", eoGenerateSlip.addDesc);
				mainMap.put("subDesc", eoGenerateSlip.subDesc);;
				
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
						tempMap.put("cancellationAmount", eoGenerateSlipDetail.cancellationAmount);
						
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
						tempMap.put("sortingKey",eoGenerateSlipStudentDetail.date+"-"+eoGenerateSlipStudentDetail.startTime+"-"+eoGenerateSlipStudentDetail.endTime);
						
						studentData.add(tempMap);
					}
					mainMap.put("studentList", studentData);
				}
				
				mainResultList.add(mainMap);
			}
		}
		
		List<HashMap<String, Object>> finalList = new ArrayList<>();
		if (mainResultList != null && mainResultList.size() > 0) {
			finalList = mainResultList;
		}
		
		if(mainResultList != null && mainResultList.size() > 0){
			if(resultList != null && resultList.size() > 0){
				for(HashMap<String, Object> list : resultList){
					boolean flag = true;
					for(HashMap<String, Object> mainList : mainResultList){
						if(mainList.get("eoTeacherUser").equals(list.get("eoTeacherUser"))){
							flag = false;
						}
					}
					if(flag){
						finalList.add(list);
					}
				}
			}
		}
		
		if(mainResultList == null || mainResultList.size() == 0){
			finalList = resultList;
		}
		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}
	
	
	@POST
	@Path("/getSalaryByTeacherPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSalaryByTeacherPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String year = (String) map.get("year");
		String teacherPk = (String) map.get("teacherPk");
		String date = (String) map.get("date");
		
		List<HashMap<String, Object>> mainResultList = new ArrayList<>();
		
		List<Object> mainTeacherList = DBServices.
				get("FROM EOGenerateSlip WHERE isActive = true AND eoTeacherUser = "+teacherPk+" AND year = '"+year+"'");
		
		if (mainTeacherList != null && mainTeacherList.size() > 0) {
			for(Object obj : mainTeacherList){
				EOGenerateSlip eoGenerateSlip = (EOGenerateSlip) obj;
				HashMap<String, Object> mainMap = new HashMap<>();
				
				mainMap.put("primaryKey", eoGenerateSlip.primaryKey+"");
				mainMap.put("eoTeacherUser", eoGenerateSlip.eoTeacherUser.primaryKey+"");
				mainMap.put("joiningDate", eoGenerateSlip.eoTeacherUser.joiningDate);
				mainMap.put("teacherfullname", eoGenerateSlip.eoTeacherUser.getFullName());
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
				mainMap.put("addDesc", eoGenerateSlip.addDesc);
				mainMap.put("subDesc", eoGenerateSlip.subDesc);
				
				List<Object> salaryDetails = DBServices.get("FROM EOGenerateSlipDetail WHERE isActive = true AND "
						+ "eogenerateslip_primary_key ="+eoGenerateSlip.primaryKey );
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
						tempMap.put("cancellationAmount", eoGenerateSlipDetail.cancellationAmount);
						
						coursedetails.add(tempMap);
					}
					mainMap.put("courseData", coursedetails);
				}
				mainResultList.add(mainMap);
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(mainResultList)).build();
	}
}
