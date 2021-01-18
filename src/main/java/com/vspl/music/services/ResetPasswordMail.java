package com.vspl.music.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.jersey.api.json.JSONUnmarshaller;
import com.vspl.music.model.hp.HPMsgObject;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.JSONUtil;
import com.vspl.music.util.VSPLUtil;

public class ResetPasswordMail {
	
	public static void composeMail(String firstName,String email,String subjectLine, String otp) {
		String text = "";
		String mailBody = "Your one time password (OTP) is : "+otp;

		text = "<p>Dear "+ firstName+",</p></br>";

		text = text + "</br><p>We just received a request to reset the password of your Nemi Account.</p></br><p><b>" + mailBody + "</p></br>";

		List<String> recipientEmailList = new ArrayList<>();
		//recipientEmailList.add("mishrarashmi267@gmail.com");
		//recipientEmailList.add("shalini.jiit@gmail.com");
		//recipientEmailList.add("aashwini2001@gmail.com");
		//recipientEmailList.add("zrkundan@gmail.com");
		recipientEmailList.add(email);
		HashMap<String, Object> mailMap = new HashMap<>();
		mailMap.put("subjectLine", subjectLine);
		mailMap.put("description", text);
		sendResetPasswordMail(mailMap, recipientEmailList);

	}
	
	public static void sendResetPasswordMail(HashMap<String, Object> map, List<String> recipientEmailList){
		HPMsgObject hpMsgObject = new HPMsgObject();
		System.out.println("hpMsgObject : "+JSONUtil.objectToJson(hpMsgObject));
		hpMsgObject.id = "FORGOT_PASSWORD";
		hpMsgObject.subject = (String)map.get("subjectLine");
		hpMsgObject.emailTmplStr = (String)map.get("description");
		
		DataUtil.factory().msgTemplData.msdIdObjectMap().put(hpMsgObject.id, hpMsgObject);
		EmailServices.sendEmail(VSPLUtil.getValueMap("toEmails~msgTemplID~greet~signature",
				recipientEmailList, "FORGOT_PASSWORD",  VSPLUtil.greet, VSPLUtil.signature));
		DataUtil.factory().msgTemplData.msdIdObjectMap().remove(hpMsgObject.id);
	}


}
