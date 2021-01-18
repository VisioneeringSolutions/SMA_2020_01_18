package com.vspl.music.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.model.eo.EOManagementUser;
import com.vspl.music.model.eo.EOMessageHistory;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOStudentUser;
import com.vspl.music.model.eo.EOTeacherUser;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.JSONUtil;
import com.vspl.music.util.VSPLUtil;

@Path("/ajaxMessage")
public class MessageController {
	
	public static Logger logger = LoggerFactory.getLogger(MessageController.class);

	@POST
	@Path("/getAvailableContactList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAvailableContactList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		
		String userKey = (String)map.get("userKey");
		String role = (String)map.get("role");
		
		String queryStudent = "";
		String queryTeacher = "";
		String queryManagement = "";
		
		if(role.equalsIgnoreCase("EOStudentUser")){
			queryManagement += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url FROM "
					+ " eomanagement_user m left join eoimage i on m.eoimage = i.primary_key";
			queryTeacher += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url,m.teacher_id as id FROM "
					+ " eoteacher_user m left join eoimage i on m.eoimage_primary_key = i.primary_key where m.is_active = true";
		}
		
		if(role.equalsIgnoreCase("EOTeacherUser")){
			queryManagement += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url FROM "
					+ " eomanagement_user m left join eoimage i on m.eoimage = i.primary_key";
			
			queryTeacher += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url,m.teacher_id as id FROM "
					+ " eoteacher_user m left join eoimage i on m.eoimage_primary_key = i.primary_key where m.is_active = true"
					+ " AND m.primary_key != "+userKey;
			
			queryStudent += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url,m.student_id as id FROM "
					+ " eostudent_user m left join eoimage i on m.eoimage_primary_key = i.primary_key where m.is_active = true";
		}
		if(role.equalsIgnoreCase("EOManagementUser")){
			queryManagement += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url FROM "
					+ " eomanagement_user m left join eoimage i on m.eoimage = i.primary_key where m.primary_key != "+userKey;
			
			queryTeacher += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url,m.teacher_id as id FROM "
					+ " eoteacher_user m left join eoimage i on m.eoimage_primary_key = i.primary_key where m.is_active = true";
			
			queryStudent += "SELECT m.primary_key, concat(m.first_name, ' ',m.last_name) as fullname, m.phone,i.image_url,m.student_id as id FROM "
					+ " eostudent_user m left join eoimage i on m.eoimage_primary_key = i.primary_key where m.is_active = true";
			
		}
		
		List<HashMap<String, Object>> studentList = new ArrayList<>();
		
		if(!role.equalsIgnoreCase("EOStudentUser")){
			studentList = DBServices.getNativeQueryResult(queryStudent);
		}
		
		List<HashMap<String, Object>> teacherList = DBServices.getNativeQueryResult(queryTeacher);
		List<HashMap<String, Object>> managementList = DBServices.getNativeQueryResult(queryManagement);
		
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		
		if(managementList != null && managementList.size() > 0){
			for(HashMap<String, Object> manageMap : managementList){
				HashMap<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("primaryKey", manageMap.get("primary_key"));
				tmpMap.put("fullName", manageMap.get("fullname"));
				tmpMap.put("phone", manageMap.get("phone"));
				if(manageMap.get("image_url") != null){

					tmpMap.put("imageUrl", manageMap.get("image_url"));
				}
				tmpMap.put("role", "EOManagementUser");
				resultList.add(tmpMap);
			}
		}
		if(teacherList != null && teacherList.size() > 0){
			for(HashMap<String, Object> teacherMap : teacherList){
				HashMap<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("primaryKey", teacherMap.get("primary_key"));
				tmpMap.put("fullName", teacherMap.get("fullname"));
				tmpMap.put("phone", teacherMap.get("phone"));
				if(teacherMap.get("image_url") != null){

					tmpMap.put("imageUrl", teacherMap.get("image_url"));
				}
				tmpMap.put("role", "EOTeacherUser");
				tmpMap.put("id", teacherMap.get("id"));
				resultList.add(tmpMap);
			}
		}
		if(studentList != null && studentList.size() > 0){
			for(HashMap<String, Object> studentMap : studentList){
				HashMap<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("primaryKey", studentMap.get("primary_key"));
				tmpMap.put("fullName", studentMap.get("fullname"));
				tmpMap.put("phone", studentMap.get("phone"));
				if(studentMap.get("image_url") != null){

					tmpMap.put("imageUrl", studentMap.get("image_url"));
				}
				tmpMap.put("role", "EOStudentUser");
				tmpMap.put("id", studentMap.get("id"));
				resultList.add(tmpMap);
			}
		}
	
		return Response.status(201).entity(JSONUtil.objectToJson(resultList)).build();
	}
	
	
	@POST
	@Path("/getContactListByUserKey")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getContactListByUserKey(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		
		String userKey = (String)map.get("userKey");
		String role = (String)map.get("role");
		
		String queryStr = "SELECT m.primary_key, m.created_date, m.receiver, m.receiver_class_name, m.sender, m.sender_class_name,"
				+ " m.message_id FROM eomessage m "
				+ " WHERE m.sender = "+userKey+" AND m.sender_class_name = '"+role+"'";
		
		List<HashMap<String, Object>> contactList = DBServices.getNativeQueryResult(queryStr);
		List<HashMap<String, Object>> resultList = new ArrayList<>();
		if(contactList != null && contactList.size() > 0){
			for(HashMap<String, Object> rMap : contactList){
				HashMap<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("eoMessage", rMap.get("primary_key"));
				tmpMap.put("messageID", rMap.get("message_id"));
				tmpMap.put("receiver", rMap.get("receiver"));
				tmpMap.put("receiverClassName", rMap.get("receiver_class_name"));
				if(((String)rMap.get("receiver_class_name")).equalsIgnoreCase("EOStudentUser")){
					EOStudentUser eoStudentUser = (EOStudentUser)EOObject.getObjectByPK("EOStudentUser", (BigInteger)rMap.get("receiver")+"");
					tmpMap.put("fullName", eoStudentUser.getFullName());
					tmpMap.put("id", eoStudentUser.studentId);
					if(eoStudentUser.eoImage != null){
						tmpMap.put("imageUrl", eoStudentUser.eoImage.imageUrl);	
					}
					
				}
				
				else if(((String)rMap.get("receiver_class_name")).equalsIgnoreCase("EOTeacherUser")){
					EOTeacherUser eoTeacherUser = (EOTeacherUser)EOObject.getObjectByPK("EOTeacherUser", (BigInteger)rMap.get("receiver")+"");
					tmpMap.put("fullName", eoTeacherUser.getFullName());
					tmpMap.put("id", eoTeacherUser.teacherId);
					if(eoTeacherUser.eoImage != null){
						tmpMap.put("imageUrl", eoTeacherUser.eoImage.imageUrl);	
					}
				}
				
				else if(((String)rMap.get("receiver_class_name")).equalsIgnoreCase("EOManagementUser")){
					EOManagementUser eoManagementUser = (EOManagementUser)EOObject.getObjectByPK("EOManagementUser", (BigInteger)rMap.get("receiver")+"");
					tmpMap.put("fullName", eoManagementUser.getFullName());
						if(eoManagementUser.eoImage != null){
							tmpMap.put("imageUrl", eoManagementUser.eoImage.imageUrl);	
						}				
					}
				String qry2 = "SELECT created_date_time, message FROM eomessage_history where primary_key = (Select max(primary_key) from eomessage_history where message_id = '"
						+(String)rMap.get("message_id")+"')";
				
				List<HashMap<String, Object>> durationList = DBServices.getNativeQueryResult(qry2);
				if(durationList != null && durationList.size() > 0){
					HashMap<String, Object> dMap = durationList.get(0);
					tmpMap.put("lastMessage", dMap.get("message"));
					tmpMap.put("createdDateTime", dMap.get("created_date_time"));
				}
				//tmpMap.put("unreadCount", 0);
				
				String qry3 = "Select COUNT(primary_key) as unread_count from eomessage_history where is_read = false"
						+ " AND message_id = '"	+(String)rMap.get("message_id")+"' AND receiver = "+userKey;
				List<HashMap<String, Object>> countList = DBServices.getNativeQueryResult(qry3);
				if(countList != null && countList.size() > 0){
					HashMap<String, Object> cMap = countList.get(0);
					tmpMap.put("unreadCount", cMap.get("unread_count"));
				}
				resultList.add(tmpMap);
				
			}
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson(resultList)).build();
	}
	
	@POST
	@Path("/addContactList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addContactList(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		
		List<HashMap<String, Object>> contactArrList = (List<HashMap<String, Object>>)map.get("contactList");
		
		for(HashMap<String, Object> tmpMap : contactArrList){
			String userKey = (String)tmpMap.get("userKey");
			String role = (String)tmpMap.get("role");
			String receiver = (String)tmpMap.get("receiver");
			String receiverRole = (String)tmpMap.get("receiverRole");
			
			String qry = "SELECT primary_key, created_date, receiver, receiver_class_name, sender, "
					+ " sender_class_name, message_id FROM eomessage where (receiver = "+userKey+" AND receiver_class_name = '"
					+role+ "' AND sender = "+receiver+" AND sender_class_name = '"+receiverRole+"') OR (receiver = "+receiver+" AND receiver_class_name = '"
					+receiverRole+ "' AND sender = "+userKey+" AND sender_class_name = '"+role+"')";
			
			List<HashMap<String, Object>> contactList = DBServices.getNativeQueryResult(qry);
			if(contactList == null || contactList.size() == 0){
				String messageID = "";
				if(role.equalsIgnoreCase("EOManagementUser")){
					messageID += userKey+"_MNG_";
				}
				else if(role.equalsIgnoreCase("EOTeacherUser")){
					messageID += userKey+"_TCH_";
				}
				else{
					messageID += userKey+"_STD_";
				}
				
				if(receiverRole.equalsIgnoreCase("EOManagementUser")){
					messageID += receiver+"_MNG";
				}
				else if(receiverRole.equalsIgnoreCase("EOTeacherUser")){
					messageID += receiver+"_TCH";
				}
				else if(receiverRole.equalsIgnoreCase("EOStudentUser")){
					messageID += receiver+"_STD";
				}
				
				DBServices.create(EOObject.createObject(VSPLUtil.getValueMap(
						"className~sender~receiver~senderClassName~receiverClassName~messageID",
						"EOMessage",userKey,receiver,role,receiverRole,messageID)));
				
				//For two way
				DBServices.create(EOObject.createObject(VSPLUtil.getValueMap(
						"className~sender~receiver~senderClassName~receiverClassName~messageID",
						"EOMessage",receiver,userKey,receiverRole,role,messageID)));
		
			}
			
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
	}
	
	@POST
	@Path("/createUserMessage")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUserMessage(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		DBServices.create(EOObject.createObject(map));
		return Response.status(201).entity(JSONUtil.objectToJson("Success")).build();
		
	}
	
	@POST
	@Path("/getMessageByUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMessageByUser(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String messageID = (String)map.get("messageID");
		String userKey = (String)map.get("userKey");
		
		/******For read status************************/
		String Qry = "Update EOMessageHistory set isRead = true where isRead = false AND  messageID = '" + messageID+"'"
				+ " AND receiver = "+userKey;
		DBServices.updateNativeQueryResult(Qry);
		
		/**************Message list for a User***************/
		List<Object> messageList = (List<Object>) DBServices.get("FROM EOMessageHistory where messageID = '"+messageID+"'");
		List<HashMap<String, Object>> resultMessageList = new ArrayList<>();
		for(Object obj : messageList){
			EOMessageHistory eoMessageHistory = (EOMessageHistory)obj;
			HashMap<String, Object> tmpMap = new HashMap<>();
			tmpMap.put("primaryKey", eoMessageHistory.primaryKey);
			tmpMap.put("message", eoMessageHistory.message);
			tmpMap.put("sender", eoMessageHistory.sender);
			tmpMap.put("receiver", eoMessageHistory.receiver);
			tmpMap.put("senderClassName", eoMessageHistory.senderClassName);
			tmpMap.put("receiverClassName", eoMessageHistory.receiverClassName);
			tmpMap.put("messageID", eoMessageHistory.messageID);
			tmpMap.put("isRead", eoMessageHistory.isRead);
			tmpMap.put("createdDateTime", eoMessageHistory.createdDateTime);
			if(eoMessageHistory.senderClassName.equalsIgnoreCase("EOStudentUser")){
				EOStudentUser eoStudentUser = (EOStudentUser)EOObject.getObjectByPK("EOStudentUser", (long)eoMessageHistory.sender+"");
				tmpMap.put("fullName", eoStudentUser.getFullName());
				if( eoStudentUser.eoImage != null){

					tmpMap.put("imageUrl", eoStudentUser.eoImage.imageUrl);
				}
			}
			
			else if(eoMessageHistory.senderClassName.equalsIgnoreCase("EOTeacherUser")){
				EOTeacherUser eoTeacherUser = (EOTeacherUser)EOObject.getObjectByPK("EOTeacherUser", (long)eoMessageHistory.sender+"");
				tmpMap.put("fullName", eoTeacherUser.getFullName());
				if(eoTeacherUser.eoImage != null){

					tmpMap.put("imageUrl", eoTeacherUser.eoImage.imageUrl);
				}
			}
			
			else if(eoMessageHistory.senderClassName.equalsIgnoreCase("EOManagementUser")){
				EOManagementUser eoManagementUser = (EOManagementUser)EOObject.getObjectByPK("EOManagementUser", (long)eoMessageHistory.sender+"");
				tmpMap.put("fullName", eoManagementUser.getFullName());
				if(eoManagementUser.eoImage != null){

					tmpMap.put("imageUrl", eoManagementUser.eoImage.imageUrl);
				}
			}
			resultMessageList.add(tmpMap);
			
		}
		
		return Response.status(201).entity(JSONUtil.objectToJson(resultMessageList)).build();
		
	}
	
	@POST
	@Path("/getHeaderMessageCount")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getHeaderMessageCount(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String userKey = (String)map.get("userKey");
		String qry = "Select count(primary_key) as count from eomessage_history where is_read = false and receiver = "+userKey;
		HashMap<String, Object> countMap = DBServices.getNativeQueryResult(qry).get(0);
	
		return Response.status(201).entity(JSONUtil.objectToJson(countMap.get("count"))).build();
		
	}

}
