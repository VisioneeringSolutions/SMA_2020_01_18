package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.model.eo.EOMasterStudentCriteria;
import com.vspl.music.model.eo.EOMasterTeacherCriteria;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOStudentCriteria;
import com.vspl.music.model.eo.EOStudentRating;
import com.vspl.music.model.eo.EOTeacherCriteria;
import com.vspl.music.model.eo.EOTeacherRating;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.JSONUtil;

@Path("/ajaxFeedback")
public class FeedbackController {

	public static Logger logger = LoggerFactory.getLogger(FeedbackController.class);

	@POST
	@Path("/getBatchListForStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBatchListForStudent(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println(" getBatchListForStudent::" + JSONUtil.objectToJson(map));

		String studPk = (String) map.get("studPk");
		String str = "SELECT sb.primary_key, sb.batch_type,students_pk, sb.eobatch_primary_key, "
				+ " b.batch_name, b.batch_id "
				+ " FROM public.eostudent_batch sb LEFT JOIN eobatch b ON sb.eobatch_primary_key = b.primary_key "
				+ " WHERE sb.is_active = true ";
		System.out.println("str:" + str);
		List<HashMap<String, Object>> studentBatchList = DBServices.getNativeQueryResult(str);
		System.out.println("studentBatchList:" + studentBatchList);

		List<HashMap<String, Object>> finalList = new ArrayList<>();
		for (HashMap<String, Object> obj : studentBatchList) {
			List<String> pkList = (List<String>) JSONUtil.jsonToObject((String) obj.get("students_pk"), List.class);
			System.out.println("pkList:" + pkList);
			for (int k = 0; k < pkList.size(); k++) {
				System.out.println("pkList.get(k):" + pkList.get(k));
				System.out.println("studPk:" + studPk);
				System.out.println(pkList.get(k).equalsIgnoreCase(studPk));
				if (pkList.get(k).equalsIgnoreCase(studPk)) {
					System.out.println("trueeeee");
					HashMap<String, Object> innerMap = new HashMap<>();
					innerMap.put("batchName", obj.get("batch_name"));
					innerMap.put("batchId", obj.get("batch_id"));
					innerMap.put("eoBatch", obj.get("eobatch_primary_key"));
					finalList.add(innerMap);
					break;
				}
				System.out.println("finalList:" + finalList);
			}
		}

		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}

	@POST
	@Path("/getSlotForStudent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSlotForStudent(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println(" getSlotForStudent::" + JSONUtil.objectToJson(map));
		String studPk = (String) map.get("studPk");
		String batchPk = (String) map.get("batchPk");

		String str = "Select ds.primary_key, ds.date, ds.start_time, ds.end_time, ds.eobatch_primary_key,ds.eoteacheruser_primary_key,students_pk "
				+ "FROM eodefined_slot ds INNER JOIN eostudent_batch sb ON ds.eobatch_primary_key = sb.eobatch_primary_key "
				+ "WHERE ds.eobatch_primary_key='" + batchPk + "' ORDER BY ds.start_time,ds.date ";
		System.out.println("str:" + str);
		List<HashMap<String, Object>> studentSlotList = DBServices.getNativeQueryResult(str);
		System.out.println("studentSlotList:" + studentSlotList);

		List<HashMap<String, Object>> finalList = new ArrayList<>();
		for (HashMap<String, Object> obj : studentSlotList) {
			List<String> pkList = (List<String>) JSONUtil.jsonToObject((String) obj.get("students_pk"), List.class);
			System.out.println("pkList:" + pkList);
			for (int k = 0; k < pkList.size(); k++) {
				System.out.println("pkList.get(k):" + pkList.get(k));
				System.out.println("studPk:" + studPk);
				System.out.println(pkList.get(k).equalsIgnoreCase(studPk));
				if (pkList.get(k).equalsIgnoreCase(studPk)) {
					System.out.println("trueeeee");
					HashMap<String, Object> innerMap = new HashMap<>();
					innerMap.put("primaryKey", obj.get("primary_key"));
					innerMap.put("date", obj.get("date"));
					innerMap.put("startTime", obj.get("start_time"));
					innerMap.put("endTime", obj.get("end_time"));
					innerMap.put("eoBatch", obj.get("eobatch_primary_key"));
					innerMap.put("eoTeacherUser", obj.get("eoteacheruser_primary_key"));
					finalList.add(innerMap);
					break;
				}
				System.out.println("finalList:" + finalList);
			}
		}

		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}

	@POST
	@Path("/getTeacherList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTeacherList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println(" getSlotForStudent::" + JSONUtil.objectToJson(map));
		String slotPk = (String) map.get("slotPk");
		String batchPk = (String) map.get("batchPk");

		String str = "SELECT  ds.eoteacheruser_primary_key, CONCAT(tu.first_name, ' ',tu.last_name) as teacherfullname, ds.date,ds.month, ds.year  "
				+ " FROM eodefined_slot ds INNER JOIN eoteacher_user tu ON ds.eoteacheruser_primary_key = tu.primary_key  "
				+ " where eobatch_primary_key='" + batchPk + "' and ds.primary_key='" + slotPk + "' ";

		/*
		 * List<HashMap<String, Object>> teacherList =
		 * DBServices.getNativeQueryResult(str);
		 * System.out.println("teacherList:"+teacherList);
		 */
		List<HashMap<String, Object>> teacherList = new ArrayList<>();
		HashMap<String, Object> tempMap = DBServices.getNativeQueryResult(str).get(0);
		HashMap<String, Object> finalMap = new HashMap<>();
		finalMap.put("eoTeacherUser", tempMap.get("eoteacheruser_primary_key"));
		finalMap.put("date", tempMap.get("date"));
		finalMap.put("month", tempMap.get("month"));
		finalMap.put("year", tempMap.get("year"));
		finalMap.put("teacherfullname", tempMap.get("teacherfullname"));
		finalMap.put("batchPk", batchPk);
		finalMap.put("slotPk", slotPk);

		(teacherList).add(finalMap);
		System.out.println("teacherList::" + teacherList);

		return Response.status(201).entity(JSONUtil.objectToJson(teacherList)).build();
	}

	@POST
	@Path("/getAttrForRating")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAttrForRating(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String batchPk = (String) map.get("batchPk");
		String studPk = (String) map.get("studPk");
		String slotPk = (String) map.get("slotPk");
		String teacherPk = (String) map.get("teacherPk");
		List<HashMap<String, Object>> resultList = new ArrayList<>();

		String qry = "FROM EOTeacherRating where eoDefinedSlot = " + slotPk + " " + "and eoBatch.primaryKey = "
				+ batchPk + " and eoTeacherUser.primaryKey = " + teacherPk;
		/* + "and eoStudentUser.primaryKey = "+studPk; */
		System.out.println("qry:::::" + qry);

		List<Object> objList = DBServices.get(qry);
		if (objList != null && objList.size() > 0) {
			EOTeacherRating eoTeacherRating = (EOTeacherRating) objList.get(0);
			for (Object obj : eoTeacherRating.eoTeacherCriteria) {
				EOTeacherCriteria eoTeacherCriteria = (EOTeacherCriteria) obj;
				HashMap<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("primaryKey", eoTeacherCriteria.primaryKey);
				tmpMap.put("criteria", eoTeacherCriteria.eoMasterTeacherCriteria.criteria);
				tmpMap.put("maxRating", eoTeacherCriteria.eoMasterTeacherCriteria.maxRating);
				tmpMap.put("optedRating", Double.toString(
						eoTeacherCriteria.optedRating)); /*
															 * convert double to
															 * string
															 */
				resultList.add(tmpMap);
			}

		} else {
			qry = "FROM EOMasterTeacherCriteria where is_active = true";
			List<Object> criteriaList = DBServices.get(qry);
			if (criteriaList != null && criteriaList.size() > 0) {
				for (Object obj : criteriaList) {
					EOMasterTeacherCriteria eoMaster = (EOMasterTeacherCriteria) obj;
					HashMap<String, Object> tmpMap = new HashMap<>();
					tmpMap.put("eoMasterTeacherCriteria", eoMaster.primaryKey);
					tmpMap.put("criteria", eoMaster.criteria);
					tmpMap.put("maxRating", eoMaster.maxRating);

					resultList.add(tmpMap);
				}

			}

		}

		return Response.status(201).entity(JSONUtil.objectToJson(resultList)).build();
	}

	@POST
	@Path("/createRatingsForTeacher")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createRatingsForTeacher(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<HashMap<String, Object>> criteriaList = (List<HashMap<String, Object>>) map.get("criteriaList");
		HashMap<String, Object> ratingMap = (HashMap<String, Object>) map.get("ratingMap");

		DBServices.create(EOObject.createObject(ratingMap));
		EOTeacherRating eoTeacherRating = (EOTeacherRating) EOObject.getLatestObject("EOTeacherRating");

		for (HashMap<String, Object> criteriaMap : criteriaList) {
			criteriaMap.put("eoTeacherRating", eoTeacherRating.primaryKey);
			DBServices.create(EOObject.createObject(criteriaMap));
		}

		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}

}
