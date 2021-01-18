package com.vspl.music.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vspl.music.model.hp.HPMsgObject;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.VSPLUtil;

public class SlotCreationMail {

	public static void composeMail(String mailBody, String subjectLine, List<String> recipientList){
		List<String> toEmail = new ArrayList<>();
		toEmail.add(VSPLUtil.properties.getProperty(VSPLUtil.to_email));
		
		System.out.println("toEmail:"+toEmail);
		
		String text = "<p>Hello,</p><br>";
		text += "<p>"+mailBody+"</p>";
		
		HashMap<String, Object> mailMap = new HashMap<>();
		mailMap.put("subjectLine", subjectLine);
		mailMap.put("description", text);
		sendSlotCreationMail(mailMap,toEmail, recipientList);
	}
	
	public static void sendSlotCreationMail(HashMap<String, Object> map,List<String> toEmail, List<String> recipientEmailList) {
		HPMsgObject hpMsgObject = new HPMsgObject();
		hpMsgObject.id = "SLOTEMAIL";
		hpMsgObject.subject = (String) map.get("subjectLine");
		hpMsgObject.emailTmplStr = (String) map.get("description");
		DataUtil.factory().msgTemplData.msdIdObjectMap().put(hpMsgObject.id, hpMsgObject);
		EmailServices.sendEmail(VSPLUtil.getValueMap("toEmails~bccEmails~msgTemplID~clientLogo~greet~signature",
				toEmail,recipientEmailList, "SLOTEMAIL", VSPLUtil.clientLogo, VSPLUtil.greet, VSPLUtil.signatureSuwayama));
		DataUtil.factory().msgTemplData.msdIdObjectMap().remove(hpMsgObject.id);

	}
}
