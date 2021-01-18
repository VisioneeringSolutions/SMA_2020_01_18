package com.vspl.music.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.vspl.music.model.eo.EOLoginDetails;
import com.vspl.music.model.eo.EOManagementUser;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.hp.HPLogin;
import com.vspl.music.services.DBServices;
import com.vspl.music.services.ForgetPasswordMail;
import com.vspl.music.util.JSONUtil;
import com.vspl.music.util.VSPLUtil;
import com.vspl.sessionmgmt.VSPLBaseReqRespObject;
import com.vspl.sessionmgmt.VSPLReqResThreadFactory;
import com.vspl.sessionmgmt.VSPLSessionObject;

@Path("/auth")
public class AuthController {

	/*
	 * @POST
	 * 
	 * @Path("/loginUser")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON) public Response
	 * loginUser(HashMap<String, String> map) { String usrName =
	 * map.get("usrName"); String password = map.get("password");
	 * //System.out.println("map::"+map); String url = "failure"; boolean
	 * isEmail = (usrName.indexOf('@') != -1) ? true : false; String qryStr =
	 * "";
	 * 
	 * String qryStr =
	 * "SELECT s.primary_key, s.email, s.first_name, concat(s.first_name, ' ',  s.last_name) as full_name, "
	 * + " s.password" + " FROM eoteacher_user s WHERE "; if (isEmail) qryStr +=
	 * " s.email = '" + usrName + "'"; else{
	 * if(usrName.matches("^[A-Za-z0-9]+(?:[_-][A-Za-z0-9]+)*$")){ qryStr +=
	 * " s.username = '" + usrName+ "'"; }else{ qryStr += " s.phone = '" +
	 * usrName+ "'"; } }
	 * 
	 * 
	 * List<HashMap<String, Object>> list = new ArrayList<>(); if (list.size()
	 * == 0) { qryStr =
	 * "SELECT s.primary_key, s.email, s.first_name, concat(s.first_name, ' ',  s.last_name) as full_name, "
	 * + " s.password" + " FROM eomanagement_user s WHERE "; if (isEmail) qryStr
	 * += " s.email = '" + usrName + "'"; else{
	 * if(usrName.matches("^[a-zA-Z0-9]+$")){ qryStr += " s.username = '" +
	 * usrName+ "'"; }else{ qryStr += " s.phone = '" + usrName+ "'"; } } list =
	 * DBServices.getNativeQueryResult(qryStr);
	 * 
	 * if(list.size() == 0){ return
	 * Response.status(401).entity(JSONUtil.objectToJson(url)).build(); }
	 * 
	 * } HashMap<String, Object> userMap = list.get(0); url =
	 * this.pswdAuthentication(userMap, usrName, password); // Object obj =
	 * DBServices.authenticateUser(className, usrName, // password); if
	 * (url.equals("failure")) { return
	 * Response.status(401).entity(JSONUtil.objectToJson(url)).build(); }
	 * 
	 * 
	 * HPLogin hpLogin = new HPLogin(url, usrName); hpLogin.firstName = (String)
	 * userMap.get("first_name"); hpLogin.userKey = ((BigInteger)
	 * userMap.get("primary_key")).longValue(); //System.out.println(
	 * "HPLogin : " + hpLogin);
	 * this.getSessionObj().setAuthenticatedUser((String)userMap.get("full_name"
	 * )); return
	 * Response.status(201).entity(JSONUtil.objectToJson(hpLogin)).build(); }
	 */

	@POST
	@Path("/loginUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(HashMap<String, String> map) {
		String usrName = map.get("usrName");
		String password = map.get("password");
		//System.out.println("map::" + JSONUtil.objectToJson(map));
		String url = "failure";
		boolean isEmail = (usrName.indexOf('@') != -1) ? true : false;
		String qryStr = "", userInfoQuery = "", role = null;

		List<HashMap<String, Object>> list = new ArrayList<>();

		if (!isEmail) {

			userInfoQuery = "Select l.role from eologin_details l where l.user_name= '" + usrName + "' OR  l.user_name_2='"+usrName+"'";

			List<HashMap<String, Object>> objList = null;

			objList = DBServices.getNativeQueryResult(userInfoQuery);

			if (objList.size() > 0) {
				HashMap<String, Object> dataMap = objList.get(0);
				role = String.valueOf(dataMap.get("role"));

				if (role.equalsIgnoreCase("EOTeacherUser")) { // teacher login

					// for teacher----------
					qryStr = "Select t.primary_key, t.email, t.first_name, concat(t.first_name,'',t.last_name) as full_name, "
							+ " l.user_name,l.password, l.role, t.teacher_id as id,i.image_url"
							+ " From eoteacher_user t INNER JOIN  eologin_details l  ON t.eologindetails_primary_key= l.primary_key "
							+ " LEFT JOIN eoimage i ON i.primary_key = t.eoimage_primary_key WHERE "
							+ " l.user_name = '" + usrName + "' OR  l.user_name_2='"+usrName+"'";

					// System.out.println("qryStr::TEACHER:"+qryStr);
				}

				if (role.equalsIgnoreCase("EOStudentUser")) { // student login

					qryStr = "Select s.primary_key, s.email, s.first_name, concat(s.first_name,'',s.last_name) as full_name, "
							+ " l.user_name,l.password, l.role, s.student_id as id ,i.image_url"
							+ " From eostudent_user s INNER JOIN  eologin_details l  ON s.eologindetails_primary_key= l.primary_key "
							+ " LEFT JOIN eoimage i ON i.primary_key = s.eoimage_primary_key WHERE "
							+ " l.user_name = '" + usrName + "' OR  l.user_name_2='"+usrName+"'";
					//System.out.println("qryStr::student:" + qryStr);
				}

			}
		} else {
			//System.out.println();
			qryStr = "SELECT s.primary_key, s.email, s.first_name, concat(s.first_name, ' ',  s.last_name) as full_name, "
					+ "s.address_1, s.address_2, s.phone,i.image_url,"
					+ " s.password" + " FROM eomanagement_user s LEFT JOIN eoimage i ON i.primary_key = s.eoimage WHERE ";
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

		if (list.get(0).get("role") == null) {
			list.get(0).put("role", "EOManagementUser");
			role = String.valueOf(list.get(0).get("role"));
		}

		if (list.size() == 0) {
			return Response.status(401).entity(JSONUtil.objectToJson(url)).build();
		}

		HashMap<String, Object> userMap = list.get(0);
		url = this.pswdAuthentication(userMap, usrName, password, role);

		if (url.equals("failure")) {
			return Response.status(401).entity(JSONUtil.objectToJson(url)).build();
		}

		HPLogin hpLogin = new HPLogin(url, usrName);
		hpLogin.firstName = (String) userMap.get("first_name");
		hpLogin.userKey = ((BigInteger) userMap.get("primary_key")).longValue();
		hpLogin.role = (String) userMap.get("role");
		hpLogin.imageUrl = (String) userMap.get("image_url");
		if(userMap.get("address_1") != null){
			hpLogin.address1 = (String) userMap.get("address_1");
		}
		if(userMap.get("address_2") != null){
			hpLogin.address2 = (String) userMap.get("address_2");
		}
		if(userMap.get("phone") != null){
			hpLogin.phone = (String) userMap.get("phone");
		}
		// System.out.println("userMap.get::"+userMap.get("id"));
		hpLogin.id = (String) userMap.get("id");

		this.getSessionObj().setAuthenticatedUser((String) userMap.get("full_name"));
		return Response.status(201).entity(JSONUtil.objectToJson(hpLogin)).build();

	}

	@POST
	@Path("/forgetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response forgetPassword(HashMap<String, String> map) {

		return Response.status(201).entity(JSONUtil.objectToJson("noRecord")).build();

	}

	private String pswdAuthentication(HashMap<String, Object> userDbMap, String usrName, String password, String role) {
		if (VSPLUtil.md5Encrypt(password).equals((String) userDbMap.get("password"))) {
			//QA and LOCAL
			if (role.equalsIgnoreCase("EOManagementUser")) {
				return VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) + "/SMA/home#/dashboardadmin";
			}
			if (role.equalsIgnoreCase("EOStudentUser")) {
				return VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) + "/SMA/home#/dashboardstudent";
			}
			if (role.equalsIgnoreCase("EOTeacherUser")) {
				return VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) + "/SMA/home#/dashboardteacher";
			}

			// return VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) +
			// "/SMA/home#/";
			
			
			//PROD
			/*if (role.equalsIgnoreCase("EOManagementUser")) {
				return VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) + "/home#/dashboardadmin";
			}
			if (role.equalsIgnoreCase("EOStudentUser")) {
				return VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) + "/home#/dashboardstudent";
			}
			if (role.equalsIgnoreCase("EOTeacherUser")) {
				return VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) + "/home#/dashboardteacher";
			}*/

		}
		return "failure";
	}

	@POST
	@Path("/resetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPassword(HashMap<String, String> map) {

		//System.out.println("map change pass::"+map);
		
		String className = (String) map.get("className");
		
		String encryptedCurrPass = VSPLUtil.md5Encrypt(map.get("currentPassword"));
		String confirmPassword = "";
		
		String respResult = "";
		boolean flag = false;
		if(map.get("newPassword").equalsIgnoreCase(map.get("confirmPassword"))){
			flag = true;
			confirmPassword = VSPLUtil.md5Encrypt(map.get("confirmPassword"));
		}
		
		//System.out.println("encryptedPass::"+encryptedCurrPass);
		
		if(className.equalsIgnoreCase("EOManagementUser")){
			
			List<Object> dataList = DBServices.get("FROM EOManagementUser WHERE primaryKey = "+map.get("userKey"));
			
			if(dataList != null && dataList.size() > 0 && flag){
				EOManagementUser eoManagementUser = (EOManagementUser) dataList.get(0);
				
				if(((String)(eoManagementUser.password)).equalsIgnoreCase(encryptedCurrPass) && flag){
					String updateQry = "UPDATE EOManagementUser SET password = '"+confirmPassword+"' WHERE primary_key = "+map.get("userKey");
					DBServices.updateNativeQueryResult(updateQry);
					respResult = "Success";
				}else {
					respResult = "New Password and Confirm password does not match OR current password is incorrect";
				}
			}
			else{
				respResult = "Failure";
			}
			
		}else{
			String query ="";
			if(className.equalsIgnoreCase("EOStudentUser")){
				query = "SELECT eoLoginDetails_primary_key FROM eostudent_user WHERE primary_key = '"+map.get("userKey") +"'";
			}
			if(className.equalsIgnoreCase("EOTeacherUser")){
				query = "SELECT eoLoginDetails_primary_key FROM eoteacher_user WHERE primary_key = '"+map.get("userKey") +"'";
			}
			
			List<HashMap<String, Object>> userData = DBServices.getNativeQueryResult(query);
			
			if(userData != null && userData.size() > 0){
				
				EOObject eoObject = EOObject.getObjectByPK("EOLoginDetails", userData.get(0).get("eologindetails_primary_key")+"");
				EOLoginDetails eoLoginDetails = (EOLoginDetails) eoObject;
				
				if(((String)(eoLoginDetails.password)).equalsIgnoreCase(encryptedCurrPass) && flag){
					
					//System.out.println("trueeeeee");
					HashMap<String, Object> loginMap = new HashMap<>();
					loginMap.put("password", confirmPassword);
					
					DBServices.update(EOObject.updateObject(eoObject,loginMap));
					respResult = "Success";;
				}
				else{
					respResult = "New Password and Confirm password does not match OR current password is incorrect";
				}
			}
		};
		
		return Response.status(201).entity(JSONUtil.objectToJson(respResult)).build();
	}

	@POST
	@Path("/getLKObject")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getLKObject(HashMap<String, String> map) {

		String className = (String) map.get("className");
		List<Object> resultList = DBServices.get("From " + className + " where IS_ACTIVE = true ORDER BY PRIMARY_KEY");
		return Response.status(201).entity(JSONUtil.objectToJson(resultList)).build();

	}

	public VSPLSessionObject getSessionObj() {
		VSPLBaseReqRespObject reqResObj = VSPLReqResThreadFactory.factory().getReqRespObject();
		return reqResObj == null ? null : reqResObj.sessionObject;
	}

	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logoutUser(HashMap<String, String> map) {
		String userPK = map.get("userPK");
		String tableName = map.get("className");

		if (userPK != null) {
			DBServices.updateNativeQueryResult(
					"Update " + tableName + " Set device_token = null where primary_key = " + userPK);
		}
		if (this.getSessionObj() == null) {
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED)
					.entity(JSONUtil.objectToJson("Your session expired as you logged out from application")).build();
		}
		this.getSessionObj().sessionDestroyed();
		return Response.status(HttpServletResponse.SC_OK).entity(JSONUtil.objectToJson("Successfully logged out"))
				.build();

	}

	@POST
	@Path("/forgotPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response forgotPassword(HashMap<String, String> map) {
		String usrName = map.get("usrName");
		String mail = (String) map.get("description");
		String subjectLine = "VSPL New Password Request";
		// System.out.println("map::"+JSONUtil.objectToJson(map));
		boolean isEmail = (usrName.indexOf('@') != -1) ? true : false;
		String qryStr = "", userInfoQuery = "", role = null;
		List<HashMap<String, Object>> list = new ArrayList<>();
		String tempPswd = VSPLUtil.getRandomPswd();
		String password = VSPLUtil.md5Encrypt(tempPswd);
		String mailBody = mail + " new password  = " + tempPswd + "";

		if (!isEmail) {

			userInfoQuery = "Select l.role from eologin_details l where l.user_name= '" + usrName + "' ";
			List<HashMap<String, Object>> objList = null;
			objList = DBServices.getNativeQueryResult(userInfoQuery);

			if (objList.size() > 0) {

				HashMap<String, Object> dataMap = objList.get(0);
				role = String.valueOf(dataMap.get("role"));

				if (role.equalsIgnoreCase("EOTeacherUser")) {

					qryStr = "Select t.primary_key, t.email, t.first_name, concat(t.first_name,'',t.last_name) as full_name, "
							+ " l.user_name,l.password, l.role, t.teacher_id as id"
							+ " From eoteacher_user t INNER JOIN  eologin_details l  ON t.eologindetails_primary_key= l.primary_key WHERE "
							+ " l.user_name = '" + usrName + "' ";
					list = DBServices.getNativeQueryResult(qryStr);

					if (list.size() > 0 && list != null) {

						String email = list.get(0).get("email").toString();
						String updateQuery = "Update EOLoginDetails set password = '" + password
								+ "' where userName = '" + usrName + "' ";
						DBServices.updateNativeQueryResult(updateQuery);
						ForgetPasswordMail.composeMail(mailBody, subjectLine, email);
					}
				}

				if (role.equalsIgnoreCase("EOStudentUser")) {

					qryStr = "Select s.primary_key, s.email, s.first_name, concat(s.first_name,'',s.last_name) as full_name, "
							+ " l.user_name,l.password, l.role, s.student_id as id"
							+ " From eostudent_user s INNER JOIN  eologin_details l  ON s.eologindetails_primary_key= l.primary_key WHERE "
							+ " l.user_name = '" + usrName + "' ";

					list = DBServices.getNativeQueryResult(qryStr);
					if (list.size() > 0 && list != null) {

						String email = list.get(0).get("email").toString();
						String updateQuery = "Update EOLoginDetails set password = '" + password
								+ "' where userName = '" + usrName + "' ";
						DBServices.updateNativeQueryResult(updateQuery);
						ForgetPasswordMail.composeMail(mailBody, subjectLine, email);

					}
				}
			}
		} else {

			qryStr = "SELECT s.primary_key, s.email, s.first_name, concat(s.first_name, ' ',  s.last_name) as full_name, "
					+ " s.password" + " FROM eomanagement_user s WHERE ";

			if (isEmail) {
				qryStr += " s.email = '" + usrName + "'";
				list = DBServices.getNativeQueryResult(qryStr);
				if (list.size() > 0 && list != null) {

					String email = list.get(0).get("email").toString();
					String updateQuery = "Update EOManagementUser set password = '" + password + "' where email = '"
							+ usrName + "' ";
					DBServices.updateNativeQueryResult(updateQuery);
					ForgetPasswordMail.composeMail(mailBody, subjectLine, email);
				}

			} else {

				if (usrName.matches("^[a-zA-Z0-9]+$")) {
					qryStr += " s.username = '" + usrName + "'";
					list = DBServices.getNativeQueryResult(qryStr);
					if (list.size() > 0 && list != null) {
						//
						String email = list.get(0).get("email").toString();
						String updateQuery = "Update EOManagementUser set password = '" + password + "' where email = '"
								+ usrName + "' ";
						DBServices.updateNativeQueryResult(updateQuery);
						ForgetPasswordMail.composeMail(mailBody, subjectLine, email);
					}
				} else {
					qryStr += " s.phone = '" + usrName + "'";
					list = DBServices.getNativeQueryResult(qryStr);
					if (list.size() > 0 && list != null) {

						String email = list.get(0).get("email").toString();
						String updateQuery = "Update EOManagementUser set password = '" + password + "' where email = '"
								+ usrName + "' ";
						DBServices.updateNativeQueryResult(updateQuery);
						ForgetPasswordMail.composeMail(mailBody, subjectLine, email);

					}
				}
			}
		}

		if (list.size() > 0 && list != null) {
			return Response.status(201).entity(JSONUtil.objectToJson(list)).build();
		}else{
			return Response.status(201).entity(JSONUtil.objectToJson("No Data")).build();
		}
	}
	
	
	@POST
	@Path("/resetPasswordByAdmin")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPasswordByAdmin(HashMap<String, String> map) {
		
		// Generating random 5 char length password
		String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	String password = "";
    	String temp = "";
    	for(int l = 0; l < 2; l++) {
    		temp += abc.charAt(new Random().nextInt(abc.length()));
	    }
    	temp += 1000 + new Random().nextInt(90000);
    	password = temp.substring(0, 5);
    	String qry = "SELECT u.primary_key, u.eologindetails_primary_key FROM "+map.get("className") +" u WHERE primary_key = "+map.get("primaryKey");
    	HashMap<String, Object> userMap = DBServices.getNativeQueryResult(qry).get(0);
    	String encPass = VSPLUtil.md5Encrypt(password);
    	String updateQry = "UPDATE EOLoginDetails SET password='" +encPass+ "' WHERE primary_key = "+userMap.get("eologindetails_primary_key");
    	DBServices.updateNativeQueryResult(updateQry);
    	
		return Response.status(201).entity(JSONUtil.objectToJson(password)).build();
	}
}
