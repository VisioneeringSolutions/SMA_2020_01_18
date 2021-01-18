package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.vspl.music.model.eo.EOGenerateSlip;
import com.vspl.music.model.eo.EOGenerateSlipDetail;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOPdf;
import com.vspl.music.model.eo.EOStudentCreditNote;
import com.vspl.music.model.eo.EOStudentFreeTextInvoice;
import com.vspl.music.model.eo.EOStudentFreeTextInvoiceMapping;
import com.vspl.music.model.eo.EOStudentInvoiceDetail;
import com.vspl.music.model.eo.EOStudentInvoiceEditable;
import com.vspl.music.model.eo.EOStudentInvoiceMain;

import com.vspl.music.services.DBServices;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.JSONUtil;


/**
 * The Invoice controller program implements an application that Manages Invoice
 * of the Student
 *
 * @author Kundan Kumar
 * @since
 */
@Path("/ajaxInvoice")
public class InvoiceController {

	public static Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	@POST
	@Path("/createInvoice")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createInvoice(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		
		List<HashMap<String, Object>> attributeList = (List<HashMap<String, Object>>) map.get("attributeList");		
		List<HashMap<String, Object>> dueList = (List<HashMap<String, Object>>) map.get("dueList");	
		List<HashMap<String, Object>> editable = (List<HashMap<String, Object>>) map.get("editable");
		List<HashMap<String, Object>> substractional = (List<HashMap<String, Object>>) map.get("substractional");
		List<HashMap<String, Object>> creditNote = (List<HashMap<String, Object>>) map.get("creditNote");	
		String status = (String) map.get("status");
		
		map.remove("attributeList");
		map.remove("dueList");
		map.remove("editable");
		map.remove("creditNote");
		map.remove("substractional");
		
		if (map.get("primaryKey") == null) {
			if(dueList != null && dueList.size() > 0){
				for (HashMap<String, Object> obj : dueList) {

					String pk = obj.get("primary_key") + "";
					HashMap<String, Object> innerMap = new HashMap<>();

					EOObject eoObject = EOObject.getObjectByPK("EOStudentInvoiceMain", pk);

					innerMap.put("dueAmount", 0);
					innerMap.put("updatedDate", DateUtil.formatedCurrentDate());
					innerMap.put("className", "EOStudentInvoiceMain");
					innerMap.put("primaryKey", obj.get("primary_key"));

					DBServices.update(EOObject.updateObject(eoObject, innerMap));
				}
			}
			
			map.put("createdDate", DateUtil.formatedCurrentDate());
			map.put("className", "EOStudentInvoiceMain");
			DBServices.create(EOObject.createObject(map));
			EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) EOObject.getLatestObject("EOStudentInvoiceMain");
			if(attributeList != null &&  attributeList.size() > 0){
				for (HashMap<String, Object> obj : attributeList) {

					obj.put("createdDate", DateUtil.formatedCurrentDate());
					obj.put("eoStudentInvoiceMain", eoStudentInvoiceMain.primaryKey);
					obj.put("year", eoStudentInvoiceMain.year);
					obj.put("month", eoStudentInvoiceMain.month);
					obj.put("className", "EOStudentInvoiceDetail");
					DBServices.create(EOObject.createObject(obj));
				}
			}
			
			if(editable != null &&  editable.size() > 0){
				for (HashMap<String, Object> obj : editable) {
					if(obj.get("amount") != null || obj.get("amount") != ""){
						obj.put("createdDate", DateUtil.formatedCurrentDate());
						obj.put("eoStudentInvoiceMain", eoStudentInvoiceMain.primaryKey);
						obj.put("year", eoStudentInvoiceMain.year);
						obj.put("month", eoStudentInvoiceMain.month);
						obj.put("amount", obj.get("amount"));
						obj.put("description", obj.get("description"));
						obj.put("className", "EOStudentInvoiceEditable");
						DBServices.create(EOObject.createObject(obj));
					}
				}
			}
			if(substractional != null &&  substractional.size() > 0){
				for (HashMap<String, Object> obj : substractional) {
					if(obj.get("amount") != null || obj.get("amount") != ""){
						obj.put("createdDate", DateUtil.formatedCurrentDate());
						obj.put("eoStudentInvoiceMain", eoStudentInvoiceMain.primaryKey);
						obj.put("year", eoStudentInvoiceMain.year);
						obj.put("month", eoStudentInvoiceMain.month);
						obj.put("amount", obj.get("amount"));
						obj.put("description", obj.get("description"));
						obj.put("className", "EOStudentInvoiceEditable");
						DBServices.create(EOObject.createObject(obj));
					}
				}
			}
			
			
		} else {
			if(dueList != null && dueList.size() > 0){
				for (HashMap<String, Object> obj : dueList) {

					String pk = obj.get("primary_key") + "";
					HashMap<String, Object> innerMap = new HashMap<>();

					EOObject eoObject2 = EOObject.getObjectByPK("EOStudentInvoiceMain", pk);

					innerMap.put("dueAmount", 0);
					innerMap.put("updatedDate", DateUtil.formatedCurrentDate());
					innerMap.put("className", "EOStudentInvoiceMain");
					innerMap.put("primaryKey", obj.get("primary_key"));

					DBServices.update(EOObject.updateObject(eoObject2, innerMap));
				}
			}
			
			map.put("updatedDate", DateUtil.formatedCurrentDate());
			map.put("className", "EOStudentInvoiceMain");
			EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) EOObject.getObjectByPK("EOStudentInvoiceMain", (String) map.get("primaryKey"));
			DBServices.update(EOObject.updateObject(eoStudentInvoiceMain, map));
			
			/*if(status.equalsIgnoreCase("Cancelled")){
				String eoPdfPk = eoStudentInvoiceMain.eoPdf.primaryKey+"";
				EOPdf eoPdf = (EOPdf) EOObject.getObjectByPK("EOPdf", eoPdfPk);
				
				HashMap<String, Object> pdfMap = new HashMap<>();
				pdfMap.put("isActive", false);
				DBServices.update(EOObject.updateObject(eoPdf, pdfMap));
			}*/
			
			
			if(attributeList != null && attributeList.size() > 0){
				for (HashMap<String, Object> obj : attributeList) {
					if(obj.get("primaryKey") == null){
						obj.put("createdDate", DateUtil.formatedCurrentDate());
						obj.put("className", "EOStudentInvoiceDetail");
						obj.put("eoStudentInvoiceMain", map.get("primaryKey"));
						obj.put("year", map.get("year"));
						obj.put("month", map.get("month"));
						DBServices.create(EOObject.createObject(obj));
					}
					else{
						obj.put("updatedDate", DateUtil.formatedCurrentDate());
						EOObject eoObject2 = EOObject.getObjectByPK("EOStudentInvoiceDetail", (String) obj.get("primaryKey"));
						DBServices.update(EOObject.updateObject(eoObject2, obj));
					}
				}
			}
			
			if(editable != null &&  editable.size() > 0){
				for (HashMap<String, Object> obj : editable) {
					if(obj.get("amount") != null || obj.get("amount") != ""){
						if(obj.get("primaryKey") == null){
							obj.put("createdDate", DateUtil.formatedCurrentDate());
							obj.put("eoStudentInvoiceMain", eoStudentInvoiceMain.primaryKey);
							obj.put("year", eoStudentInvoiceMain.year);
							obj.put("month", eoStudentInvoiceMain.month);
							obj.put("amount", obj.get("amount"));
							obj.put("description", obj.get("description"));
							obj.put("className", "EOStudentInvoiceEditable");
							DBServices.create(EOObject.createObject(obj));
						}
						else{
							obj.put("updatedDate", DateUtil.formatedCurrentDate());
							EOObject eoObject2 = EOObject.getObjectByPK("EOStudentInvoiceEditable", (String) obj.get("primaryKey"));
							DBServices.update(EOObject.updateObject(eoObject2, obj));
						}
					}
				}
			}
			if(substractional != null &&  substractional.size() > 0){
				for (HashMap<String, Object> obj : substractional) {
					if(obj.get("amount") != null || obj.get("amount") != ""){
						if(obj.get("primaryKey") == null){
							obj.put("createdDate", DateUtil.formatedCurrentDate());
							obj.put("eoStudentInvoiceMain", eoStudentInvoiceMain.primaryKey);
							obj.put("year", eoStudentInvoiceMain.year);
							obj.put("month", eoStudentInvoiceMain.month);
							obj.put("amount", obj.get("amount"));
							obj.put("description", obj.get("description"));
							obj.put("className", "EOStudentInvoiceEditable");
							DBServices.create(EOObject.createObject(obj));
						}
						else{
							obj.put("updatedDate", DateUtil.formatedCurrentDate());
							EOObject eoObject2 = EOObject.getObjectByPK("EOStudentInvoiceEditable", (String) obj.get("primaryKey"));
							DBServices.update(EOObject.updateObject(eoObject2, obj));
						}
					}
				}
			}
			
			if(creditNote != null &&  creditNote.size() > 0){
				for (HashMap<String, Object> obj : creditNote) {
					if(obj.get("primaryKey") == null){
						obj.put("createdDate", DateUtil.formatedCurrentDate());
						obj.put("eoStudentInvoiceMain", eoStudentInvoiceMain.primaryKey);
						obj.put("year", eoStudentInvoiceMain.year);
						obj.put("month", eoStudentInvoiceMain.month);
						obj.put("amount", obj.get("amount"));
						obj.put("description", obj.get("description"));
						obj.put("className", "EOStudentCreditNote");
						DBServices.create(EOObject.createObject(obj));
					}
				}
			}
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	@POST
	@Path("/getStudentForInvoice")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentForInvoice(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String month = (String)map.get("month");
		String year = (String)map.get("year");
		String startDate = (String)map.get("startDate");
		String endDate = (String)map.get("endDate");
		String str = " SELECT bm.primary_key, bm.batch_pk, s.primary_key as student_pk, bm.eostudentbatch_primary_key, "
					+" c.fee_type,b.batch_name , c.course_name, b.eocourses_primary_key"
					+" as coursepk ,lk.music_type,c.fees,c.lkmusictype_primary_key,CONCAT(s.first_name,' ' ,s.last_name) "
					+" as studentfullname, CONCAT(s.first_name,' ' ,s.last_name ,'_',s.primary_key ) as stdkey, "
					+" b.primary_key as batchpk, lkd.duration FROM eostudent_batch_mapping bm "
					+" LEFT JOIN eobatch b ON b.primary_key = CAST(bm.batch_pk as bigint) "
					+" LEFT JOIN eocourses c ON c.primary_key = CAST(b.eocourses_primary_key as bigint) "
					+" LEFT JOIN lkclass_duration lkd ON lkd.primary_key = CAST(c.lkclassduration_primary_key as bigint)"
					+" LEFT JOIN lkmusic_type lk ON lk.primary_key = CAST(c.lkmusictype_primary_key as bigint)"
					+" LEFT JOIN eostudent_user s ON s.primary_key = CAST(bm.student_pk as bigint)"
					+" WHERE s.is_active = true AND b.is_active = true AND is_visible = true ORDER BY studentfullname ";
		
		List< HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(str);
		
		String[] monthArr = DateUtil.janToDecMonthArr;
		
		int count = 0;
		String monthName = "(";
		for (String mon : monthArr) {
			if (mon.equalsIgnoreCase(month) ) {
				if(count == 0){
					monthName += "'"+mon+"')";
				}else{
					monthName += ", "+"'"+mon+"'" +")";
				}
				break;
			}
			if(count == 0){
				monthName += "'"+mon+"'";
			}else{
				monthName += ", "+"'"+mon+"'";
			}
			
			count++;
		}
		
		Map< Object,HashMap<String, Object>> respMap = new HashMap<>();
		for(HashMap<String, Object> obj : dataList){
			
			HashMap<String, Object> innerMap = (HashMap<String, Object>) obj;
			HashMap<String, Object> mainMap = respMap.get(innerMap.get("stdkey"));
			
			if(mainMap == null){
				mainMap = new HashMap<>();
				
				HashMap<String, Object> detail = new HashMap<>();
				
				detail.put("eoStudentUser", innerMap.get("student_pk")+"");
				detail.put("studentfullname", innerMap.get("studentfullname"));
				
				
				String dataQry = "select invoice_no from eostudent_invoice_main where status = 'Cancelled' "
						 + " and eostudentuser_primary_key = "+innerMap.get("student_pk")+" AND is_active = false ";
				
				List<HashMap<String , Object>> cancelledInvoiceList = DBServices.getNativeQueryResult(dataQry);
				if(cancelledInvoiceList != null && cancelledInvoiceList.size() > 0){
					detail.put("cancelledInvoiceList", cancelledInvoiceList);
				}
				
				String qryData = "SELECT invoice_no FROM eostudent_free_text_invoice "
						+ "WHERE student_pk = '"+innerMap.get("student_pk")+"' AND is_active = true";
				
				List<HashMap<String , Object>> freeTextInvoice = DBServices.getNativeQueryResult(qryData);
				
				if(freeTextInvoice != null && freeTextInvoice.size() > 0){
					detail.put("freeTextInvoiceNo", freeTextInvoice.get(0).get("invoice_no"));
				}
				
				String strQuery =" SELECT ta.status,SUM(ta.cancellation_amount) ,ta.eostudentuser_primary_key "
								+" FROM eotime_slot_allocation ta  LEFT JOIN eotimeslot t ON "
								+" t.primary_key = ta.eotimeslot_primary_key WHERE ta.status ='Cancelled'"
								+" AND t.date BETWEEN'"+startDate+"' AND '"+endDate+"' AND ta.student_cancellation = true "
								+" AND ta.eostudentuser_primary_key = "+innerMap.get("student_pk")
								+" GROUP BY ta.status , ta.eostudentuser_primary_key ";
				
				
				
				List<HashMap<String, Object>> cancelledAmountList = DBServices.getNativeQueryResult(strQuery);
				if(cancelledAmountList != null && cancelledAmountList.size() > 0){
					detail.put("cancellationAmount", cancelledAmountList.get(0).get("sum"));
				}
				
				String query = "SELECT primary_key, due_amount,month, year, eostudentuser_primary_key"
						+" FROM eostudent_invoice_main WHERE eostudentuser_primary_key ="
						+innerMap.get("student_pk")+" AND due_amount > 0 AND month IN "+monthName+" ORDER BY created_date";
		
					List<HashMap<String, Object>> dueList = DBServices.getNativeQueryResult(query);
					
					if(dueList != null && dueList.size() > 0){
						detail.put("dueList", dueList);
					}
				
				List<HashMap<String, Object>> attributeList = new ArrayList<>();
				
				HashMap<String, Object> attribMap = new HashMap<>();
				attribMap.put("courseName", innerMap.get("course_name"));
				attribMap.put("feeType", innerMap.get("fee_type"));
				attribMap.put("sessionDuration", innerMap.get("duration"));
				attribMap.put("eoCourses", innerMap.get("coursepk"));
				attribMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
				attribMap.put("fees", innerMap.get("fees"));
				attribMap.put("musicType", innerMap.get("music_type"));
				attribMap.put("eoBatch", innerMap.get("batchpk"));
				
				String qry = "SELECT ds.primary_key, ds.slot_pk, ds.month, ds.year, ds.eobatch_primary_key,"
					       +" ds.eoteacheruser_primary_key, ds.lkmusictype_primary_key,ds.eostudentuser_primary_key,"
					       +" CONCAT(b.batch_name,'_',b.primary_key) as batchkey FROM public.eodefined_slot ds "
					       +" LEFT JOIN eobatch b ON b.primary_key = ds.eobatch_primary_key "
					       +" WHERE ds.eostudentuser_primary_key ="+innerMap.get("student_pk") +" AND ds.month='"+month+"' AND ds.year='"+year+"'"
					       +" AND ds.lkmusictype_primary_key = "+innerMap.get("lkmusictype_primary_key") +" AND ds.eobatch_primary_key ="+innerMap.get("batchpk"); 	
				
				List<HashMap<String, Object>> sessionDetail = DBServices.getNativeQueryResult(qry);		
				
				double session = 0;
				double totalMins = 0;
				if(sessionDetail != null && sessionDetail.size() > 0){
					
					String sessionDurationArr [] = ((String) innerMap.get("duration")).split(" ");
					double sessionDuration = Double.parseDouble(sessionDurationArr[0]);
					for(HashMap<String, Object> sessionObj: sessionDetail){
					
						List<String> slotPkArray = (List<String>)JSONUtil.jsonToObject((String) sessionObj.get("slot_pk"), List.class);
						totalMins += 15 * slotPkArray.size();
					}
					session = totalMins/sessionDuration;
				}
				
				attribMap.put("session",session);
				attribMap.put("totalMins",totalMins);
				attributeList.add(attribMap);
				detail.put("attributeList", attributeList);
				respMap.put(innerMap.get("stdkey"), detail);
			}
			
			else{
				if(respMap.containsKey(innerMap.get("stdkey"))){
					HashMap<String, Object> tempDetail = respMap.get(innerMap.get("stdkey"));
					
					List<HashMap<String, Object>> tempDetailList = (List<HashMap<String, Object>>) tempDetail.get("attributeList");
					
					HashMap<String, Object> attribMap = new HashMap<>();
					attribMap.put("courseName", innerMap.get("course_name"));
					attribMap.put("feeType", innerMap.get("fee_type"));
					attribMap.put("sessionDuration", innerMap.get("duration"));
					attribMap.put("eoCourses", innerMap.get("coursepk"));
					attribMap.put("lkMusicType", innerMap.get("lkmusictype_primary_key"));
					attribMap.put("fees", innerMap.get("fees"));
					attribMap.put("musicType", innerMap.get("music_type"));
					attribMap.put("eoBatch", innerMap.get("batchpk"));
					
					String qry = "SELECT ds.primary_key, ds.slot_pk, ds.month, ds.year, ds.eobatch_primary_key,"
						       +" ds.eoteacheruser_primary_key, ds.lkmusictype_primary_key,ds.eostudentuser_primary_key,"
						       +" CONCAT(b.batch_name,'_',b.primary_key) as batchkey FROM public.eodefined_slot ds "
						       +" LEFT JOIN eobatch b ON b.primary_key = ds.eobatch_primary_key "
						       +" WHERE ds.eostudentuser_primary_key ="+innerMap.get("student_pk") +" AND ds.month='"+month+"' AND ds.year='"+year+"'"
						       +" AND ds.lkmusictype_primary_key = "+innerMap.get("lkmusictype_primary_key")+" AND ds.eobatch_primary_key ="+innerMap.get("batchpk"); 	
					
					List<HashMap<String, Object>> sessionDetail = DBServices.getNativeQueryResult(qry);
					
					double session = 0;
					double totalMins = 0;
					if(sessionDetail != null && sessionDetail.size() > 0){
						String sessionDurationArr [] = ((String) innerMap.get("duration")).split(" ");
						double sessionDuration = Double.parseDouble(sessionDurationArr[0]);
						for(HashMap<String, Object> sessionObj: sessionDetail){
						
							List<String> slotPkArray = (List<String>)JSONUtil.jsonToObject((String) sessionObj.get("slot_pk"), List.class);
							totalMins += 15 * slotPkArray.size();
						}
						session = totalMins/sessionDuration;
					}
					attribMap.put("session",session);
					attribMap.put("totalMins",totalMins);
					
					tempDetailList.add(attribMap);
				}
			}
		}
		
		List<Object> dataListMain = DBServices.get("FROM EOStudentInvoiceMain WHERE isActive = true AND month = '"+month+"' AND year ='"+year+"'");
		
		Map< Object,HashMap<String, Object>> mainRespMap = new HashMap<>();
		
		if (dataListMain != null && dataListMain.size() > 0) {
			for (Object obj : dataListMain) {

				EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) obj;
				List<EOStudentInvoiceDetail> detailArray = (List<EOStudentInvoiceDetail>) eoStudentInvoiceMain.eoStudentInvoiceDetails;
				List<EOStudentInvoiceEditable> editableDetails = (List<EOStudentInvoiceEditable>) eoStudentInvoiceMain.eoStudentInvoiceEditable;
				List<EOStudentCreditNote> creditNote = (List<EOStudentCreditNote>) eoStudentInvoiceMain.eoStudentCreditNote;
				
				HashMap<String, Object> MainInnerMap = new HashMap<>();

				MainInnerMap.put("primaryKey", eoStudentInvoiceMain.primaryKey+"");
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
				MainInnerMap.put("invoiceNo", eoStudentInvoiceMain.invoiceNo);
				MainInnerMap.put("isActive", eoStudentInvoiceMain.isActive);
				MainInnerMap.put("dueDate", eoStudentInvoiceMain.dueDate);
				
				String dataQry = "select invoice_no from eostudent_invoice_main where status = 'Cancelled' "
						 + " and eostudentuser_primary_key = "+eoStudentInvoiceMain.eoStudentUser.primaryKey + "AND is_active = false ";
				
				List<HashMap<String , Object>> cancelledInvoiceList = DBServices.getNativeQueryResult(dataQry);
				if(cancelledInvoiceList != null && cancelledInvoiceList.size() > 0){
					MainInnerMap.put("cancelledInvoiceList", cancelledInvoiceList);
				}
				
				String qryData = "SELECT invoice_no FROM eostudent_free_text_invoice "
						+ "WHERE student_pk = '"+eoStudentInvoiceMain.eoStudentUser.primaryKey+"' AND is_active = true";
				
				List<HashMap<String , Object>> freeTextInvoice = DBServices.getNativeQueryResult(qryData);
				
				if(freeTextInvoice != null && freeTextInvoice.size() > 0){
					MainInnerMap.put("freeTextInvoiceNo", freeTextInvoice.get(0).get("invoice_no"));
				}
				
				String key = eoStudentInvoiceMain.eoStudentUser.getFullName() +"_"+eoStudentInvoiceMain.eoStudentUser.primaryKey + "";

				List<HashMap<String, Object>> mainTempList = new ArrayList<>();
				List<HashMap<String, Object>> editableList = new ArrayList<>();
				List<HashMap<String, Object>> creditNoteList = new ArrayList<>();
				
				if (detailArray != null && detailArray.size() > 0) {
					for (EOStudentInvoiceDetail eoStudentInvoiceDetail : detailArray) {
						HashMap<String, Object> mainTempMap = new HashMap<>();

						mainTempMap.put("primaryKey", eoStudentInvoiceDetail.primaryKey+"");
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
						mainTempMap.put("totalMins", eoStudentInvoiceDetail.totalMins);


						mainTempList.add(mainTempMap);					
						}
					MainInnerMap.put("attributeList", mainTempList);	
				}
				
				if (editableDetails != null && editableDetails.size() > 0) {
					for (EOStudentInvoiceEditable eoStudentInvoiceEditable : editableDetails) {
						HashMap<String, Object> mainTempMap = new HashMap<>();

						if(eoStudentInvoiceEditable.isActive == true){
							mainTempMap.put("primaryKey", eoStudentInvoiceEditable.primaryKey+"");
							mainTempMap.put("amount", eoStudentInvoiceEditable.amount);
							mainTempMap.put("description", eoStudentInvoiceEditable.description);
							mainTempMap.put("month", eoStudentInvoiceEditable.month);
							mainTempMap.put("year", eoStudentInvoiceEditable.year);
							mainTempMap.put("isActive", eoStudentInvoiceEditable.isActive);

							editableList.add(mainTempMap);	
						}				
					}
					if(editableList != null && editableList.size() > 0){
						MainInnerMap.put("editable", editableList);
					}						
				}
				
				if (creditNote != null && creditNote.size() > 0) {
					for (EOStudentCreditNote eoStudentCreditNote : creditNote) {
						HashMap<String, Object> mainTempMap = new HashMap<>();

						if(eoStudentInvoiceMain.isActive == true){
							mainTempMap.put("primaryKey", eoStudentCreditNote.primaryKey+"");
							mainTempMap.put("amount", eoStudentCreditNote.amount);
							mainTempMap.put("description", eoStudentCreditNote.description);
							mainTempMap.put("month", eoStudentCreditNote.month);
							mainTempMap.put("year", eoStudentCreditNote.year);
							mainTempMap.put("isActive", eoStudentCreditNote.isActive);

							creditNoteList.add(mainTempMap);	
						}				
					}
					MainInnerMap.put("creditNote", creditNoteList);
				}
				
				String query = "SELECT primary_key, due_amount,month, year, eostudentuser_primary_key"
								+" FROM eostudent_invoice_main WHERE eostudentuser_primary_key ="
								+eoStudentInvoiceMain.eoStudentUser.primaryKey+" AND due_amount > 0 AND month IN "
								+monthName+" ORDER BY created_date" ;
				
				List<HashMap<String, Object>> dueList = DBServices.getNativeQueryResult(query);
				
				if(dueList != null && dueList.size() > 0){
					MainInnerMap.put("dueList", dueList);
				}
				mainRespMap.put(key, MainInnerMap);
			}
		}
		
		Map< Object,HashMap<String, Object>> finalMap = new HashMap<>();
		if (mainRespMap != null && mainRespMap.size() > 0) {
			for(Entry<Object, HashMap<String, Object>> entry1 : mainRespMap.entrySet()){
				finalMap.put(entry1.getKey(), (HashMap<String, Object>)entry1.getValue());
			}
		}
		
		if(mainRespMap != null && respMap != null){
			for(Entry<Object, HashMap<String, Object>> entry1 : respMap.entrySet()) {
				
				HashMap<String, Object> respMapValue = (HashMap<String, Object>) entry1.getValue();
				
				boolean flag = true;
				for(Entry<Object, HashMap<String, Object>> entry2 : mainRespMap.entrySet()){
					
					HashMap<String, Object> mainRespMapValue = (HashMap<String, Object>) entry2.getValue();
					if(respMapValue.get("eoStudentUser").equals(mainRespMapValue.get("eoStudentUser"))){
						flag = false;
					}
				}
				
				if(flag){
					finalMap.put(entry1.getKey(),respMapValue);
				}
			}
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson(finalMap)).build();
	}
	
	@POST
	@Path("/getInvoiceByStudentPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getInvoiceByStudentPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String month = (String)map.get("month");
		String year = (String)map.get("year");
		String stdPk = (String)map.get("stdPk");
		
		List<Object> dataListMain = DBServices.get("FROM EOStudentInvoiceMain "
				+ "WHERE isActive = true AND eoStudentUser ="+stdPk+" AND status = 'Fix' AND year ='"+year+"'");
		
		Map< Object,HashMap<String, Object>> mainRespMap = new HashMap<>();
		
		if (dataListMain != null && dataListMain.size() > 0) {
			for (Object obj : dataListMain) {

				EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) obj;
				List<EOStudentInvoiceDetail> detailArray = (List<EOStudentInvoiceDetail>) eoStudentInvoiceMain.eoStudentInvoiceDetails;
				List<EOStudentInvoiceEditable> editableArray = (List<EOStudentInvoiceEditable>) eoStudentInvoiceMain.eoStudentInvoiceEditable;
				HashMap<String, Object> MainInnerMap = new HashMap<>();

				MainInnerMap.put("primaryKey", eoStudentInvoiceMain.primaryKey+"");
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
				
				String key = eoStudentInvoiceMain.eoStudentUser.getFullName() +"_"+eoStudentInvoiceMain.eoStudentUser.primaryKey + "";

				List<HashMap<String, Object>> mainTempList = new ArrayList<>();
				if (detailArray != null && detailArray.size() > 0) {
					for (EOStudentInvoiceDetail eoStudentInvoiceDetail : detailArray) {
						HashMap<String, Object> mainTempMap = new HashMap<>();

						mainTempMap.put("primaryKey", eoStudentInvoiceDetail.primaryKey+"");
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
				
				List<HashMap<String, Object>> editableList = new ArrayList<>();
				if (editableArray != null && editableArray.size() > 0) {
					for (EOStudentInvoiceEditable eoStudentInvoiceEditable : editableArray) {
						HashMap<String, Object> mainTempMap = new HashMap<>();

						if(eoStudentInvoiceEditable.isActive == true){
							mainTempMap.put("primaryKey", eoStudentInvoiceEditable.primaryKey+"");
							mainTempMap.put("amount", eoStudentInvoiceEditable.amount);
							mainTempMap.put("description", eoStudentInvoiceEditable.description);

							editableList.add(mainTempMap);	
						}
										
					}
					MainInnerMap.put("editableList", editableList);
				}
				mainRespMap.put(eoStudentInvoiceMain.month, MainInnerMap);
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(mainRespMap)).build();
	}
	
	
	@POST
	@Path("/getMaxInvoiceNo")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMaxInvoiceNo(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String className = (String)map.get("className");
		
		List<HashMap<String, Object>> invoiceNo = DBServices.getMaxRunningNo(className);
		return Response.status(201).entity(JSONUtil.objectToJson(invoiceNo)).build();
	}
	
	
	@POST
	@Path("/createFreeTextInvoiceByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createFreeTextInvoiceByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<HashMap<String, Object>> dataList = (List<HashMap<String, Object>>) map.get("freeTextInvoiceList");
		map.remove("freeTextInvoiceList");
		
		if(map.get("primarykey") == null){
			map.put("createdDate", DateUtil.formatedCurrentDate());
			map.put("className", "EOStudentFreeTextInvoice");
			DBServices.create(EOObject.createObject(map));
			
			EOStudentFreeTextInvoice eoStudentFreeTextInvoice = (EOStudentFreeTextInvoice) EOObject.getLatestObject("EOStudentFreeTextInvoice");
			for(HashMap<String, Object> obj : dataList){
				obj.put("createdDate", DateUtil.formatedCurrentDate());
				obj.put("eoStudentFreeTextInvoice", eoStudentFreeTextInvoice.primaryKey);
				DBServices.create(EOObject.createObject(obj));
			}
		}
		else{
	
		}
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	
	@POST
	@Path("/getFreeTextInvoiceByPk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getFreeTextInvoiceByPk(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String studentPk = (String) map.get("studentPk");
		String month = (String) map.get("month");
		String year = (String) map.get("year");
		
		String str = "FROM EOStudentFreeTextInvoice WHERE isActive = true AND studentPk = '"+studentPk+"' AND "
				+ " month ='"+month+"' AND year = '"+year+"'";
		List<Object> dataList = DBServices.get(str);
		
		HashMap<String, Object> innerMap = new HashMap<>();
		for(Object obj : dataList){
			
			EOStudentFreeTextInvoice eoStudentFreeTextInvoice = (EOStudentFreeTextInvoice) obj;
			
			innerMap.put("primaryKey", eoStudentFreeTextInvoice.primaryKey+"");
			innerMap.put("studentPk", eoStudentFreeTextInvoice.studentPk);
			innerMap.put("month", eoStudentFreeTextInvoice.month);
			innerMap.put("runningNo", eoStudentFreeTextInvoice.runningNo);
			innerMap.put("year", eoStudentFreeTextInvoice.year);
			innerMap.put("invoiceNo", eoStudentFreeTextInvoice.invoiceNo);
			
			List<EOStudentFreeTextInvoiceMapping> respList = 
					(List<EOStudentFreeTextInvoiceMapping>) eoStudentFreeTextInvoice.eoStudentFreeTextInvoiceMapping;
			
			if(respList != null && respList.size() > 0){
				
				List<HashMap<String, Object>> detailList = new ArrayList<>();
				for(EOStudentFreeTextInvoiceMapping eoStudentFreeTextInvoiceMapping : respList){
					
					HashMap<String, Object> detailMap = new HashMap<>();
					detailMap.put("primaryKey", eoStudentFreeTextInvoiceMapping.primaryKey);
					detailMap.put("amount", eoStudentFreeTextInvoiceMapping.amount);
					detailMap.put("description", eoStudentFreeTextInvoiceMapping.description);
					detailMap.put("isActive", eoStudentFreeTextInvoiceMapping.isActive);
					detailList.add(detailMap);
				}
				innerMap.put("freeTextInvoiceList", detailList);
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(innerMap)).build();
	}
	
	@POST
	@Path("/getInvoiceDateWiseDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getInvoiceDateWiseDetails(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String stdPk = (String) map.get("studentPk");
		String year = (String) map.get("year");
		String month = (String) map.get("month");
		
		String query = " SELECT d.day, d.date, CONCAT(d.start_time, '-' ,d.end_time) as time,ld.duration,"
						+" c.fees, d.slot_pk FROM eodefined_slot d"
						+" LEFT JOIN eobatch b ON eobatch_primary_key = b.primary_key"
						+" LEFT JOIN eocourses c ON b.eocourses_primary_key = c.primary_key"
						+" LEFT JOIN lkclass_duration ld ON c.lkclassduration_primary_key = ld.primary_key "
						+" WHERE eostudentuser_primary_key = '"+stdPk+"' AND month = '"+month+"' AND year = '"+year+"'";
		
		List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(query);
		List<HashMap<String, Object>> finalList = new ArrayList<>();
		
		if(dataList != null && dataList.size() > 0){
			for(HashMap<String, Object> obj : dataList){
				HashMap<String, Object> innerMap = new HashMap<>();
				double totalMins = 0;
				double session = 0;
				double amount = 0;
				innerMap.put("day", obj.get("day"));
				innerMap.put("date", obj.get("date"));
				innerMap.put("time", obj.get("time"));
				
				String durationArr[] = ((String)obj.get("duration")).split(" ");
				int duration = Integer.valueOf(durationArr[0]);
				List<String> slotPkArray = (List<String>)JSONUtil.jsonToObject((String) obj.get("slot_pk"), List.class);
				totalMins = 15 * slotPkArray.size();
				session = totalMins/duration;
				amount = session * Double.valueOf(obj.get("fees")+"");
				innerMap.put("amount", Math.round(amount));
				
				finalList.add(innerMap);
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(finalList)).build();
	}
}
	
	
