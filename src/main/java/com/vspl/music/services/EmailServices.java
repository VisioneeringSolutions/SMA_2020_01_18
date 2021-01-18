package com.vspl.music.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.vspl.music.model.hp.HPMsgObject;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.VSPLUtil;

public class EmailServices {
	
	
	//public static final String fromEmailId = "visioneeringsolutions@gmail.com";
	//public static final String fromPassword = "gpntsoasegaohojm";
	
	@SuppressWarnings("unchecked")
	public static void sendEmail(Map<String, Object> valueMap) {
		
		final String fromEmailId = VSPLUtil.properties.getProperty(VSPLUtil.email_id);
		final String fromPassword = VSPLUtil.properties.getProperty(VSPLUtil.email_pass);
		
		System.out.println("fromEmailId:"+fromEmailId);
		System.out.println("fromPassword:"+fromPassword);
		
		Session session = Session.getDefaultInstance(getProperties(), new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmailId, fromPassword);
			}
		});
		try {
			List<String> toEmails = (List<String>) valueMap.get("toEmails");
			List<String> ccEmails = (List<String>) valueMap.get("ccEmails");
			List<String> bccEmails = (List<String>) valueMap.get("bccEmails");
			InternetAddress[] toEmailIds = new InternetAddress[toEmails.size()];
			for (int i = 0; i < toEmails.size(); i++) {
				toEmailIds[i] = new InternetAddress(toEmails.get(i));
			}
			InternetAddress[] ccEmailIds = null;
			InternetAddress[] bccEmailIds = null;
			if (ccEmails != null && ccEmails.size() > 0) {
				ccEmailIds = new InternetAddress[ccEmails.size()];
				for (int i = 0; i < ccEmails.size(); i++) {
					ccEmailIds[i] = new InternetAddress(ccEmails.get(i));
				}
			}
			if (bccEmails != null && bccEmails.size() > 0) {
				bccEmailIds = new InternetAddress[bccEmails.size()];
				for (int i = 0; i < bccEmails.size(); i++) {
					bccEmailIds[i] = new InternetAddress(bccEmails.get(i));
				}
			}
			Transport.send(mailObject(valueMap, session, toEmailIds, ccEmailIds, bccEmailIds));
			System.out.println("Mail Sent Successfully");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public static Properties getProperties() {
		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.socketFactory.port", "465");
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.port", "465");

		return prop;
	}

	public static Message mailObject(Map<String, Object> valueMap, Session session, InternetAddress[] toEmailIds,
			InternetAddress[] ccEmailIds, InternetAddress[] bccEmailIds) {
		//System.out.println("valueMap : "+valueMap);
		Message msg = new MimeMessage(session);
		HPMsgObject msgTempl = DataUtil.factory().msgTemplData.msdIdObjectMap()
				.get(String.valueOf(valueMap.get("msgTemplID")));

		String genericTemp = DataUtil.factory().messageFormat(null,
				DataUtil.factory().readFile(DataUtil.GEN_MAIL_TEMPL, true), valueMap);
		String msgTemplate = DataUtil.factory().messageFormat(null, msgTempl.emailTmplStr, valueMap);
		genericTemp = genericTemp.replace("#{bodyText}", msgTemplate);
		//System.out.println("GENERIC TEMP : " + genericTemp);

		/*genericTemp = genericTemp.replace("#{imagePath}",
				VSPLUtil.properties.getProperty(VSPLUtil.BASE_URL) + "/ImgData/NEMI/nemi_logo.png");*/

		/*genericTemp = genericTemp.replace("#{imagePath}", String.valueOf(valueMap.get("clientLogo")));
		System.out.println("clientLogo - " + valueMap.get("clientLogo"));*/

		if (valueMap.containsKey("greet")) {
			genericTemp = genericTemp.replace("#{greet}", String.valueOf(valueMap.get("greet")));
		}
		if (valueMap.containsKey("signature")) {
			genericTemp = genericTemp.replace("#{signature}", String.valueOf(valueMap.get("signature")));
		}
		try {
			msg.setRecipients(Message.RecipientType.TO, toEmailIds);
			if (ccEmailIds != null && ccEmailIds.length > 0) {
				msg.setRecipients(Message.RecipientType.CC, ccEmailIds);
			}
			if (bccEmailIds != null && bccEmailIds.length > 0) {
				msg.setRecipients(Message.RecipientType.BCC, bccEmailIds);
			}
			msg.setSubject(msgTempl.subject);
			// msg.setText(genericTemp);
			// msg.setContent(genericTemp, "text/html; charset=utf-8");
			MimeMultipart multipart = new MimeMultipart("related");
			BodyPart messageBodyPart = new MimeBodyPart();
			// String htmlText = "<H1>Hello</H1><img src=\"cid:image\">";
			messageBodyPart.setContent(genericTemp, "text/html; charset=utf-8");
			// add it
			multipart.addBodyPart(messageBodyPart);
			// second part (the image)
			/*if (valueMap.get("clientLogo") != null) {
				messageBodyPart = new MimeBodyPart();
				DataSource fds = new FileDataSource(String.valueOf(valueMap.get("clientLogo")));
				messageBodyPart.setDataHandler(new DataHandler(fds));
				messageBodyPart.setHeader("Content-ID", "<image>");
				// add image to the multipart
				multipart.addBodyPart(messageBodyPart);
			}*/
			// put everything together
			msg.setContent(multipart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	private static MimeMultipart addAttachments(MimeMultipart multipart, List<Object> attachments)
			throws MessagingException, IOException {
		if (attachments != null && attachments.size() > 0) {
			for (Object attachmentObj : attachments) {
				Map<String, Object> attachment = (Map<String, Object>) attachmentObj;
				BodyPart attachmentBodyPart = new MimeBodyPart();
				String detailStr = attachment.get("detail").toString();
				String detail = detailStr.substring(detailStr.indexOf(',') + 1);
				byte[] bytes = VSPLUtil.decodeBase64(detail);
				DataSource source = new ByteArrayDataSource(bytes, getValidMimeType((String) attachment.get("type")));
				attachmentBodyPart.setDataHandler(new DataHandler(source));
				// attachmentBodyPart.setFileName("Attach");
				attachmentBodyPart.setFileName((String) attachment.get("displayName"));
				multipart.addBodyPart(attachmentBodyPart);
			}
		}
		return multipart;
	}

	private static String getValidMimeType(String type) {
		if (type != null && "doc".equals(type)) {
			return "application/msword";
		} else if (type != null && "docx".equals(type)) {
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		} else if (type != null && "pdf".equals(type)) {
			return "application/pdf";
		} else if (type != null && "jpeg".equals(type)) {
			return "image/jpeg";
		} else if ("jpg".equals(type)) {
			return "image/jpg";
		} else if ("png".equals(type)) {
			return "image/png";
		}
		return "image/jpg";
	}

}