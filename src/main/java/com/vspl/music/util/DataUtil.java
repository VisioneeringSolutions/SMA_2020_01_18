package com.vspl.music.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vspl.music.model.hp.HPMsgTempl;
import com.vspl.music.services.SlotCreation;


public class DataUtil {

	public HPMsgTempl msgTemplData;
	public static final String GEN_MAIL_TEMPL = "data/genericMail.txt";
	public static final String EMAIL_BODY_TEXT = "data/emailTempl.json";
	public static final String TIME_SLOT = "data/timeSlotTempl.json";

	public HashMap<String, Object> timeSlotMap;
	
	private volatile static DataUtil factory;

	public static DataUtil factory() {
		if (factory == null) {
			synchronized (DataUtil.class) {
				if (factory == null) {
					factory = new DataUtil();
				}
			}
		}
		return factory;
	}

	public void loadData() {
		String jsonContent = this.readFile(DataUtil.EMAIL_BODY_TEXT, true);
		this.msgTemplData = (HPMsgTempl) JSONUtil.jsonToObject(jsonContent, HPMsgTempl.class);
		
		String jsonTimeSlotTempl = this.readFile(DataUtil.TIME_SLOT, true);
		this.timeSlotMap = (HashMap<String, Object>) JSONUtil.jsonToObject(jsonTimeSlotTempl, HashMap.class);
	}

	public String readFile(String fileName, boolean resourceFile) {
		try {
			File file;
			if (resourceFile) {
				ClassLoader classLoader = this.getClass().getClassLoader();
				file = new File(classLoader.getResource(fileName).getFile());
			} else {
				file = new File(fileName);
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}
				return sb.toString();
			} finally {
				fr.close();
				br.close();
			}
		} catch (Exception e) {
		}
		return "";
	}

	public String messageFormat(Matcher matcher, String messageString, Map<String, Object> replacements) {
		StringBuffer buffer = new StringBuffer();
		if (matcher == null) {
			Pattern pattern = Pattern.compile("\\{\\{(.+?)\\}\\}"); // Create a
																	// pattern
																	// for
																	// {{anyKey}}
			matcher = pattern.matcher(messageString);
		}
		while (matcher.find()) {
			Object replacement = replacements.get(matcher.group(1));
			if (replacement != null) {
				matcher.appendReplacement(buffer, "");
				buffer.append(replacement);
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	public static boolean boolValue(final Object v) {
		if (v == null) {
			return false;
		}

		if (v instanceof String) {
			if (v.equals("")) {
				return false;
			}

			String s = (String) v;
			char c0 = s.charAt(0);
			if ((c0 == 'N' || c0 == 'n') && s.equalsIgnoreCase("NO")) {
				return false;
			}
			if ((c0 == 'f' || c0 == 'F') && s.equalsIgnoreCase("false")) {
				return false;
			}
			if (c0 == 'u' || s.equalsIgnoreCase("undefined")) {
				return false;
			}
			if (s.length() == 1) {
				if (c0 == '0') {
					return false;
				}
				if (c0 == ' ') {
					return false;
				}
			}
			return true;
		}

		if (v instanceof Boolean) {
			return ((Boolean) v).booleanValue();
		}

		if (v instanceof Number) {
			return ((Number) v).intValue() != 0;
		}

		if (v instanceof Collection) {
			return ((Collection<?>) v).size() > 0;
		}

		return true;
	}
	
	public String deleteTableData(String queryStr){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session.createQuery(queryStr);
		query.executeUpdate();
		transaction.commit();
		session.close();
		return "Success";
	}
	public void startSlotCreationTimer(){
		TimerTask slotCreation = new SlotCreation();
		Timer timer = new Timer(false);
		//System.out.println("slotCreationTime::"+VSPLUtil.properties.getProperty("slotCreationTime"));
		//Date mailtime = DateUtil.setTimeToCurrentDate(VSPLUtil.properties.getProperty("slotCreationTime"));
		Date time = DateUtil.currentDate();
		//System.out.println("time::------"+time);
		//System.out.println("mailtime::"+mailtime);
		int days = 1;
		timer.scheduleAtFixedRate(slotCreation, time, 1000 * 60 * 60 * 24 * days);
	}

}
