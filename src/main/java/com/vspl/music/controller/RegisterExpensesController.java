package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.model.eo.EOAccountType;
import com.vspl.music.model.eo.EOAddExpenses;
import com.vspl.music.model.eo.EOAddExpensesDetail;
import com.vspl.music.model.eo.EOLoginDetails;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOStudentUser;
import com.vspl.music.model.eo.EOSubAccount;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.JSONUtil;

@Path("/ajaxExpenses")
public class RegisterExpensesController {
	
	public static Logger logger = LoggerFactory.getLogger(RegisterExpensesController.class);
	@POST
	@Path("/createExpenses")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createExpenses(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		List<HashMap<String, Object>> dataList = (List<HashMap<String, Object>>) map.get("data");
		List<HashMap<String, Object>> removeList = (List<HashMap<String, Object>>) map.get("removeExpenseList");
		List<HashMap<String, Object>> removeSubExpenseList = (List<HashMap<String, Object>>) map.get("removeSubExpenseList");
		
		for (HashMap<String, Object> criteriaMap : dataList) {
			List<HashMap<String, Object>> subAccountList = (List<HashMap<String, Object>>) criteriaMap.get("subAccountList");
			criteriaMap.remove("subAccountList");
			if (criteriaMap.get("primaryKey") == null) {
				criteriaMap.put("createdDate", DateUtil.formatedCurrentDate());
				criteriaMap.put("className", "EOAccountType");
				DBServices.create(EOObject.createObject(criteriaMap));
				EOAccountType accountdetails = (EOAccountType) EOObject.getLatestObject("EOAccountType");
				if(subAccountList != null && subAccountList.size() > 0){
					for (HashMap<String, Object> subAccountMap : subAccountList) {
						HashMap<String, Object> innerMap = new HashMap<>();
						innerMap.put("accountType", subAccountMap.get("accountType"));
						innerMap.put("accountName", subAccountMap.get("accountName"));
						innerMap.put("createdDate", DateUtil.formatedCurrentDate());
						innerMap.put("descriptions", subAccountMap.get("descriptions"));
						innerMap.put("className", "EOSubAccount");
						innerMap.put("eoAccountType", accountdetails.primaryKey);
						DBServices.create(EOObject.createObject(innerMap));
					}
				}
				
			} else {
				criteriaMap.put("updatedDate", DateUtil.formatedCurrentDate());
				EOObject eoObject = EOObject.getObjectByPK("EOAccountType",criteriaMap.get("primaryKey")+"");
				DBServices.update(EOObject.updateObject(eoObject, criteriaMap));
				EOAccountType accountdetails = (EOAccountType) eoObject;
				if (subAccountList != null && subAccountList.size() > 0) {
					for (HashMap<String, Object> subAccountMap : subAccountList) {
						HashMap<String, Object> innerMap = new HashMap<>();
						innerMap.put("accountType", subAccountMap.get("accountType"));
						innerMap.put("accountName", subAccountMap.get("accountName"));
						innerMap.put("descriptions", subAccountMap.get("descriptions"));
						innerMap.put("className", "EOSubAccount");
						
						if (subAccountMap.get("primaryKey") == null) {
							//create subAccount
							innerMap.put("eoAccountType", accountdetails.primaryKey);
							innerMap.put("createdDate", DateUtil.formatedCurrentDate());
							DBServices.create(EOObject.createObject(innerMap));
						} else {
							//update suAccount
							subAccountMap.put("updatedDate", DateUtil.formatedCurrentDate());
							EOObject eoObject1 = EOObject.getObjectByPK("EOSubAccount",subAccountMap.get("primaryKey")+"");
							DBServices.update(EOObject.updateObject(eoObject1, subAccountMap));
						}
					}
				}
			}
		}
		
		if (removeList != null && removeList.size() > 0) {
			for (HashMap<String, Object> removeMap : removeList) {
				List<HashMap<String, Object>> tempList = (List<HashMap<String, Object>>) removeMap.get("subAccountList");
				if (removeMap.get("primaryKey") != null) {
					removeMap.put("updatedDate", DateUtil.formatedCurrentDate());
					removeMap.remove("subAccountList");
					EOObject eoObject3 = EOObject.getObjectByPK("EOAccountType",removeMap.get("primaryKey")+"");
					DBServices.update(EOObject.updateObject(eoObject3, removeMap));
					//List<HashMap<String, Object>> suList = (List<HashMap<String, Object>>) removeMap.get("subAccountList");
					if (tempList != null && tempList.size() > 0){
						for (HashMap<String, Object> subMap : tempList) {
							if (subMap.get("primaryKey") != null) {
								subMap.put("isActive", false);
								EOObject eoObject2 = EOObject.getObjectByPK("EOSubAccount",subMap.get("primaryKey")+"");
								DBServices.update(EOObject.updateObject(eoObject2, subMap));
							}
						}
					}
				}
			}
		}
		if (removeSubExpenseList != null && removeSubExpenseList.size() > 0) {
			for (HashMap<String, Object> removeMap : removeSubExpenseList) {
				if (removeMap.get("primaryKey") != null) {
					removeMap.put("updatedDate", DateUtil.formatedCurrentDate());
						EOObject eoObject2 = EOObject.getObjectByPK("EOSubAccount",removeMap.get("primaryKey")+"");
						DBServices.update(EOObject.updateObject(eoObject2, removeMap));
				}
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	@POST
	@Path("/getRegisterExpenses")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getRegisterExpenses(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Object> subAccountObjList = DBServices.get("From EOAccountType WHERE  is_active = true");
		List<HashMap<String, Object>> expenseList = new ArrayList<>();

		for (Object obj : subAccountObjList) {
			EOAccountType eoAccountType = (EOAccountType) obj;
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("primaryKey", eoAccountType.primaryKey);
			innerMap.put("accountName", eoAccountType.accountName);
			innerMap.put("accountType", eoAccountType.accountType);
			innerMap.put("descriptions", eoAccountType.descriptions);
			innerMap.put("isActive", eoAccountType.isActive);
		
			List<Object> expenseObjList = DBServices.get("From EOSubAccount WHERE  is_active = true AND eoaccounttype_primary_key=" + eoAccountType.primaryKey );
			List<HashMap<String,Object>> List = new ArrayList<>() ;
			if (expenseObjList != null && expenseObjList.size() > 0) {
				List<HashMap<String,Object>> tempList = new ArrayList<>() ;
				for (Object obje : expenseObjList) {
					EOSubAccount eoExpense = (EOSubAccount) obje;
					HashMap<String, Object> subAccountMap = new HashMap<>();
					subAccountMap.put("primaryKey", eoExpense.primaryKey);
					subAccountMap.put("accountName", eoExpense.accountName);
					subAccountMap.put("descriptions", eoExpense.descriptions);
					subAccountMap.put("isActive", eoExpense.isActive);
					tempList.add(subAccountMap);
					
					innerMap.put("subAccountList",tempList);
				}
			}else{
				innerMap.put("subAccountList",List);
			}
			expenseList.add(innerMap);
		}
		return Response.status(201).entity(JSONUtil.objectToJson(expenseList)).build();
	}
	
	@POST
	@Path("/getExpensesByMonth")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getExpensesByMonth(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Object> expenseListObj = DBServices
				.get("From EOAddExpenses WHERE is_active = true AND month ='"+map.get("month")+"' AND year='"+map.get("year")+"'");
		List<HashMap<String, Object>> expenseList = new ArrayList<>();
		List<HashMap<String, Object>> addExpenseList = new ArrayList<>();
		if(expenseListObj != null && expenseListObj.size() > 0){
			//data exists for selected month and year
			for (Object obj : expenseListObj) {
				EOAddExpenses eoAddExpenses = (EOAddExpenses) obj;
				HashMap<String, Object> innerMap = new HashMap<>();
				innerMap.put("primaryKey", eoAddExpenses.primaryKey);
				innerMap.put("accountName", eoAddExpenses.accountName);
				innerMap.put("accountType", eoAddExpenses.accountType);
				innerMap.put("descriptions", eoAddExpenses.descriptions);
				innerMap.put("expenseDate", eoAddExpenses.expenseDate);
				innerMap.put("amount", eoAddExpenses.amount);
				innerMap.put("isActive", eoAddExpenses.isActive);
			
				List<Object> subExpenseObjList = DBServices.
						get("From EOAddExpensesDetail WHERE  is_active = true AND eoaddexpenses_primary_key=" + eoAddExpenses.primaryKey );
				List<HashMap<String,Object>> List = new ArrayList<>() ;
				if (subExpenseObjList != null && subExpenseObjList.size() > 0) {
					List<HashMap<String,Object>> tempList = new ArrayList<>() ;
					for (Object obje : subExpenseObjList) {
						EOAddExpensesDetail eoAddExpensesDetail = (EOAddExpensesDetail) obje;
						HashMap<String, Object> subAccountMap = new HashMap<>();
						subAccountMap.put("primaryKey", eoAddExpensesDetail.primaryKey);
						subAccountMap.put("subAccountName", eoAddExpensesDetail.subAccountName);
						subAccountMap.put("descriptions", eoAddExpensesDetail.descriptions);
						subAccountMap.put("amount", eoAddExpensesDetail.amount);
						subAccountMap.put("expenseDate", eoAddExpensesDetail.expenseDate);
						subAccountMap.put("isActive", eoAddExpensesDetail.isActive);
						tempList.add(subAccountMap);
						
						innerMap.put("subAccountList",tempList);
					}
				}else{
					innerMap.put("subAccountList",List);
				}
				addExpenseList.add(innerMap);
			}
			
		}
		List<Object> accountTypeObj = DBServices.get("From EOAccountType WHERE  is_active = true");
		if(accountTypeObj != null && accountTypeObj.size() > 0){
			//no data for selected month then get attributes from EOAccountType and EOSubAccount
			
			for (Object obj : accountTypeObj) {
				EOAccountType eoAccountType = (EOAccountType) obj;
				HashMap<String, Object> innerMap = new HashMap<>();
				//innerMap.put("accountPk", eoAccountType.primaryKey);
				innerMap.put("accountName", eoAccountType.accountName);
				innerMap.put("accountType", eoAccountType.accountType);
				innerMap.put("descriptions", eoAccountType.descriptions);
				innerMap.put("isActive", eoAccountType.isActive);
			
				List<Object> expenseObjList = DBServices.
						get("From EOSubAccount WHERE  is_active = true AND eoaccounttype_primary_key=" + eoAccountType.primaryKey );
				List<HashMap<String,Object>> List = new ArrayList<>() ;
				if (expenseObjList != null && expenseObjList.size() > 0) {
					List<HashMap<String,Object>> tempList = new ArrayList<>() ;
					for (Object obje : expenseObjList) {
						EOSubAccount eoExpense = (EOSubAccount) obje;
						HashMap<String, Object> subAccountMap = new HashMap<>();
						//subAccountMap.put("subAccountPk", eoExpense.primaryKey);
						subAccountMap.put("subAccountName", eoExpense.accountName);
						subAccountMap.put("descriptions", eoExpense.descriptions);
						subAccountMap.put("isActive", eoExpense.isActive);
						//subAccountMap.put("eoAccountType", eoExpense.eoAccountType.primaryKey);
						tempList.add(subAccountMap);
						
						innerMap.put("subAccountList",tempList);
					}
				}else{
					innerMap.put("subAccountList",List);
				}
				expenseList.add(innerMap);
			}
		}
		
		HashMap<String, Object> finalMap = new HashMap<>();
		finalMap.put("expenseData", addExpenseList);
		finalMap.put("defaultData", expenseList);
		
		return Response.status(201).entity(JSONUtil.objectToJson(finalMap)).build();
	}
	
	@POST
	@Path("/createExpensesByMonth")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createExpensesByMonth(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<HashMap<String, Object>> dataList = (List<HashMap<String,Object>>)map.get("data");
		
		if(dataList != null && dataList.size() > 0){
			//data received from request 
			for(HashMap<String, Object> data : dataList){
				//create account expenses
				if(data.get("primaryKey") == null){
					List<HashMap<String, Object>> subAccountList = (List<HashMap<String,Object>>)data.get("subAccountList");
					data.remove("subAccountList");
					data.put("createdDate", DateUtil.formatedCurrentDate());
					data.put("className", "EOAddExpenses");
					DBServices.create(EOObject.createObject(data));
					EOAddExpenses eoAddExpenses = (EOAddExpenses)EOObject.getLatestObject("EOAddExpenses");
					if(subAccountList != null && subAccountList.size() > 0){
						//create subaccountExpenses
						for(HashMap<String, Object> obj : subAccountList){
							obj.put("createdDate", DateUtil.formatedCurrentDate());
							obj.put("className", "EOAddExpensesDetail");
							obj.put("eoAddExpenses", eoAddExpenses.primaryKey);
							DBServices.create(EOObject.createObject(obj));
						}
					}
				}else{
					//update account expenses
					List<HashMap<String, Object>> subAccountList = (List<HashMap<String,Object>>)data.get("subAccountList");
					EOObject eoObject = EOObject.getObjectByPK("EOAddExpenses",data.get("primaryKey")+"");
					data.remove("subAccountList");
					data.put("updatedDate", DateUtil.formatedCurrentDate());
					DBServices.update(EOObject.updateObject(eoObject, data));
					
					if(subAccountList != null && subAccountList.size() > 0){
						//update subaccountExpenses
						for(HashMap<String, Object> obj : subAccountList){
							EOObject eoObject2 = EOObject.getObjectByPK("EOAddExpensesDetail",obj.get("primaryKey")+"");
							obj.put("updatedDate", DateUtil.formatedCurrentDate());
							DBServices.update(EOObject.updateObject(eoObject2, obj));
						}
					}
				}
				
			}
			
			return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		}else{
			//no data received from request 
			return Response.status(201).entity(JSONUtil.objectToJson("no data")).build();
		}
	}
	
	public static HashMap<String, Object> getExpenseSheetData(HashMap<String, Object> map){
		System.out.println("map:::"+map);
		HashMap<String, Object> finalMap = new HashMap<>();
		String str = "SELECT primary_key, background_color, color_code, course_array, "
					+" CONCAT(first_name,' ',last_name) as teacherfullname,"
					+" CONCAT(first_name_jap, ' ',last_name_jap) teacherfullnamejap,"
					+" gender, teacher_id FROM eoteacher_user";
		
		List<HashMap<String, Object>> teacherList = DBServices.getNativeQueryResult(str);
		
		String month = (String) map.get("month");
		String year = (String) map.get("year");
		
		String studentQry = "SELECT s.primary_key AS studentpk, CONCAT(s.first_name,' ', s.last_name) as studentfullname"
						+ " FROM eostudent_user s WHERE s.is_active = true";
		List<HashMap<String, Object>> defaultStudentList = DBServices.getNativeQueryResult(studentQry);
		HashMap<String, Object> respMap = new HashMap<>();
		HashMap<String, Object> defaultStudentMap = new HashMap<>();
		if(defaultStudentList != null && defaultStudentList.size() > 0){
			for(HashMap<String, Object>stdObj : defaultStudentList){
				HashMap<String, Object> innerMap = new HashMap<>();
				innerMap.put("studentpk", stdObj.get("studentpk"));
				innerMap.put("studentFullName", stdObj.get("studentfullname"));
				
				defaultStudentMap.put(stdObj.get("studentfullname")+"_"+stdObj.get("studentpk"), innerMap);
			}
		}
		
		String qry = "SELECT m.primary_key, m.grand_total, m.month, m.year, m.eostudentuser_primary_key,"
					+" d.eocourses_primary_key, CONCAT(u.first_name,' ' ,u.last_name) As studentfullname, "
					+" c.course_name FROM eostudent_invoice_main m LEFT JOIN eostudent_invoice_detail d ON "
					+" m.primary_key = d.eostudentinvoicemain_primary_key"
					+" LEFT JOIN eocourses c ON c.primary_key = d.eocourses_primary_key"
					+" LEFT JOIN eostudent_user u ON u.primary_key = m.eostudentuser_primary_key"
					+" WHERE m.month = '"+month+"' AND m.year = '"+year+"' AND m.is_active = true";
		List<HashMap<String, Object>> expenseList = DBServices.getNativeQueryResult(qry);
		
		
		for(HashMap<String, Object> obj : expenseList){
			String key = obj.get("studentfullname") +"_"+obj.get("eostudentuser_primary_key");
			respMap.get(key);
			HashMap<String, Object> innerMap = new HashMap<>();
			
			innerMap.put("primaryKey",obj.get("primary_key"));
			innerMap.put("grandTotal",obj.get("grand_total"));
			innerMap.put("month",obj.get("month"));
			innerMap.put("year",obj.get("year"));
			innerMap.put("studentFullName",obj.get("studentfullname"));
			innerMap.put("studentpk",obj.get("eostudentuser_primary_key"));
			
			String courses = obj.get("course_name")+"";
			innerMap.put("courseName",courses);
			List<Object> teacherNameList = new ArrayList<>();
			List<Object> teacherJapNameList = new ArrayList<>();
			
			for(HashMap<String, Object> teacher : teacherList){
				List<String> coursesPk = (List<String>) JSONUtil.jsonToObject(teacher.get("course_array")+"", List.class);
				if(coursesPk != null){
					if(coursesPk.contains(obj.get("eocourses_primary_key")+"")){
						teacherNameList.add(teacher.get("teacherfullname")+"_"+teacher.get("color_code")+"-"+teacher.get("primary_key"));
						innerMap.put("teacherNameList",teacherNameList);
						teacherJapNameList.add(teacher.get("teacherfullnamejap")+"_"+teacher.get("color_code")+"-"+teacher.get("primary_key"));
						innerMap.put("teacherJapNameList",teacherJapNameList);
					}
				}
			}
			if(respMap.isEmpty()){
				//1st time call
				respMap.put(key, innerMap);
				
			}else{
				if(respMap.containsKey(key)){
					//appending course name and adding teacher for the same student
					HashMap<String, Object> tempMap = (HashMap<String, Object>) respMap.get(key);
					String tempCourse = (String) tempMap.get("courseName");
					tempCourse += ", ";
					tempCourse += obj.get("course_name")+"";
					tempMap.put("courseName",tempCourse);
					List<Object> tempTeacherList = (List<Object>) tempMap.get("teacherNameList");
					List<Object> tempTeacherJapList = (List<Object>) tempMap.get("teacherJapNameList");
					
					for(HashMap<String, Object> teacher : teacherList){
						List<String> coursesPk = (List<String>) JSONUtil.jsonToObject(teacher.get("course_array")+"", List.class);
						if(coursesPk != null){
							if(coursesPk.contains(obj.get("eocourses_primary_key")+"")){
								if(tempTeacherList != null){
									if(!tempTeacherList.contains(teacher.get("teacherfullname")+"_"+teacher.get("color_code")+"-"+teacher.get("primary_key"))){
										tempTeacherList.add(teacher.get("teacherfullname")+"_"+teacher.get("color_code")+"-"+teacher.get("primary_key"));
										tempTeacherJapList.add(teacher.get("teacherfullnamejap")+"_"+teacher.get("color_code")+"-"+teacher.get("primary_key"));
									}
								}
							}
						}
					}
					
				}else{
					//new data
					respMap.put(key, innerMap);
				}
			}				
		}
		
		
		//merging the student whose invoice is not generated
		
		if(respMap != null && defaultStudentMap != null){
			for(Map.Entry<String, Object> entry1 : defaultStudentMap.entrySet()){
				if(!respMap.containsKey(entry1.getKey())){
					respMap.put(entry1.getKey(), entry1.getValue());
				}
			}
		}
		finalMap.put("income", respMap);
		
		String strQuery = "SELECT s.primary_key, s.amount, s.month, s.total_salary, s.year, s.eoteacheruser_primary_key,"
						+" CONCAT(u.first_name, ' ', u.last_name) as teacherfullname"
						+" FROM eogenerate_slip s LEFT JOIN eoteacher_user u ON u.primary_key = s.eoteacheruser_primary_key"
						+" WHERE s.month = '"+month+"' AND s.year ='"+year+"'";
		List<HashMap<String, Object>> salaryData = DBServices.getNativeQueryResult(strQuery);
		
		
		String teacherQry = "SELECT s.primary_key AS eoteacheruser_primary_key, CONCAT(s.first_name,' ',s.last_name) AS teacherfullname"
							+" FROM eoteacher_user s WHERE s.is_active = true";
		List<HashMap<String, Object>> defaultTeacherList = DBServices.getNativeQueryResult(teacherQry);
		
		//merging the teacher whose salary is not generated
		
		List<HashMap<String, Object>> finalSalaryList = new ArrayList<>();
		if (salaryData.size() == 0) {
			finalSalaryList = defaultTeacherList;
		}
		if (salaryData != null && salaryData.size() > 0) {
			finalSalaryList = salaryData;
		}
		
		if(salaryData != null && salaryData.size() > 0){
			if(defaultTeacherList != null && defaultTeacherList.size() > 0){
				for(HashMap<String, Object> list : defaultTeacherList){
					boolean flag = true;
					for(HashMap<String, Object> mainList : salaryData){
						if(mainList.get("eoteacheruser_primary_key").equals(list.get("eoteacheruser_primary_key"))){
							flag = false;
						}
					}
					if(flag){
						finalSalaryList.add(list);
					}
				}
			}
		}
		finalMap.put("salaryData", finalSalaryList);
		
		
		List<Object> expenseDataObj = DBServices
				.get("FROM EOAddExpenses WHERE is_active = true AND month='"+month+"' AND year='"+year+"'");
		List<HashMap<String, Object>> expenseDataList = new ArrayList<>();
		if(expenseDataObj != null && expenseDataObj.size() > 0){
			for(Object expense : expenseDataObj){
				HashMap<String, Object> tempMap = new HashMap<>();
				EOAddExpenses eoAddExpenses = (EOAddExpenses) expense;
				tempMap.put("primaryKey", eoAddExpenses.primaryKey);
				tempMap.put("accountName", eoAddExpenses.accountName);
				tempMap.put("accountType", eoAddExpenses.accountType);
				tempMap.put("descriptions", eoAddExpenses.descriptions);
				tempMap.put("amount", eoAddExpenses.amount);
				List<Object> subExpenseDataObj = DBServices
						.get("FROM EOAddExpensesDetail WHERE eoaddexpenses_primary_key = "+eoAddExpenses.primaryKey);
				List<HashMap<String, Object>> subExpenseList = new ArrayList<>();
				if(subExpenseDataObj != null && subExpenseDataObj.size() > 0){
					for(Object subExpense : subExpenseDataObj){
						HashMap<String, Object> subMap = new HashMap<>();
						EOAddExpensesDetail eoAddExpensesDetail = (EOAddExpensesDetail) subExpense;
						subMap.put("primaryKey", eoAddExpensesDetail.primaryKey);
						subMap.put("amount", eoAddExpensesDetail.amount);
						subMap.put("descriptions", eoAddExpensesDetail.descriptions);
						subMap.put("subAccountName", eoAddExpensesDetail.subAccountName);
						subExpenseList.add(subMap);
					}
					tempMap.put("subExpenseList", subExpenseList);
				}
				expenseDataList.add(tempMap);
			}
		}
		finalMap.put("expenseData", expenseDataList);
		return finalMap;
	}
	@POST
	@Path("/getDataForExpenseSheet")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDataForExpenseSheet(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		return Response.status(201).entity(JSONUtil.objectToJson(getExpenseSheetData(map))).build();
		
	}
}
