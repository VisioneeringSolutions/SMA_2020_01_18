package com.vspl.music.services;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.jersey.api.json.JSONUnmarshaller;
import com.vspl.music.model.hp.HPMsgObject;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.JSONUtil;
import com.vspl.music.util.VSPLUtil;

public class ForgetPasswordMail {
	public static void composeMail(String mailBody, String subjectLine ,String email) {
		String text = "";

		text = "<p>Hello,</p></br>";

		text = text + "<p>" + mailBody + "</p></br>";
		List<String> recipientEmailList = new ArrayList<>();
	     recipientEmailList.add(email);
		//recipientEmailList.add("shalini.jiit@gmail.com");
		//recipientEmailList.add("aashwini2001@gmail.com");
		//recipientEmailList.add("zrkundan@gmail.com");
		//recipientEmailList.add("tanujkmr27@gmail.com");
		HashMap<String, Object> mailMap = new HashMap<>();
		mailMap.put("subjectLine", subjectLine);
		mailMap.put("description", text);
		sendforgetPasswordMail(mailMap, recipientEmailList);

	}

	public static void sendforgetPasswordMail(HashMap<String, Object> map, List<String> recipientEmailList) {
		HPMsgObject hpMsgObject = new HPMsgObject();
		hpMsgObject.id = "OUTSIDE";
		hpMsgObject.subject = (String) map.get("subjectLine");
		hpMsgObject.emailTmplStr = (String) map.get("description");
		DataUtil.factory().msgTemplData.msdIdObjectMap().put(hpMsgObject.id, hpMsgObject);
		EmailServices.sendEmail(VSPLUtil.getValueMap("toEmails~msgTemplID~clientLogo~greet~signature",
				recipientEmailList, "OUTSIDE", VSPLUtil.clientLogo, VSPLUtil.greet, VSPLUtil.signature));
		DataUtil.factory().msgTemplData.msdIdObjectMap().remove(hpMsgObject.id);

	}

}
