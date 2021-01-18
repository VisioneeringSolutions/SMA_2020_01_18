package com.vspl.music.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.services.DBServices;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.JSONUtil;

@Path("/ajaxDashboard")
public class DashboardController {
	public static Logger logger = LoggerFactory.getLogger(DashboardController.class);

	private static final DecimalFormat df2 = new DecimalFormat("#.##");

	@POST
	@Path("/getStudentTeacherCount")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTotalStudent(HashMap<String, String> map) {

		String className = map.get("className");
		String qryString = "SELECT count(primary_key) as count FROM " + className + " WHERE is_active = true";
		HashMap<String, Object> countMap = DBServices.getNativeQueryResult(qryString).get(0);

		return Response.status(200).entity(JSONUtil.objectToJson(countMap)).build();
	}

	@POST
	@Path("/getStudentRating")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentRating(HashMap<String, Object> map) {

		String[] monthArr = DateUtil.janToDecMonthArr;
		String year = map.get("year") != null ? (String) map.get("year") : DateUtil.currentYear() + "";
		HashMap<Object, Object> resultMap = new HashMap<>();
		int count = 0;
		for (String month : monthArr) {
			HashMap<String, Object> monthMap = new HashMap<>();
			if (month.equalsIgnoreCase(DateUtil.currMonth())) {
				monthMap = countStudentRating(month, year);
				resultMap.put(count++, monthMap);
				break;
			}
			monthMap = countStudentRating(month, year);
			resultMap.put(count++, monthMap);

		}

		return Response.status(200).entity(JSONUtil.objectToJson(resultMap)).build();
	}

	private HashMap<String, Object> countStudentRating(String month, String year) {

		String qryStr = "Select count(primary_key) as no_of_slot, SUM(r.avg_opted_rating) as rating FROM EOSTUDENT_RATING r where "
				+ " r.month = '" + month + "' and r.year = '" + year + "' GROUP BY r.eostudentuser_primary_key";

		List<HashMap<String, Object>> ratingList = DBServices.getNativeQueryResult(qryStr);

		HashMap<String, Object> returnMap = new HashMap<>();

		double maxRating = 0.0, minRating = 0.0, avgRating = 0.0, totalRating = 0.0, totalSlot = 0.0;
		if (ratingList != null && ratingList.size() > 0) {
			minRating = ((BigDecimal) ratingList.get(0).get("rating")).doubleValue();
			for (HashMap<String, Object> ratingObj : ratingList) {
				double noOfSlot = ((BigInteger) ratingObj.get("no_of_slot")).doubleValue();
				double rating = ((BigDecimal) ratingObj.get("rating")).doubleValue();
				if (maxRating < rating) {
					maxRating = rating;
				}
				if (minRating > rating) {
					minRating = rating;
				}
				totalSlot += noOfSlot;
				totalRating += rating * noOfSlot;
			}

		}

		if (totalRating != 0.0) {
			avgRating = Double.valueOf(df2.format(totalRating / totalSlot));
		}

		returnMap.put("maxRating", maxRating);
		returnMap.put("minRating", minRating);
		returnMap.put("avgRating", avgRating);
		returnMap.put("month", month);

		return returnMap;

	}

	@POST
	@Path("/getStudentRatingByPk")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentRatingByPk(HashMap<String, Object> map) {

		String studentPk = (String) map.get("studentPk");   //convert studentPk from int to string(nikita)

		String[] monthArr = DateUtil.janToDecMonthArr;
		String year = map.get("year") != null ? (String) map.get("year") : DateUtil.currentYear() + "";
		HashMap<Object, Object> resultMap = new HashMap<>();
		int count = 0;
		for (String month : monthArr) {
			HashMap<String, Object> monthMap = new HashMap<>();
			if (month.equalsIgnoreCase(DateUtil.currMonth())) {
				monthMap = countStudentByPkRating(month, year, studentPk);
				resultMap.put(count++, monthMap);
				break;
			}
			monthMap = countStudentByPkRating(month, year, studentPk);
			resultMap.put(count++, monthMap);

		}
		return Response.status(200).entity(JSONUtil.objectToJson(resultMap)).build();
	}

	private HashMap<String, Object> countStudentByPkRating(String month, String year, String studentPk) {   //convert studentPk from int to string(nikita)

		String qryStr = "Select SUM(r.avg_opted_rating) as rating FROM EOSTUDENT_RATING r where " + " r.month = '"
				+ month + "' and r.year = '" + year + "' and r.eostudentuser_primary_key = " + studentPk;

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
	@Path("/getTeacherRating")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeacherRating(HashMap<String, Object> map) {

		String[] monthArr = DateUtil.janToDecMonthArr;
		String year = map.get("year") != null ? (String) map.get("year") : DateUtil.currentYear() + "";
		HashMap<Object, Object> resultMap = new HashMap<>();
		int count = 0;
		for (String month : monthArr) {

			HashMap<String, Object> monthMap = new HashMap<>();
			if (month.equalsIgnoreCase(DateUtil.currMonth())) {
				monthMap = countTeacherRating(month, year);
				resultMap.put(count++, monthMap);
				break;
			}
			monthMap = countTeacherRating(month, year);
			resultMap.put(count++, monthMap);
			// }

		}

		return Response.status(200).entity(JSONUtil.objectToJson(resultMap)).build();
	}

	private HashMap<String, Object> countTeacherRating(String month, String year) {

		String qryStr = "Select count(primary_key) as no_of_student, SUM(r.avg_opted_rating) as rating FROM EOTEACHER_RATING r  where "
				+ " r.month = '" + month + "' and r.year = '" + year + "' GROUP BY r.eoteacheruser_primary_key";

		List<HashMap<String, Object>> ratingList = DBServices.getNativeQueryResult(qryStr);

		HashMap<String, Object> returnMap = new HashMap<>();

		double maxRating = 0.0, minRating = 0.0, avgRating = 0.0, totalRating = 0.0, totalStudentsWhoHasGivenRating = 0.0;
		if (ratingList != null && ratingList.size() > 0) {
			minRating = ((BigDecimal) ratingList.get(0).get("rating")).doubleValue();
			for (HashMap<String, Object> ratingObj : ratingList) {
				double noOfStudentWhoHasGivenRating = ((BigInteger) ratingObj.get("no_of_student")).doubleValue();
				double rating = ((BigDecimal) ratingObj.get("rating")).doubleValue();
				if (maxRating < rating) {
					maxRating = rating;
				}
				if (minRating > rating) {
					minRating = rating;
				}
				totalStudentsWhoHasGivenRating += noOfStudentWhoHasGivenRating;
				totalRating += rating * noOfStudentWhoHasGivenRating;
			}

		}

		if (totalRating != 0.0) {
			avgRating = Double.valueOf(df2.format(totalRating / totalStudentsWhoHasGivenRating));
		}

		returnMap.put("maxRating", maxRating);
		returnMap.put("minRating", minRating);
		returnMap.put("avgRating", avgRating);
		returnMap.put("month", month);

		return returnMap;

	}
	
	@POST
	@Path("/getTeacherRatingByPk")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeacherRatingByPk(HashMap<String, Object> map) {

		Integer teacherPk = (Integer) map.get("teacherPk");

		String[] monthArr = DateUtil.janToDecMonthArr;
		String year = map.get("year") != null ? (String) map.get("year") : DateUtil.currentYear() + "";
		HashMap<Object, Object> resultMap = new HashMap<>();
		int count = 0;
		for (String month : monthArr) {
			HashMap<String, Object> monthMap = new HashMap<>();
			if (month.equalsIgnoreCase(DateUtil.currMonth())) {
				monthMap = countTeacherByPkRating(month, year, teacherPk);
				resultMap.put(count++, monthMap);
				break;
			}
			monthMap = countTeacherByPkRating(month, year, teacherPk);
			resultMap.put(count++, monthMap);

		}
		return Response.status(200).entity(JSONUtil.objectToJson(resultMap)).build();
	}

	private HashMap<String, Object> countTeacherByPkRating(String month, String year, Integer teacherPk) {

		String qryStr = "Select SUM(r.avg_opted_rating) as rating FROM EOTEACHER_RATING r where " + " r.month = '"
				+ month + "' and r.year = '" + year + "' and r.eoteacheruser_primary_key = " + teacherPk;

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

}
