package com.vspl.music.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vspl.music.model.hp.HPMsgObject;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.VSPLUtil;

public class StudentRegistrationMail {
	public static void composeMail(String firstName ,String email) {
		String subjectLine = "Welcome to Suwayama";
		
		String text = "";

		text = "<p>Dear "+firstName+"</p></br>";

		text = text + "</br><p>You have been successfully Registered.</p></br><b>";
		List<String> recipientEmailList = new ArrayList<>();
	     recipientEmailList.add(email);
		
		HashMap<String, Object> mailMap = new HashMap<>();
		mailMap.put("subjectLine", subjectLine);
		mailMap.put("text", text);
		sendstudentRegistrationMail(mailMap, recipientEmailList);

	}

	public static void sendstudentRegistrationMail(HashMap<String, Object> map, List<String> recipientEmailList) {
		HPMsgObject hpMsgObject = new HPMsgObject();
		hpMsgObject.id = "REGISTRATION_MAIL";
		hpMsgObject.subject = (String) map.get("subjectLine");
		hpMsgObject.emailTmplStr = (String) map.get("text");
		DataUtil.factory().msgTemplData.msdIdObjectMap().put(hpMsgObject.id, hpMsgObject);
		EmailServices.sendEmail(VSPLUtil.getValueMap("toEmails~msgTemplID~clientLogo~greet~signature",
				recipientEmailList, "REGISTRATION_MAIL", VSPLUtil.clientLogo, VSPLUtil.greet, VSPLUtil.signatureSuwayama));
		DataUtil.factory().msgTemplData.msdIdObjectMap().remove(hpMsgObject.id);

	}

}
