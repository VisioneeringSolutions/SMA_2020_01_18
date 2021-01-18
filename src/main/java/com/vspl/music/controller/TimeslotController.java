package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.print.attribute.HashAttributeSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.ListIndexBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOStudentUser;
import com.vspl.music.model.eo.EOTimeSlot;
import com.vspl.music.services.DBServices;
import com.vspl.music.services.SlotCreationMail;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.JSONUtil;
import com.vspl.music.util.VSPLUtil;

/**
 * The TimeslotController program implements an application that Manage Time
 * slot for teacher and student
 *
 * @author Kundan Kumar
 * @since
 */
@Path("/ajaxTimeSlot")
public class TimeslotController {
	public static Logger logger = LoggerFactory.getLogger(TimeslotController.class);
	public static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unchecked")
	@POST
	@Path("/createTimeSlot")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTimeSlot(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
	
		HashMap<String, Object> dataMap = (HashMap<String, Object>) map.get("dataForm");
		HashMap<String, Object> timeSlotData = (HashMap<String, Object>) map.get("timeSlotData");

		for (Map.Entry<String, Object> entry1 : timeSlotData.entrySet()) {
			String str = (String) entry1.getKey();
			String date = str.split("_")[1];
			String month = DateUtil.shortMonthName(formater.parse(date));
			int year = DateUtil.year(date);
			String status = "";
			try {
				String deleteQry = "DELETE FROM EODefinedSlot WHERE date = '" + date + "'";
				status = DataUtil.factory().deleteTableData(deleteQry);
			} catch (Exception e) {
				// return Response.status(201).entity(JSONUtil.objectToJson("Failure")).build();
				continue;
			}

			Gson gson = new Gson();
			System.out.println("status:::::" + status);
			if (status.equalsIgnoreCase("Success")) {
				for (Map.Entry<String, Object> entry2 : ((HashMap<String, Object>) entry1.getValue()).entrySet()) {

					for (Map.Entry<String, Object> entry3 : ((HashMap<String, Object>) entry2.getValue()).entrySet()) {
						LinkedHashMap<String, Object> slotDataObj = (LinkedHashMap<String, Object>) entry3.getValue();
						String eoSlotValue = gson.toJson(slotDataObj.get("eoSlot"));
						slotDataObj.put("eoSlot", eoSlotValue);
						slotDataObj.put("month", month);
						slotDataObj.put("year", year);
						slotDataObj.put("className", "EODefinedSlot");
						DBServices.create(EOObject.createObject(slotDataObj));
					}
				}
			} else {
				return Response.status(201).entity(JSONUtil.objectToJson("Failure")).build();
			}
		}

		for (Map.Entry<String, Object> entry1 : dataMap.entrySet()) {

			Object valueKey = entry1.getKey();
			Object valueMap = entry1.getValue();
			String str = (String) valueKey;
			String[] arrOfStr = str.split("_");
			String date = arrOfStr[1];
			String day = arrOfStr[0];

			HashMap<String, Object> timeSlotMap = new HashMap<>();
			for (Map.Entry<String, Object> entry2 : ((HashMap<String, Object>) valueMap).entrySet()) {

				if (((HashMap<String, Object>) entry2.getValue()).get("primaryKey") != null) {
					String status = (String) ((HashMap<String, Object>) entry2.getValue()).get("status");
					Integer pk = (Integer) ((HashMap<String, Object>) entry2.getValue()).get("primaryKey");
					EOObject eoObject = EOObject.getObjectByPK("EOTimeSlot", pk);
					timeSlotMap.put("status", status);
					timeSlotMap.put("className", "EOTimeSlot");
					timeSlotMap.put("updatedDate", DateUtil.formatedCurrentDate());
					DBServices.update(EOObject.updateObject(eoObject, timeSlotMap));
					List<HashMap<String, Object>> teacherList = (List<HashMap<String, Object>>) ((HashMap<String, Object>) entry2
							.getValue()).get("teacherList");

					if (teacherList != null) {
						for (HashMap<String, Object> obj : teacherList) {
							System.out.println("colorCode111::" + obj.get("colorCode"));
							obj.remove("colorCode");
							obj.remove("courseName");

							EOObject eoObject2 = EOObject.getObjectByPK("EOTimeSlotAllocation",
									(Integer) obj.get("primaryKey"));
							DBServices.update(EOObject.updateObject(eoObject2, obj));
							Integer pkvalue = (Integer) obj.get("primaryKey");
							System.out.println("pkvalue::" + pk);

							/* vishuja added in case of cancellation by admin or student slot will be shown available(green) on behalf of teacher */
							   if((((String)obj.get("status")).equalsIgnoreCase("Cancelled") && (obj.get("teacherCancellation").equals(true)))
							  ){ 
							  String qry ="DELETE FROM EOTimeSlotAllocation WHERE eoTimeSlot.primaryKey ="+pk;
							
							  DBServices.updateNativeQueryResult(qry); 
							  }
							 
						}
					}
				} else {
					String status = (String) ((HashMap<String, Object>) entry2.getValue()).get("status");
					String timeString = (String) entry2.getKey();
					String[] timeStringArray = timeString.split("-");
					String startTime = timeStringArray[0];
					String endTime = timeStringArray[1];
					timeSlotMap.put("day", day);
					timeSlotMap.put("date", date);
					timeSlotMap.put("startTime", startTime);
					timeSlotMap.put("endTime", endTime);
					timeSlotMap.put("status", status);
					timeSlotMap.put("className", "EOTimeSlot");
					timeSlotMap.put("createdDate", DateUtil.formatedCurrentDate());
					DBServices.create(EOObject.createObject(timeSlotMap));
				}
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getTimeSlotForAdmin")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTimeSlotForAdmin(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String dateTo = (String) map.get("dateTo");
		String dateFrom = (String) map.get("dateFrom");

		String str = "Select ts.primary_key,ts.date,ts.day,ts.end_time,ts.start_time,ts.status,CONCAT(ts.day,'_',ts.date) as timeslotkey,ta.is_teacher_paid,"
				+ " CONCAT(ts.start_time,'-',ts.end_time)as timeslot, ta.primary_key as allocationpk,lk.music_type,ta.cancellation_amount,ta.cancellation_percentage,tu.color_code,tu.is_active, "
				+ " CONCAT(tu.first_name, ' ', tu.last_name) as teacherfullname,cu.lkmusictype_primary_key,ta.student_cancellation,ta.admin_cancellation,cu.fees,cu.course_name,cu.lkcategorytype_primary_key,"
				+ " CONCAT(su.first_name, ' ', su.last_name) as studentfullname, b.batch_name, m.room_name,ta.teacher_cancellation,lkd.duration,"
				+ " ta.cancellation_request_date,ta.eobatch_primary_key, ta.eoroom_primary_key,ta.eoteacheruser_primary_key,b.batch_id,m.room_id, su.student_id,"
				+ " ta.eotimeslot_primary_key,ta.status as allocationstatus, tu.teacher_id, ta.eostudentuser_primary_key FROM eotimeslot ts"
				+ " LEFT JOIN eotime_slot_allocation ta on ts.primary_key = ta.eotimeslot_primary_key "
				+ " LEFT JOIN eobatch b on b.primary_key = ta.eobatch_primary_key "
				+ " LEFT JOIN eomusic_room m on m.primary_key = ta.eoroom_primary_key"
				+ " LEFT JOIN eoteacher_user tu on tu.primary_key = ta.eoteacheruser_primary_key"
				+ " LEFT JOIN eostudent_user su on su.primary_key = ta.eostudentuser_primary_key"
				+ " LEFT JOIN eocourses cu on cu.primary_key = b.eocourses_primary_key"
				+ " LEFT JOIN lkclass_duration lkd on lkd.primary_key = cu.lkclassduration_primary_key"
				+ " LEFT JOIN lkmusic_type lk on lk.primary_key = cu.lkmusictype_primary_key"
				+ " Where ts.date BETWEEN '" + dateFrom + "' AND '" + dateTo + "' ORDER BY ts.date ";

		List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(str);

		Map<Object, Map<Object, HashMap<String, Object>>> respMap = new HashMap<>();

		for (Object obj : dataList) {
			HashMap<String, Object> innerMap = (HashMap<String, Object>) obj;
			Map<Object, HashMap<String, Object>> mainMap = respMap.get(innerMap.get("timeslotkey"));

			if (mainMap == null) {
				mainMap = new HashMap<>();
				HashMap<String, Object> detailMap = new HashMap<>();
				detailMap.put("primaryKey", innerMap.get("primary_key"));
				detailMap.put("status", innerMap.get("status"));
				detailMap.put("date", innerMap.get("date"));
				detailMap.put("day", innerMap.get("day"));
				detailMap.put("endTime", innerMap.get("end_time"));
				detailMap.put("startTime", innerMap.get("start_time"));
				detailMap.put("status", innerMap.get("status"));

				List<HashMap<String, Object>> teacherList = new ArrayList<>();
				if (innerMap.get("allocationpk") != null) {
					HashMap<String, Object> teacherMap = new HashMap<>();
					teacherMap.put("primaryKey", innerMap.get("allocationpk"));
					teacherMap.put("status", innerMap.get("allocationstatus"));
					teacherMap.put("colorCode", innerMap.get("color_code"));
					teacherMap.put("courseName", innerMap.get("course_name"));
					teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
					teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
					teacherMap.put("teacherId", innerMap.get("teacher_id"));
					teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
					teacherMap.put("studentId", innerMap.get("student_id"));
					teacherMap.put("studentFullName", innerMap.get("studentfullname"));
					teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
					teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
					teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
					teacherMap.put("cancellationAmount", innerMap.get("cancellation_amount"));
					teacherMap.put("cancellationPercentage",
							(innerMap.get("cancellation_percentage") != null
									&& !innerMap.get("cancellation_percentage").equals(""))
											? innerMap.get("cancellation_percentage")
											: 0);
					teacherMap.put("cancellationRequestDate", innerMap.get("cancellation_request_date"));
					teacherMap.put("duration", innerMap.get("duration"));
					teacherMap.put("fees", innerMap.get("fees"));
					teacherMap.put("isTeacherPaid", innerMap.get("is_teacher_paid"));
					teacherMap.put("isActive", innerMap.get("is_active"));
					teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

					if (innerMap.get("eobatch_primary_key") != null) {
						teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
						teacherMap.put("batchId", innerMap.get("batch_id"));
						teacherMap.put("batchName", innerMap.get("batch_name"));
					}
					if (innerMap.get("eostudentuser_primary_key") != null) {
						teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
						teacherMap.put("studentId", innerMap.get("student_id"));
						teacherMap.put("studentFullName", innerMap.get("studentfullname"));
					}
					if (innerMap.get("eoroom_primary_key") != null) {
						teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
						teacherMap.put("roomId", innerMap.get("room_id"));
						teacherMap.put("roomName", innerMap.get("room_name"));
					}
					if (innerMap.get("lkmusictype_primary_key") != null) {
						teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
						teacherMap.put("musicType", innerMap.get("music_type"));

					}
					teacherList.add(teacherMap);
					detailMap.put("teacherList", teacherList);
				}
				mainMap.put(innerMap.get("timeslot"), detailMap);
				respMap.put(innerMap.get("timeslotkey"), mainMap);
			} else {
				
				if (respMap.containsKey(innerMap.get("timeslotkey"))) {
					Map<Object, HashMap<String, Object>> tempDetailMap = (Map<Object, HashMap<String, Object>>) respMap
							.get(innerMap.get("timeslotkey"));

					// If timeslot is present in result map
					if (tempDetailMap.containsKey(innerMap.get("timeslot"))) {
						HashMap<String, Object> slotMap = (HashMap<String, Object>) tempDetailMap
								.get(innerMap.get("timeslot"));
						if (innerMap.get("allocationpk") != null) {
							List<HashMap<String, Object>> teacherList = new ArrayList<>();
							if (slotMap.get("teacherList") != null) {
								teacherList = (List<HashMap<String, Object>>) slotMap.get("teacherList");
								HashMap<String, Object> teacherMap = new HashMap<>();
								teacherMap.put("primaryKey", innerMap.get("allocationpk"));
								teacherMap.put("status", innerMap.get("allocationstatus"));
								teacherMap.put("colorCode", innerMap.get("color_code"));
								teacherMap.put("courseName", innerMap.get("course_name"));
								teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
								teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
								teacherMap.put("teacherId", innerMap.get("teacher_id"));
								teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
								teacherMap.put("studentId", innerMap.get("student_id"));
								teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
								teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
								teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
								teacherMap.put("cancellationAmount", innerMap.get("cancellation_amount"));
								teacherMap.put("cancellationPercentage",
										(innerMap.get("cancellation_percentage") != null
												&& !innerMap.get("cancellation_percentage").equals(""))
														? innerMap.get("cancellation_percentage")
														: 0);
								teacherMap.put("cancellationRequestDate", innerMap.get("cancellation_request_date"));
								teacherMap.put("duration", innerMap.get("duration"));
								teacherMap.put("fees", innerMap.get("fees"));
								teacherMap.put("isTeacherPaid", innerMap.get("is_teacher_paid"));
								teacherMap.put("isActive", innerMap.get("is_active"));
								teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

								if (innerMap.get("eobatch_primary_key") != null) {
									teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
									teacherMap.put("batchId", innerMap.get("batch_id"));
									teacherMap.put("batchName", innerMap.get("batch_name"));
								}
								if (innerMap.get("eostudentuser_primary_key") != null) {
									teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
									teacherMap.put("studentId", innerMap.get("student_id"));
									teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								}
								if (innerMap.get("eoroom_primary_key") != null) {
									teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
									teacherMap.put("roomId", innerMap.get("room_id"));
									teacherMap.put("roomName", innerMap.get("room_name"));
								}
								if (innerMap.get("lkmusictype_primary_key") != null) {
									teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
									teacherMap.put("musicType", innerMap.get("music_type"));

								}
								teacherList.add(teacherMap);

							} else {
								HashMap<String, Object> teacherMap = new HashMap<>();
								teacherMap.put("primaryKey", innerMap.get("allocationpk"));
								teacherMap.put("status", innerMap.get("allocationstatus"));
								teacherMap.put("colorCode", innerMap.get("color_code"));
								teacherMap.put("courseName", innerMap.get("course_name"));
								teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
								teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
								teacherMap.put("teacherId", innerMap.get("teacher_id"));
								teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
								teacherMap.put("studentId", innerMap.get("student_id"));
								teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
								teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
								teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
								teacherMap.put("cancellationAmount", innerMap.get("cancellation_amount"));
								teacherMap.put("cancellationPercentage",
										(innerMap.get("cancellation_percentage") != null
												&& !innerMap.get("cancellation_percentage").equals(""))
														? innerMap.get("cancellation_percentage")
														: 0);
								teacherMap.put("cancellationRequestDate", innerMap.get("cancellation_request_date"));
								teacherMap.put("duration", innerMap.get("duration"));
								teacherMap.put("fees", innerMap.get("fees"));
								teacherMap.put("isTeacherPaid", innerMap.get("is_teacher_paid"));
								teacherMap.put("isActive", innerMap.get("is_active"));
								teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

								if (innerMap.get("eobatch_primary_key") != null) {
									teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
									teacherMap.put("batchId", innerMap.get("batch_id"));
									teacherMap.put("batchName", innerMap.get("batch_name"));
								}
								if (innerMap.get("eostudentuser_primary_key") != null) {
									teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
									teacherMap.put("studentId", innerMap.get("student_id"));
									teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								}
								if (innerMap.get("eoroom_primary_key") != null) {
									teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
									teacherMap.put("roomId", innerMap.get("room_id"));
									teacherMap.put("roomName", innerMap.get("room_name"));
								}
								if (innerMap.get("lkmusictype_primary_key") != null) {
									teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
									teacherMap.put("musicType", innerMap.get("music_type"));

								}
								teacherList.add(teacherMap);
							}
							slotMap.put("teacherList", teacherList);
						}
						tempDetailMap.put(innerMap.get("timeslot"), slotMap);
					} else { // if timeslot is not present in resultmap

						HashMap<String, Object> detailMap = new HashMap<>();
						detailMap.put("primaryKey", innerMap.get("primary_key"));
						detailMap.put("status", innerMap.get("status"));
						detailMap.put("date", innerMap.get("date"));
						detailMap.put("day", innerMap.get("day"));
						detailMap.put("endTime", innerMap.get("end_time"));
						detailMap.put("startTime", innerMap.get("start_time"));
						detailMap.put("status", innerMap.get("status"));

						List<HashMap<String, Object>> teacherList = new ArrayList<>();
						if (innerMap.get("allocationpk") != null) {
							HashMap<String, Object> teacherMap = new HashMap<>();
							teacherMap.put("primaryKey", innerMap.get("allocationpk"));
							teacherMap.put("status", innerMap.get("allocationstatus"));
							teacherMap.put("colorCode", innerMap.get("color_code"));
							teacherMap.put("courseName", innerMap.get("course_name"));
							teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
							teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
							teacherMap.put("teacherId", innerMap.get("teacher_id"));
							teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
							teacherMap.put("studentId", innerMap.get("student_id"));
							teacherMap.put("studentFullName", innerMap.get("studentfullname"));
							teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
							teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
							teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
							teacherMap.put("cancellationAmount", innerMap.get("cancellation_amount"));
							teacherMap.put("cancellationPercentage",
									(innerMap.get("cancellation_percentage") != null
											&& !innerMap.get("cancellation_percentage").equals(""))
													? innerMap.get("cancellation_percentage")
													: 0);
							teacherMap.put("cancellationRequestDate", innerMap.get("cancellation_request_date"));
							teacherMap.put("duration", innerMap.get("duration"));
							teacherMap.put("fees", innerMap.get("fees"));
							teacherMap.put("isTeacherPaid", innerMap.get("is_teacher_paid"));
							teacherMap.put("isActive", innerMap.get("is_active"));
							teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

							if (innerMap.get("eobatch_primary_key") != null) {
								teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
								teacherMap.put("batchId", innerMap.get("batch_id"));
								teacherMap.put("batchName", innerMap.get("batch_name"));
							}
							if (innerMap.get("eostudentuser_primary_key") != null) {
								teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
								teacherMap.put("studentId", innerMap.get("student_id"));
								teacherMap.put("studentFullName", innerMap.get("studentfullname"));
							}
							if (innerMap.get("eoroom_primary_key") != null) {
								teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
								teacherMap.put("roomId", innerMap.get("room_id"));
								teacherMap.put("roomName", innerMap.get("room_name"));
							}
							if (innerMap.get("lkmusictype_primary_key") != null) {
								teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
								teacherMap.put("musicType", innerMap.get("music_type"));

							}
							teacherList.add(teacherMap);
							detailMap.put("teacherList", teacherList);
						}
						tempDetailMap.put(innerMap.get("timeslot"), detailMap);

					}
					respMap.put(innerMap.get("timeslotkey"), tempDetailMap);
				}
			}
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson(respMap)).build();
	}

	@POST
	@Path("/createTimeSlotForTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTimeSlotForTeacher(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Map.Entry<String, Object> entry1 : map.entrySet()) {

			Object valueKey = entry1.getKey();
			Object valueMap = entry1.getValue();

			for (Map.Entry<String, Object> entry2 : ((HashMap<String, Object>) valueMap).entrySet()) {

				HashMap<String, Object> timeMap = (HashMap<String, Object>) entry2.getValue();

				List<HashMap<String, Object>> teacherList = (List<HashMap<String, Object>>) ((HashMap<String, Object>) entry2
						.getValue()).get("teacherList");

				if (teacherList != null) {

					for (HashMap<String, Object> obj : teacherList) {
						if (obj.get("primaryKey") != null) {
							obj.remove("courseName");
							// update
							System.out.println("pkvalue::" + obj.get("primaryKey"));
							Integer pk = (Integer) obj.get("primaryKey");

							if (((String) obj.get("status")).equalsIgnoreCase("canceled")) {

								String qry = "DELETE FROM EOTimeSlotAllocation WHERE primary_key =" + pk;
								DBServices.updateNativeQueryResult(qry);
							} else {
								obj.put("updatedDate", DateUtil.formatedCurrentDate());
								obj.put("className", "EOTimeSlotAllocation");
								EOObject eoObject = EOObject.getObjectByPK("EOTimeSlotAllocation",
										(Integer) obj.get("primaryKey"));
								DBServices.update(EOObject.updateObject(eoObject, obj));
							}
						} else {
							// create
							obj.put("createdDate", DateUtil.formatedCurrentDate());
							obj.put("className", "EOTimeSlotAllocation");
							DBServices.create(EOObject.createObject(obj));
						}
					}

					/*
					 * HashMap<String, Object> teacherMap = teacherList.get(0);
					 * if(teacherMap.get("primaryKey") != null){ //update Integer pk = (Integer)
					 * teacherMap.get("primaryKey");
					 * 
					 * if(((String)teacherMap.get("status")).equalsIgnoreCase("canceled")){
					 * 
					 * String qry = "DELETE FROM EOTimeSlotAllocation WHERE primary_key ="+pk;
					 * DBServices.updateNativeQueryResult(qry); } else{
					 * teacherMap.put("updatedDate", DateUtil.formatedCurrentDate());
					 * teacherMap.put("className", "EOTimeSlotAllocation"); EOObject eoObject =
					 * EOObject.getObjectByPK("EOTimeSlotAllocation", (Integer)
					 * teacherMap.get("primaryKey") );
					 * DBServices.update(EOObject.updateObject(eoObject, teacherMap)); } } else{
					 * //create teacherMap.put("createdDate", DateUtil.formatedCurrentDate());
					 * teacherMap.put("className", "EOTimeSlotAllocation");
					 * DBServices.create(EOObject.createObject(teacherMap)); }
					 */
				} else {
					// no action
					System.out.println("no action");
				}
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

	@POST
	@Path("/getTimeSlotForTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTimeSlotForTeacher(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String dateTo = (String) map.get("dateTo");
		String dateFrom = (String) map.get("dateFrom");
		String userPk = (String) map.get("userPk");

		String str = "Select ts.primary_key,ts.date,ts.day,ts.end_time,ts.start_time,ts.status,CONCAT(ts.day,'_',ts.date) as timeslotkey, cu.lkcategorytype_primary_key,cu.course_name,"
				+ " CONCAT(ts.start_time,'-',ts.end_time)as timeslot, ta.primary_key as allocationpk, ta.eobatch_primary_key, "
				+ " ta.eoroom_primary_key,ta.eoteacheruser_primary_key,b.batch_id,m.room_id, ta.eostudentuser_primary_key,su.student_id,"
				+ " CONCAT(su.first_name, ' ', su.last_name) as studentfullname, m.room_name, b.eocourses_primary_key,b.batch_name,ta.teacher_cancellation,"
				+ " ta.student_cancellation,ta.admin_cancellation,ta.eotimeslot_primary_key,ta.status as allocationstatus FROM eotimeslot ts LEFT JOIN eotime_slot_allocation ta "
				+ " on ts.primary_key = ta.eotimeslot_primary_key AND ta.eoteacheruser_primary_key = " + userPk
				+ " LEFT JOIN eobatch b on b.primary_key = ta.eobatch_primary_key "
				+ " LEFT JOIN eocourses cu on cu.primary_key = b.eocourses_primary_key"
				+ " LEFT JOIN eomusic_room m on m.primary_key = ta.eoroom_primary_key"
				+ " LEFT JOIN eostudent_user su on su.primary_key = ta.eostudentuser_primary_key"
				+ " Where ts.date BETWEEN '" + dateFrom + "' AND '" + dateTo + "' ORDER BY ts.date ";

		//System.out.println("str for teacher::" + str);
		List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(str);

		// Map<Object,HashMap<String, Object>> respMap = new HashMap<>();
		Map<Object, Map<Object, HashMap<String, Object>>> respMap = new HashMap<>();

		for (Object obj : dataList) {
			HashMap<String, Object> innerMap = (HashMap<String, Object>) obj;
			Map<Object, HashMap<String, Object>> mainMap = respMap.get(innerMap.get("timeslotkey"));

			if (mainMap == null) {
				mainMap = new HashMap<>();
				HashMap<String, Object> detailMap = new HashMap<>();
				detailMap.put("primaryKey", innerMap.get("primary_key"));
				detailMap.put("status", innerMap.get("status"));
				detailMap.put("date", innerMap.get("date"));
				detailMap.put("day", innerMap.get("day"));
				detailMap.put("endTime", innerMap.get("end_time"));
				detailMap.put("startTime", innerMap.get("start_time"));
				detailMap.put("status", innerMap.get("status"));

				List<HashMap<String, Object>> teacherList = new ArrayList<>();
				if (innerMap.get("allocationpk") != null) {
					HashMap<String, Object> teacherMap = new HashMap<>();
					teacherMap.put("primaryKey", innerMap.get("allocationpk"));
					teacherMap.put("status", innerMap.get("allocationstatus"));
					teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
					teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
					teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
					teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
					teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
					teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

					if (innerMap.get("eobatch_primary_key") != null) {
						teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
						teacherMap.put("batchId", innerMap.get("batch_id"));
						teacherMap.put("batchName", innerMap.get("batch_name"));
					}
					if (innerMap.get("eostudentuser_primary_key") != null) {
						teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
						teacherMap.put("studentId", innerMap.get("student_id"));
						teacherMap.put("studentFullName", innerMap.get("studentfullname"));
					}
					if (innerMap.get("eoroom_primary_key") != null) {
						teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
						teacherMap.put("roomId", innerMap.get("room_id"));
						teacherMap.put("roomName", innerMap.get("room_name"));
					}
					if (innerMap.get("lkmusictype_primary_key") != null) {
						teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
						teacherMap.put("musicType", innerMap.get("music_type"));
						

					}
					if (innerMap.get("eocourses_primary_key") != null) {
						teacherMap.put("courseName", innerMap.get("course_name"));
						
					}
					teacherList.add(teacherMap);
					detailMap.put("teacherList", teacherList);
				}
				mainMap.put(innerMap.get("timeslot"), detailMap);
				respMap.put(innerMap.get("timeslotkey"), mainMap);
			} else {
				if (respMap.containsKey(innerMap.get("timeslotkey"))) {
					Map<Object, HashMap<String, Object>> tempDetailMap = (Map<Object, HashMap<String, Object>>) respMap
							.get(innerMap.get("timeslotkey"));

					// If timeslot is present in result map
					if (tempDetailMap.containsKey(innerMap.get("timeslot"))) {
						HashMap<String, Object> slotMap = (HashMap<String, Object>) tempDetailMap
								.get(innerMap.get("timeslot"));
						if (innerMap.get("allocationpk") != null) {
							List<HashMap<String, Object>> teacherList = new ArrayList<>();
							if (slotMap.get("teacherList") != null) {
								teacherList = (List<HashMap<String, Object>>) slotMap.get("teacherList");
								HashMap<String, Object> teacherMap = new HashMap<>();
								teacherMap.put("primaryKey", innerMap.get("allocationpk"));
								teacherMap.put("status", innerMap.get("allocationstatus"));
								teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
								teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
								teacherMap.put("studentId", innerMap.get("student_id"));
								teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
								teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
								teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
								teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

								if (innerMap.get("eobatch_primary_key") != null) {
									teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
									teacherMap.put("batchId", innerMap.get("batch_id"));
									teacherMap.put("batchName", innerMap.get("batch_name"));
								}
								if (innerMap.get("eostudentuser_primary_key") != null) {
									teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
									teacherMap.put("studentId", innerMap.get("student_id"));
									teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								}
								if (innerMap.get("eoroom_primary_key") != null) {
									teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
									teacherMap.put("roomId", innerMap.get("room_id"));
									teacherMap.put("roomName", innerMap.get("room_name"));
								}
								if (innerMap.get("lkmusictype_primary_key") != null) {
									teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
									teacherMap.put("musicType", innerMap.get("music_type"));

								}
								if (innerMap.get("eocourses_primary_key") != null) {
									teacherMap.put("courseName", innerMap.get("course_name"));
									
								}
								teacherList.add(teacherMap);

							} else {
								HashMap<String, Object> teacherMap = new HashMap<>();
								teacherMap.put("primaryKey", innerMap.get("allocationpk"));
								teacherMap.put("status", innerMap.get("allocationstatus"));
								teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
								teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
								teacherMap.put("studentId", innerMap.get("student_id"));
								teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
								teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
								teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
								teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

								if (innerMap.get("eobatch_primary_key") != null) {
									teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
									teacherMap.put("batchId", innerMap.get("batch_id"));
									teacherMap.put("batchName", innerMap.get("batch_name"));
								}
								if (innerMap.get("eostudentuser_primary_key") != null) {
									teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
									teacherMap.put("studentId", innerMap.get("student_id"));
									teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								}
								if (innerMap.get("eoroom_primary_key") != null) {
									teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
									teacherMap.put("roomId", innerMap.get("room_id"));
									teacherMap.put("roomName", innerMap.get("room_name"));
								}
								if (innerMap.get("lkmusictype_primary_key") != null) {
									teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
									teacherMap.put("musicType", innerMap.get("music_type"));

								}
								if (innerMap.get("eocourses_primary_key") != null) {
									teacherMap.put("courseName", innerMap.get("course_name"));
									
								}
								teacherList.add(teacherMap);
							}
							slotMap.put("teacherList", teacherList);
						}
						tempDetailMap.put(innerMap.get("timeslot"), slotMap);
					} else { // if timeslot is not present in resultmap

						HashMap<String, Object> detailMap = new HashMap<>();
						detailMap.put("primaryKey", innerMap.get("primary_key"));
						detailMap.put("status", innerMap.get("status"));
						detailMap.put("date", innerMap.get("date"));
						detailMap.put("day", innerMap.get("day"));
						detailMap.put("endTime", innerMap.get("end_time"));
						detailMap.put("startTime", innerMap.get("start_time"));
						detailMap.put("status", innerMap.get("status"));

						List<HashMap<String, Object>> teacherList = new ArrayList<>();
						if (innerMap.get("allocationpk") != null) {
							HashMap<String, Object> teacherMap = new HashMap<>();
							teacherMap.put("primaryKey", innerMap.get("allocationpk"));
							teacherMap.put("status", innerMap.get("allocationstatus"));
							teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
							teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
							teacherMap.put("studentId", innerMap.get("student_id"));
							teacherMap.put("studentFullName", innerMap.get("studentfullname"));
							teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
							teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
							teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));
							teacherMap.put("lkCategoryType", innerMap.get("lkcategorytype_primary_key"));

							if (innerMap.get("eobatch_primary_key") != null) {
								teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
								teacherMap.put("batchId", innerMap.get("batch_id"));
								teacherMap.put("batchName", innerMap.get("batch_name"));
							}
							if (innerMap.get("eostudentuser_primary_key") != null) {
								teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
								teacherMap.put("studentId", innerMap.get("student_id"));
								teacherMap.put("studentFullName", innerMap.get("studentfullname"));
							}
							if (innerMap.get("eoroom_primary_key") != null) {
								teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
								teacherMap.put("roomId", innerMap.get("room_id"));
								teacherMap.put("roomName", innerMap.get("room_name"));
							}
							if (innerMap.get("lkmusictype_primary_key") != null) {
								teacherMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
								teacherMap.put("musicType", innerMap.get("music_type"));

							}
							if (innerMap.get("eocourses_primary_key") != null) {
								teacherMap.put("courseName", innerMap.get("course_name"));
								
							}
							teacherList.add(teacherMap);
							detailMap.put("teacherList", teacherList);
						}
						tempDetailMap.put(innerMap.get("timeslot"), detailMap);

					}
					respMap.put(innerMap.get("timeslotkey"), tempDetailMap);
				}
			}
		}

		/*
		 * for(Object obj : dataList){ HashMap<String, Object> innerMap =
		 * (HashMap<String, Object>)obj; HashMap<String, Object> mainMap =
		 * respMap.get(innerMap.get("timeslotkey"));
		 * 
		 * if(mainMap == null){ mainMap = new HashMap<>(); HashMap<String, Object>
		 * detailMap = new HashMap<>(); detailMap.put("primaryKey",
		 * innerMap.get("primary_key")); detailMap.put("status",
		 * innerMap.get("status")); detailMap.put("date", innerMap.get("date"));
		 * detailMap.put("day", innerMap.get("day")); detailMap.put("endTime",
		 * innerMap.get("end_time")); detailMap.put("startTime",
		 * innerMap.get("start_time")); detailMap.put("status", innerMap.get("status"));
		 * 
		 * List<HashMap<String, Object>> teacherList = new ArrayList<>();
		 * if(innerMap.get("allocationpk") != null){ HashMap<String, Object> teacherMap
		 * = new HashMap<>();
		 * 
		 * teacherMap.put("primaryKey", innerMap.get("allocationpk"));
		 * teacherMap.put("status", innerMap.get("allocationstatus"));
		 * teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
		 * 
		 * teacherMap.put("studentFullname", innerMap.get("studentfullname"));
		 * teacherMap.put("studentId", innerMap.get("student_id"));
		 * teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
		 * teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
		 * 
		 * if(innerMap.get("eobatch_primary_key") != null){ teacherMap.put("eoBatch",
		 * innerMap.get("eobatch_primary_key")); teacherMap.put("batchId",
		 * innerMap.get("batch_id")); teacherMap.put("batchName",
		 * innerMap.get("batch_name")); } if(innerMap.get("eostudentuser_primary_key")
		 * != null){ teacherMap.put("eoStudentUser",
		 * innerMap.get("eostudentuser_primary_key")); teacherMap.put("studentId",
		 * innerMap.get("student_id")); teacherMap.put("studentFullName",
		 * innerMap.get("studentfullname")); } if(innerMap.get("eoroom_primary_key") !=
		 * null){ teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
		 * teacherMap.put("roomId", innerMap.get("room_id")); teacherMap.put("roomName",
		 * innerMap.get("room_name")); } teacherList.add(teacherMap);
		 * detailMap.put("teacherList", teacherList); }
		 * 
		 * mainMap.put((String) innerMap.get("timeslot"), detailMap);
		 * respMap.put(innerMap.get("timeslotkey"), mainMap);
		 * 
		 * } else{ if(respMap.containsKey(innerMap.get("timeslotkey"))){ HashMap<String,
		 * Object> tempDetailMap = (HashMap<String,
		 * Object>)respMap.get(innerMap.get("timeslotkey")); HashMap<String, Object>
		 * tempMap = new HashMap<>();
		 * 
		 * tempMap.put("primaryKey", innerMap.get("primary_key")); tempMap.put("status",
		 * innerMap.get("status")); tempMap.put("date", innerMap.get("date"));
		 * tempMap.put("day", innerMap.get("day")); tempMap.put("endTime",
		 * innerMap.get("end_time")); tempMap.put("startTime",
		 * innerMap.get("start_time")); tempMap.put("status", innerMap.get("status"));
		 * 
		 * if(innerMap.get("allocationpk") != null){ HashMap<String, Object> teacherMap
		 * = new HashMap<>(); List<HashMap<String, Object>> teacherList = new
		 * ArrayList<>(); teacherMap.put("primaryKey", innerMap.get("allocationpk"));
		 * teacherMap.put("status", innerMap.get("allocationstatus"));
		 * 
		 * teacherMap.put("studentFullname", innerMap.get("studentfullname"));
		 * teacherMap.put("studentId", innerMap.get("student_id"));
		 * 
		 * teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
		 * teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
		 * teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
		 * 
		 * if(innerMap.get("eobatch_primary_key") != null){ teacherMap.put("eoBatch",
		 * innerMap.get("eobatch_primary_key")); teacherMap.put("batchId",
		 * innerMap.get("batch_id")); teacherMap.put("batchName",
		 * innerMap.get("batch_name")); } if(innerMap.get("eostudentuser_primary_key")
		 * != null){ teacherMap.put("eoStudentUser",
		 * innerMap.get("eostudentuser_primary_key")); teacherMap.put("studentId",
		 * innerMap.get("student_id")); teacherMap.put("studentFullName",
		 * innerMap.get("studentfullname")); } if(innerMap.get("eoroom_primary_key") !=
		 * null){ teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
		 * teacherMap.put("roomId", innerMap.get("room_id")); teacherMap.put("roomName",
		 * innerMap.get("room_name")); } teacherList.add(teacherMap);
		 * tempMap.put("teacherList", teacherList); }
		 * 
		 * tempDetailMap.put((String) innerMap.get("timeslot"), tempMap); } } }
		 */
		return Response.status(201).entity(JSONUtil.objectToJson(respMap)).build();
	}

	@POST
	@Path("/getTimeSlotForStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTimeSlotForStudent(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String dateTo = (String) map.get("dateTo");
		String dateFrom = (String) map.get("dateFrom");
		String userPk = (String) map.get("userPk");

		String str = "SELECT sb.primary_key, sb.batch_type,sb.students_pk, sb.eobatch_primary_key,"
				+ " b.batch_name, b.batch_id,c.course_name,lk.music_type,lk.primary_key AS lkmusictype_primary_key"
				+ " FROM eostudent_batch sb LEFT JOIN eobatch b ON sb.eobatch_primary_key = b.primary_key"
				+ " LEFT JOIN eocourses c ON b.eocourses_primary_key = c.primary_key"
				+ " LEFT JOIN lkmusic_type lk ON c.lkmusictype_primary_key = lk.primary_key"
				+ " LEFT JOIN lkcategory_type cat ON c.lkcategorytype_primary_key = cat.primary_key"
				+ " WHERE sb.is_active = true";
		List<HashMap<String, Object>> studentBatchList = DBServices.getNativeQueryResult(str);

		List<Object> musicList = new ArrayList<>();
		List<Object> batchpk = new ArrayList<>();
		for (HashMap<String, Object> obj : studentBatchList) {
			List<String> pkList = (List<String>) JSONUtil.jsonToObject((String) obj.get("students_pk"), List.class);

			for (int k = 0; k < pkList.size(); k++) {

				if (pkList.get(k).equalsIgnoreCase(userPk)) {
					BigInteger temp = (BigInteger) obj.get("eobatch_primary_key");
					if (!musicList.contains(obj.get("lkmusictype_primary_key"))) {
						musicList.add(obj.get("lkmusictype_primary_key"));
					}
					batchpk.add(temp);
					break;
				}
			}
		}

		String musicPk = "(";
		for (int k = 0; k < musicList.size(); k++) {
			musicPk += musicList.get(k);
			if (k != (musicList.size() - 1)) {
				musicPk += ",";
			}
		}
		musicPk += ")";
		String batchPkString = "(";
		for (int k = 0; k < batchpk.size(); k++) {
			batchPkString += batchpk.get(k);
			if (k != (batchpk.size() - 1)) {
				batchPkString += ",";
			}
		}
		batchPkString += ")";

		String qry = "SELECT primary_key, music_category_pk FROM eoteacher_user WHERE is_active = true AND music_category_pk IS NOT NULL";
		List<HashMap<String, Object>> musicCategoryPk = DBServices.getNativeQueryResult(qry);

		List<BigInteger> teacherPk = new ArrayList<>();
		for (int k = 0; k < musicList.size(); k++) {

			for (int i = 0; i < musicCategoryPk.size(); i++) {
				List<String> pkObj = (List<String>) JSONUtil
						.jsonToObject((String) musicCategoryPk.get(i).get("music_category_pk"), List.class);
				BigInteger tpk = (BigInteger) musicCategoryPk.get(i).get("primary_key");

				for (int m = 0; m < pkObj.size(); m++) {

					String value = pkObj.get(m);
					String[] arrOfStr = value.split("_");
					String musicPkValue = arrOfStr[0];
					String categoryPk = arrOfStr[1];

					if (BigInteger.valueOf(Long.parseLong(musicPkValue)) == musicList.get(k)
							&& !teacherPk.contains(tpk)) {
						teacherPk.add(tpk);
						break;
					}
				}
			}
		}
		String teacherPkString = "(";
		for (int k = 0; k < teacherPk.size(); k++) {
			teacherPkString += teacherPk.get(k);
			if (k != (teacherPk.size() - 1)) {
				teacherPkString += ",";
			}
		}
		teacherPkString += ")";

		if (batchpk != null && batchpk.size() > 0 && teacherPk != null && teacherPk.size() > 0) {

			String query = "Select ts.primary_key,ts.date,ts.day,ts.end_time,ts.start_time,ts.status,CONCAT(ts.day,'_',ts.date)"
					+ " as timeslotkey,CONCAT(ts.start_time,'-',ts.end_time)as timeslot, ta.primary_key as allocationpk,  "
					+ " ta.eoroom_primary_key,ta.eoteacheruser_primary_key,m.room_id, ta.eostudentuser_primary_key,su.student_id,"
					+ " CONCAT(su.first_name, ' ', su.last_name) as studentfullname, m.room_name,tu.teacher_id,ta.student_cancellation,ta.teacher_cancellation,ta.admin_cancellation,"
					+ " CONCAT(tu.first_name, ' ', tu.last_name) as teacherfullname,b.batch_name,b.batch_id,ta.eobatch_primary_key,"
					+ " ta.eotimeslot_primary_key,ta.status as allocationstatus FROM eotimeslot ts LEFT JOIN eotime_slot_allocation ta "
					+ " on ts.primary_key = ta.eotimeslot_primary_key AND ta.eoteacheruser_primary_key in "
					+ teacherPkString + " LEFT JOIN eobatch b on b.primary_key = ta.eobatch_primary_key "
					+ " LEFT JOIN eomusic_room m on m.primary_key = ta.eoroom_primary_key"
					+ " LEFT JOIN eostudent_user su on su.primary_key = ta.eostudentuser_primary_key"
					+ " LEFT JOIN eoteacher_user tu on tu.primary_key = ta.eoteacheruser_primary_key"
					+ " Where ts.date BETWEEN '" + dateFrom + "' AND '" + dateTo
					+ "' AND ta.eoteacheruser_primary_key in" + teacherPkString
					+ " AND CASE WHEN ta.eobatch_primary_key in" + batchPkString + " THEN ta.eobatch_primary_key in"
					+ batchPkString + " OR ta.status = 'Requested' ELSE ta.status = 'Requested' END   ORDER BY ts.date";

			List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(query);

			Map<Object, Map<Object, HashMap<String, Object>>> respMap = new HashMap<>();

			for (Object obj : dataList) {
				HashMap<String, Object> innerMap = (HashMap<String, Object>) obj;
				Map<Object, HashMap<String, Object>> mainMap = respMap.get(innerMap.get("timeslotkey"));

				if (mainMap == null) {
					mainMap = new HashMap<>();
					HashMap<String, Object> detailMap = new HashMap<>();
					detailMap.put("primaryKey", innerMap.get("primary_key"));
					detailMap.put("status", innerMap.get("status"));
					detailMap.put("date", innerMap.get("date"));
					detailMap.put("day", innerMap.get("day"));
					detailMap.put("endTime", innerMap.get("end_time"));
					detailMap.put("startTime", innerMap.get("start_time"));
					detailMap.put("status", innerMap.get("status"));

					List<HashMap<String, Object>> teacherList = new ArrayList<>();
					if (innerMap.get("allocationpk") != null) {
						HashMap<String, Object> teacherMap = new HashMap<>();
						teacherMap.put("primaryKey", innerMap.get("allocationpk"));
						teacherMap.put("status", innerMap.get("allocationstatus"));
						teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
						teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
						teacherMap.put("teacherId", innerMap.get("teacher_id"));
						teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
						teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
						teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
						teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));

						if (innerMap.get("eostudentuser_primary_key") != null) {
							teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
							teacherMap.put("studentId", innerMap.get("student_id"));
							teacherMap.put("studentFullName", innerMap.get("studentfullname"));
						}
						if (innerMap.get("eoroom_primary_key") != null) {
							teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
							teacherMap.put("roomId", innerMap.get("room_id"));
							teacherMap.put("roomName", innerMap.get("room_name"));
						}
						if (innerMap.get("eobatch_primary_key") != null) {
							teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
							teacherMap.put("batchId", innerMap.get("batch_id"));
							teacherMap.put("batchName", innerMap.get("batch_name"));
						}
						teacherList.add(teacherMap);
						detailMap.put("teacherList", teacherList);
					}
					mainMap.put(innerMap.get("timeslot"), detailMap);
					respMap.put(innerMap.get("timeslotkey"), mainMap);
				} else {
					if (respMap.containsKey(innerMap.get("timeslotkey"))) {
						Map<Object, HashMap<String, Object>> tempDetailMap = (Map<Object, HashMap<String, Object>>) respMap
								.get(innerMap.get("timeslotkey"));

						// If timeslot is present in result map
						if (tempDetailMap.containsKey(innerMap.get("timeslot"))) {
							HashMap<String, Object> slotMap = (HashMap<String, Object>) tempDetailMap
									.get(innerMap.get("timeslot"));
							if (innerMap.get("allocationpk") != null) {
								List<HashMap<String, Object>> teacherList = new ArrayList<>();
								if (slotMap.get("teacherList") != null) {
									teacherList = (List<HashMap<String, Object>>) slotMap.get("teacherList");
									HashMap<String, Object> teacherMap = new HashMap<>();
									teacherMap.put("primaryKey", innerMap.get("allocationpk"));
									teacherMap.put("status", innerMap.get("allocationstatus"));
									teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
									teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
									teacherMap.put("teacherId", innerMap.get("teacher_id"));
									teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
									teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
									teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
									teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));

									if (innerMap.get("eostudentuser_primary_key") != null) {
										teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
										teacherMap.put("studentId", innerMap.get("student_id"));
										teacherMap.put("studentFullName", innerMap.get("studentfullname"));
									}
									if (innerMap.get("eoroom_primary_key") != null) {
										teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
										teacherMap.put("roomId", innerMap.get("room_id"));
										teacherMap.put("roomName", innerMap.get("room_name"));
									}
									if (innerMap.get("eobatch_primary_key") != null) {
										teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
										teacherMap.put("batchId", innerMap.get("batch_id"));
										teacherMap.put("batchName", innerMap.get("batch_name"));
									}
									teacherList.add(teacherMap);

								} else {
									HashMap<String, Object> teacherMap = new HashMap<>();
									teacherMap.put("primaryKey", innerMap.get("allocationpk"));
									teacherMap.put("status", innerMap.get("allocationstatus"));
									teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
									teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
									teacherMap.put("teacherId", innerMap.get("teacher_id"));
									teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
									teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
									teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
									teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));

									if (innerMap.get("eostudentuser_primary_key") != null) {
										teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
										teacherMap.put("studentId", innerMap.get("student_id"));
										teacherMap.put("studentFullName", innerMap.get("studentfullname"));
									}
									if (innerMap.get("eoroom_primary_key") != null) {
										teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
										teacherMap.put("roomId", innerMap.get("room_id"));
										teacherMap.put("roomName", innerMap.get("room_name"));
									}
									if (innerMap.get("eobatch_primary_key") != null) {
										teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
										teacherMap.put("batchId", innerMap.get("batch_id"));
										teacherMap.put("batchName", innerMap.get("batch_name"));
									}
									teacherList.add(teacherMap);
								}
								slotMap.put("teacherList", teacherList);
							}
							tempDetailMap.put(innerMap.get("timeslot"), slotMap);
						} else {
							// if timeslot is not present in resultmap

							HashMap<String, Object> detailMap = new HashMap<>();
							detailMap.put("primaryKey", innerMap.get("primary_key"));
							detailMap.put("status", innerMap.get("status"));
							detailMap.put("date", innerMap.get("date"));
							detailMap.put("day", innerMap.get("day"));
							detailMap.put("endTime", innerMap.get("end_time"));
							detailMap.put("startTime", innerMap.get("start_time"));
							detailMap.put("status", innerMap.get("status"));

							List<HashMap<String, Object>> teacherList = new ArrayList<>();
							if (innerMap.get("allocationpk") != null) {
								HashMap<String, Object> teacherMap = new HashMap<>();
								teacherMap.put("primaryKey", innerMap.get("allocationpk"));
								teacherMap.put("status", innerMap.get("allocationstatus"));
								teacherMap.put("eoTeacherUser", innerMap.get("eoteacheruser_primary_key"));
								teacherMap.put("eoTimeSlot", innerMap.get("eotimeslot_primary_key"));
								teacherMap.put("teacherId", innerMap.get("teacher_id"));
								teacherMap.put("teacherFullName", innerMap.get("teacherfullname"));
								teacherMap.put("studentCancellation", innerMap.get("student_cancellation"));
								teacherMap.put("teacherCancellation", innerMap.get("teacher_cancellation"));
								teacherMap.put("adminCancellation", innerMap.get("admin_cancellation"));

								if (innerMap.get("eostudentuser_primary_key") != null) {
									teacherMap.put("eoStudentUser", innerMap.get("eostudentuser_primary_key"));
									teacherMap.put("studentId", innerMap.get("student_id"));
									teacherMap.put("studentFullName", innerMap.get("studentfullname"));
								}
								if (innerMap.get("eoroom_primary_key") != null) {
									teacherMap.put("eoRoom", innerMap.get("eoroom_primary_key"));
									teacherMap.put("roomId", innerMap.get("room_id"));
									teacherMap.put("roomName", innerMap.get("room_name"));
								}
								if (innerMap.get("eobatch_primary_key") != null) {
									teacherMap.put("eoBatch", innerMap.get("eobatch_primary_key"));
									teacherMap.put("batchId", innerMap.get("batch_id"));
									teacherMap.put("batchName", innerMap.get("batch_name"));
								}
								teacherList.add(teacherMap);
								detailMap.put("teacherList", teacherList);
							}
							tempDetailMap.put(innerMap.get("timeslot"), detailMap);

						}
						respMap.put(innerMap.get("timeslotkey"), tempDetailMap);
					}
				}
			}
			return Response.status(201).entity(JSONUtil.objectToJson(respMap)).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson("no data")).build();
		}

	}

	@POST
	@Path("/getTimeSlotCount")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTimeSlotCount(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String dateTo = (String) map.get("dateTo");
		String dateFrom = (String) map.get("dateFrom");

		String str = "SELECT primary_key FROM eotimeslot WHERE date BETWEEN '" + dateFrom + "' AND '" + dateTo + "'";
		List<HashMap<String, Object>> timeSlotPkList = DBServices.getNativeQueryResult(str);

		List<HashMap<String, Object>> countList = new ArrayList<>();
		if (timeSlotPkList != null && timeSlotPkList.size() > 0) {
			String pkStr = "(";
			int count = 0;
			for (HashMap<String, Object> obj : timeSlotPkList) {
				pkStr += obj.get("primary_key") + "";

				if (count != (timeSlotPkList.size() - 1)) {
					pkStr += ",";
				}
				count++;
			}
			pkStr += ")";

			String qry = "SELECT eotimeslot_primary_key, COUNT(eotimeslot_primary_key) AS count FROM  eotime_slot_allocation "
					+ "WHERE status = 'Allocated' " + " AND eotimeslot_primary_key in " + pkStr
					+ " GROUP BY eotimeslot_primary_key HAVING COUNT(eotimeslot_primary_key) >= 1 "
					+ " ORDER BY eotimeslot_primary_key ASC";

			countList = DBServices.getNativeQueryResult(qry);

			return Response.status(201).entity(JSONUtil.objectToJson(countList)).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson(countList)).build();
		}

	}

	@POST
	@Path("/getBatchByStudentPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBatchByStudentPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		HashMap<String, Object> respMap = new HashMap<>();

		String stdPk = (String) map.get("userKey");
		String str = " SELECT sb.primary_key, sb.batch_type,students_pk, sb.eobatch_primary_key, c.lkmusictype_primary_key,lc.duration,"
				+ " b.batch_name, b.batch_id, lk.music_type,c.lkclassduration_primary_key,c.course_name FROM eostudent_batch sb "
				+ " LEFT JOIN eobatch b ON b.primary_key = sb.eobatch_primary_key "
				+ " LEFT JOIN eocourses c ON c.primary_key = b.eocourses_primary_key"
				+ " LEFT JOIN lkmusic_type lk ON lk.primary_key = c.lkmusictype_primary_key"
				+ " LEFT JOIN lkclass_duration lc ON lc.primary_key = c.lkclassduration_primary_key"
				+ " WHERE sb.is_active = true AND sb.batch_type = 'Individual'";
		List<HashMap<String, Object>> studentBatchList = DBServices.getNativeQueryResult(str);

		List<HashMap<String, Object>> finalList = new ArrayList<>();
		for (HashMap<String, Object> obj : studentBatchList) {
			List<String> pkList = (List<String>) JSONUtil.jsonToObject((String) obj.get("students_pk"), List.class);
			for (int k = 0; k < pkList.size(); k++) {
				if (pkList.get(k).equalsIgnoreCase(stdPk)) {
					HashMap<String, Object> innerMap = new HashMap<>();
					innerMap.put("batchName", obj.get("batch_name"));
					innerMap.put("batchId", obj.get("batch_id"));
					innerMap.put("batchType", obj.get("batch_type"));
					innerMap.put("eoBatch", obj.get("eobatch_primary_key"));
					innerMap.put("musicType", obj.get("music_type"));
					innerMap.put("courseName", obj.get("course_name"));
					innerMap.put("sessionDuration", obj.get("duration"));
					innerMap.put("lkMusicTypePrimaryKey", obj.get("lkmusictype_primary_key"));
					finalList.add(innerMap);
					break;
				}
			}
		}

		respMap.put("Individual", finalList);

		String strQuery = " SELECT sb.primary_key, sb.batch_type,students_pk, sb.eobatch_primary_key, c.lkmusictype_primary_key,"
				+ " b.batch_name, b.batch_id, lk.music_type,c.course_name,c.lkclassduration_primary_key,lc.duration FROM eostudent_batch sb "
				+ " LEFT JOIN eobatch b ON b.primary_key = sb.eobatch_primary_key "
				+ " LEFT JOIN eocourses c ON c.primary_key = b.eocourses_primary_key"
				+ " LEFT JOIN lkmusic_type lk ON lk.primary_key = c.lkmusictype_primary_key"
				+ " LEFT JOIN lkclass_duration lc ON lc.primary_key = c.lkclassduration_primary_key"
				+ " WHERE sb.is_active = true AND sb.batch_type = 'Group'";
		List<HashMap<String, Object>> studentBatchListGroup = DBServices.getNativeQueryResult(strQuery);

		List<HashMap<String, Object>> groupList = new ArrayList<>();
		for (HashMap<String, Object> obj : studentBatchListGroup) {
			List<String> pkList = (List<String>) JSONUtil.jsonToObject((String) obj.get("students_pk"), List.class);
			for (int k = 0; k < pkList.size(); k++) {
				if (pkList.get(k).equalsIgnoreCase(stdPk)) {
					HashMap<String, Object> innerMap = new HashMap<>();
					innerMap.put("batchName", obj.get("batch_name"));
					innerMap.put("batchId", obj.get("batch_id"));
					innerMap.put("batchType", obj.get("batch_type"));
					innerMap.put("eoBatch", obj.get("eobatch_primary_key"));
					innerMap.put("musicType", obj.get("music_type"));
					innerMap.put("courseName", obj.get("course_name"));
					innerMap.put("sessionDuration", obj.get("duration"));
					innerMap.put("lkMusicTypePrimaryKey", obj.get("lkmusictype_primary_key"));
					groupList.add(innerMap);
					break;
				}
			}
		}
		respMap.put("Group", groupList);

		return Response.status(201).entity(JSONUtil.objectToJson(respMap)).build();
	}

	@POST
	@Path("/getStudentBatchByTecherPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentBatchByTecherPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String teacherPk = (String) map.get("eoTeacherUser");
		String str = "SELECT primary_key, teacher_id, course_array, music_category_pk  FROM eoteacher_user"
				+ " WHERE is_active = true AND primary_key = " + teacherPk;
		List<HashMap<String, Object>> teacher = DBServices.getNativeQueryResult(str);

		List<HashMap<String, Object>> finalList = new ArrayList<>();

		if (teacher != null && teacher.size() > 0) {
			List<String> pkList = new ArrayList<>();
			List<String> coursePkList = new ArrayList<>();
			if (teacher.get(0).get("course_array") != null) {
				if (!teacher.get(0).get("course_array").equals("")) {
					coursePkList = (List<String>) JSONUtil.jsonToObject((String) teacher.get(0).get("course_array"),
							List.class);
				}
			}
			if (teacher.get(0).get("music_category_pk") != null) {
				if (!teacher.get(0).get("music_category_pk").equals("")) {
					pkList = (List<String>) JSONUtil.jsonToObject((String) teacher.get(0).get("music_category_pk"),
							List.class);
				}
			}
			
			String coursePkString = "(";
			int count3 = 0;
			for (int k = 0; k < coursePkList.size(); k++) {
				count3++;
				coursePkString += coursePkList.get(k);
				if (count3 < (coursePkList.size())) {
					coursePkString += ",";
				}
			}
			coursePkString += ")";
			
			String musicPkString = "(";
			String categoryPkString = "(";
			Set<String> musicPkSet = new HashSet<String>();
			Set<String> categoryPkSet = new HashSet<String>();
			for (int k = 0; k < pkList.size(); k++) {
				String[] arrstr = pkList.get(k).split("_");
				musicPkSet.add(arrstr[0]);
				categoryPkSet.add(arrstr[1]);
			}
			int count = 0;
			for (String strValue : musicPkSet) {
				count++;
				musicPkString += strValue;
				if (count < (musicPkSet.size())) {
					musicPkString += ",";
				}
			}
			musicPkString += ")";

			int count2 = 0;
			for (String strValue : categoryPkSet) {
				count2++;
				categoryPkString += strValue;
				if (count2 < (categoryPkSet.size())) {
					categoryPkString += ",";
				}
			}
			categoryPkString += ")";

			if (pkList != null && pkList.size() > 0) {
				/*String qry = "SELECT sb.primary_key,sb.batch_type,sb.eobatch_primary_key,b.batch_name,b.batch_id,su.student_id,"
						+ " b.eocourses_primary_key,b.is_active,c.is_active as courseactive,c.course_name, "
						+ " c.lkcategorytype_primary_key,c.lkmusictype_primary_key,lk.music_type, cat.category_type,"
						+ " CONCAT (su.first_name , ' ' , last_name) as studfullname, su.primary_key as studentpk FROM eostudent_batch sb "
						+ " LEFT JOIN eobatch b ON sb.eobatch_primary_key = b.primary_key"
						+ " LEFT JOIN eocourses c ON b.eocourses_primary_key = c.primary_key"
						+ " LEFT JOIN lkmusic_type lk ON c.lkmusictype_primary_key = lk.primary_key"
						+ " LEFT JOIN lkcategory_type cat ON c.lkcategorytype_primary_key = cat.primary_key"
						+ " LEFT JOIN eostudent_batch_mapping bm ON bm.eostudentbatch_primary_key = sb.primary_key"
						+ " LEFT JOIN eostudent_user su ON CAST(su.primary_key as varchar) = bm.student_pk"
						+ " WHERE c.lkmusictype_primary_key IN " + musicPkString
						+ " AND su.is_active = true AND su.is_visible = true "
						+ " AND b.is_active = true AND bm.is_active = true" + " AND c.lkcategorytype_primary_key IN"
						+ categoryPkString;*/
				String qry = "SELECT sb.primary_key,sb.batch_type,sb.eobatch_primary_key,b.batch_name,b.batch_id,su.student_id,"
						+ " b.eocourses_primary_key,b.is_active,c.is_active as courseactive,c.course_name, "
						+ " c.lkcategorytype_primary_key,c.lkmusictype_primary_key,lk.music_type, cat.category_type,"
						+ " CONCAT (su.first_name , ' ' , last_name) as studfullname, su.primary_key as studentpk FROM eostudent_batch sb "
						+ " LEFT JOIN eobatch b ON sb.eobatch_primary_key = b.primary_key"
						+ " LEFT JOIN eocourses c ON b.eocourses_primary_key = c.primary_key"
						+ " LEFT JOIN lkmusic_type lk ON c.lkmusictype_primary_key = lk.primary_key"
						+ " LEFT JOIN lkcategory_type cat ON c.lkcategorytype_primary_key = cat.primary_key"
						+ " LEFT JOIN eostudent_batch_mapping bm ON bm.eostudentbatch_primary_key = sb.primary_key"
						+ " LEFT JOIN eostudent_user su ON CAST(su.primary_key as varchar) = bm.student_pk"
						+ " WHERE su.is_active = true AND su.is_visible = true "
						+ " AND b.is_active = true AND bm.is_active = true" 
						+ " AND b.eocourses_primary_key IN "+ coursePkString;
				//System.out.println("qry::"+qry);
				List<HashMap<String, Object>> batchList = DBServices.getNativeQueryResult(qry);
				if (batchList != null && batchList.size() > 0) {
					finalList.addAll(batchList);
				}
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}

	@POST
	@Path("/getTimeSlotDetailsForMobileStd")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTimeSlotDetailsForMobile(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String eoStudentUser = (String) map.get("eoStudentUser");
		String dateFrom = (String) map.get("dateFrom");
		String dateTo = (String) map.get("dateTo");

		String str = "SELECT sb.batch_type, sb.eobatch_primary_key, bm.student_pk, bm.batch_pk , bm.is_active, "
				+ " bm.eostudentbatch_primary_key FROM eostudent_batch sb LEFT JOIN eostudent_batch_mapping bm"
				+ " ON bm.eostudentbatch_primary_key  = sb.primary_key WHERE bm.student_pk ='" + eoStudentUser + "'"
				+ " AND bm.is_active = true";

		List<HashMap<String, Object>> batchList = DBServices.getNativeQueryResult(str);
		if (batchList != null && batchList.size() > 0) {

			String batchPkString = "(";
			for (int k = 0; k < batchList.size(); k++) {
				batchPkString += (String) batchList.get(k).get("batch_pk");
				if (k != batchList.size() - 1) {
					batchPkString += ",";
				}
			}
			batchPkString += ")";

			/*
			 * String qry =
			 * "SELECT ed.start_time, ed.end_time , ed.eoteacheruser_primary_key, ed.date, ed.eoroom_primary_key,"
			 * +" CONCAT(tu.first_name,' ' ,tu.last_name) as teacherfullname, m.room_name, m.room_id "
			 * +" FROM eodefined_slot ed LEFT JOIN eoteacher_user tu ON tu.primary_key = ed.eoteacheruser_primary_key "
			 * +" LEFT JOIN eomusic_room m ON m.primary_key = ed.eoroom_primary_key"
			 * +" WHERE ed.eobatch_primary_key in "+batchPkString+" AND ed.date " +
			 * "BETWEEN '"+dateFrom+"' AND '"+dateTo+"'";
			 */

			/*
			 * String qry =
			 * "SELECT ed.start_time, ed.end_time , ed.eoteacheruser_primary_key, ed.date, ed.eoroom_primary_key,"
			 * +" CONCAT(tu.first_name,' ' ,tu.last_name) as teacherfullname, m.room_name, m.room_id "
			 * +" FROM eodefined_slot ed LEFT JOIN eoteacher_user tu ON tu.primary_key = ed.eoteacheruser_primary_key "
			 * +" LEFT JOIN eomusic_room m ON m.primary_key = ed.eoroom_primary_key"
			 * +" WHERE ed.eobatch_primary_key in "+batchPkString+" AND ed.date < '"
			 * +dateTo+"' ORDER BY ed.date DESC";
			 */
			String qry = "SELECT ed.start_time, ed.end_time , ed.eoteacheruser_primary_key, ed.date, ed.eoroom_primary_key,"
					+ " CONCAT(tu.first_name,' ' ,tu.last_name) as teacherfullname, m.room_name, m.room_id "
					+ " FROM eodefined_slot ed LEFT JOIN eoteacher_user tu ON tu.primary_key = ed.eoteacheruser_primary_key "
					+ " LEFT JOIN eomusic_room m ON m.primary_key = ed.eoroom_primary_key"
					+ " WHERE ed.eobatch_primary_key in " + batchPkString
					+ " AND ed.date >= '2020-01-01' ORDER BY ed.date DESC";

			List<HashMap<String, Object>> timeslotList = DBServices.getNativeQueryResult(qry);

			HashMap<String, Object> respMap = new HashMap<>();

			/*
			 * if(timeslotList != null && timeslotList.size() > 0 ){ for(HashMap<String,
			 * Object> obj : timeslotList){ List<HashMap<String, Object>> dataList =
			 * (List<HashMap<String, Object>>) respMap.get(obj.get("date"));
			 * 
			 * if(dataList == null){ dataList = new ArrayList<>(); HashMap<String, Object>
			 * innerMap = new HashMap<>();
			 * 
			 * innerMap.put("startTime", obj.get("start_time")); innerMap.put("endTime",
			 * obj.get("end_time")); innerMap.put("eoTeacheruser",
			 * obj.get("eoteacheruser_primary_key")); innerMap.put("date", obj.get("date"));
			 * innerMap.put("eoroom", obj.get("eoroom_primary_key"));
			 * innerMap.put("teacherfullname", obj.get("teacherfullname"));
			 * innerMap.put("roomname", obj.get("room_name")); innerMap.put("roomId",
			 * obj.get("room_id"));
			 * 
			 * dataList.add(innerMap); respMap.put((String)obj.get("date"), dataList); }
			 * else{ HashMap<String, Object> tempMap = new HashMap<>();
			 * 
			 * tempMap.put("startTime", obj.get("start_time")); tempMap.put("endTime",
			 * obj.get("end_time")); tempMap.put("eoTeacheruser",
			 * obj.get("eoteacheruser_primary_key")); tempMap.put("date", obj.get("date"));
			 * tempMap.put("eoroom", obj.get("eoroom_primary_key"));
			 * tempMap.put("teacherfullname", obj.get("teacherfullname"));
			 * tempMap.put("roomname", obj.get("room_name")); tempMap.put("roomId",
			 * obj.get("room_id"));
			 * 
			 * dataList.add(tempMap); } } }
			 */
			return Response.status(201).entity(JSONUtil.objectToJson(timeslotList)).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson("no data")).build();
		}
	}

	@POST
	@Path("/getTimeSlotDetailsForMobileTech")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTimeSlotDetailsForMobileTech(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String eoTeacherUser = (String) map.get("eoTeacherUser");
		String dateFrom = (String) map.get("dateFrom");
		String dateTo = (String) map.get("dateTo");

		String qry = " SELECT ed.start_time, ed.end_time , ed.eoteacheruser_primary_key, ed.date,"
				+ " CONCAT(s.first_name , ' ' , s.last_name) AS studentfullname, ed.eoroom_primary_key, "
				+ " b.batch_name, b.batch_id, m.room_name, m.room_id ,ed.eostudentuser_primary_key"
				+ " FROM eodefined_slot ed LEFT JOIN eobatch b ON b.primary_key = ed.eobatch_primary_key "
				+ " LEFT JOIN eomusic_room m ON m.primary_key = ed.eoroom_primary_key"
				+ " LEFT JOIN eostudent_user s ON s.primary_key = ed.eostudentuser_primary_key"
				+ " WHERE ed.eoteacheruser_primary_key =" + eoTeacherUser + " AND ed.date " + " BETWEEN '" + dateFrom
				+ "' AND '" + dateTo + "' ORDER BY ed.date DESC";

		List<HashMap<String, Object>> timeslotList = DBServices.getNativeQueryResult(qry);

		HashMap<String, Object> respMap = new HashMap<>();

		if (timeslotList != null && timeslotList.size() > 0) {
			/*
			 * for(HashMap<String, Object> obj : timeslotList){ List<HashMap<String,
			 * Object>> dataList = (List<HashMap<String, Object>>)
			 * respMap.get(obj.get("date"));
			 * 
			 * if(dataList == null){ dataList = new ArrayList<>(); HashMap<String, Object>
			 * innerMap = new HashMap<>();
			 * 
			 * innerMap.put("startTime", obj.get("start_time")); innerMap.put("endTime",
			 * obj.get("end_time")); innerMap.put("eoTeacheruser",
			 * obj.get("eoteacheruser_primary_key")); innerMap.put("date", obj.get("date"));
			 * innerMap.put("eoroom", obj.get("eoroom_primary_key"));
			 * innerMap.put("batchName", obj.get("batch_name")); innerMap.put("batchId",
			 * obj.get("batch_id")); innerMap.put("roomname", obj.get("room_name"));
			 * innerMap.put("roomId", obj.get("room_id"));
			 * 
			 * dataList.add(innerMap); respMap.put((String)obj.get("date"), dataList); }
			 * else{ HashMap<String, Object> tempMap = new HashMap<>();
			 * 
			 * tempMap.put("startTime", obj.get("start_time")); tempMap.put("endTime",
			 * obj.get("end_time")); tempMap.put("eoTeacheruser",
			 * obj.get("eoteacheruser_primary_key")); tempMap.put("date", obj.get("date"));
			 * tempMap.put("eoroom", obj.get("eoroom_primary_key"));
			 * tempMap.put("batchName", obj.get("batch_name")); tempMap.put("batchId",
			 * obj.get("batch_id")); tempMap.put("roomname", obj.get("room_name"));
			 * tempMap.put("roomId", obj.get("room_id"));
			 * 
			 * dataList.add(tempMap); } }
			 */

			return Response.status(201).entity(JSONUtil.objectToJson(timeslotList)).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson("no data")).build();
		}
	}

	@POST
	@Path("/sendEmailOnNewSlotCreation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendEmailOnNewSlotCreation(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<HashMap<String, Object>> dateList = (List<HashMap<String, Object>>) map.get("dateList");

		String body = "<p>Time slot ";
		if (dateList != null && dateList.size() > 0) {
			if (dateList.size() == 1) {
				body += "of <b>" + dateList.get(0) + "</b>";
				System.out.println("dateString: ifff:" + body);
			} else {
				body += "from <b>" + dateList.get(0) + "</b>" + " to <b>" + dateList.get(dateList.size() - 1) + "</b>";
			}
		}
		body += " has been created. Please mark your availabilty.";

		String qry = "SELECT email FROM eoteacher_user WHERE is_active = true";
		List<HashMap<String, Object>> emailList = DBServices.getNativeQueryResult(qry);

		List<String> finalMailList = new ArrayList<>();
		for (HashMap<String, Object> obj : emailList) {
			if (obj.get("email") != null || !((String) obj.get("email")).equalsIgnoreCase("")) {
				finalMailList.add((String) obj.get("email"));
			}
		}
		String subjectLine = "Time slot created";
		// email
		if (VSPLUtil.properties.getProperty(VSPLUtil.send_email).trim().equalsIgnoreCase("y")) {
			SlotCreationMail.composeMail(body, subjectLine, finalMailList);
		}

		return Response.status(201).entity(JSONUtil.objectToJson("no data")).build();
	}

	@POST
	@Path("/getSlotLengthForCancellation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSlotLengthForCancellation(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String eoStudentUser = (String) map.get("eoStudentUser");
		String eoTeacherUser = (String) map.get("eoTeacherUser");
		String lkMusicType = (String) map.get("lkMusicType");
		String fees = (String) map.get("fees");
		String duration = (String) map.get("duration");

		String str = "SELECT primary_key, date, day, end_time, slot_pk, is_active,month, start_time, "
				+ " year, eostudentuser_primary_key, eoteacheruser_primary_key, lkmusictype_primary_key"
				+ " FROM public.eodefined_slot WHERE eostudentuser_primary_key = " + eoStudentUser
				+ "AND eoteacheruser_primary_key = " + eoTeacherUser + " AND lkmusictype_primary_key = " + lkMusicType;

		List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(str);

		if (dataList != null && dataList.size() > 0) {
			double session = 0;
			double totalMins = 0;
			double amount = 0;
			String sessionDurationArr[] = (duration.split(" "));
			double sessionDuration = Double.parseDouble(sessionDurationArr[0]);
			for (HashMap<String, Object> obj : dataList) {
				List<String> slotArray = (List<String>) JSONUtil.jsonToObject((String) obj.get("slot_pk"), List.class);
				totalMins = 15 * slotArray.size();
				session = totalMins / sessionDuration;
				amount = session * Double.valueOf(fees);
			}
			return Response.status(201).entity(JSONUtil.objectToJson(Math.round(amount))).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson("no data")).build();
		}
	}

	@POST
	@Path("/checkCourseDeletionForStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkCourseDeletionForStudent(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String eoStudentUser = (String) map.get("studentPk");
		String categoryPk = (String) map.get("categoryPk");
		String musicPk = (String) map.get("musicPk");

		String str = "SELECT primary_key, is_teacher_paid, status,eostudentuser_primary_key, eoteacheruser_primary_key,"
				+ " eotimeslot_primary_key,lkmusictype_primary_key, lkcategorytype_primary_key"
				+ " FROM public.eotime_slot_allocation WHERE eostudentuser_primary_key = " + eoStudentUser + " AND "
				+ " lkmusictype_primary_key = " + musicPk + " AND lkcategorytype_primary_key = " + categoryPk
				+ " AND status != 'Cancelled'";
		System.out.println("str check::::" + str);
		List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(str);
		if (dataList != null && dataList.size() > 0) {
			return Response.status(201).entity(JSONUtil.objectToJson(true)).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson(false)).build();
		}

	}
}
