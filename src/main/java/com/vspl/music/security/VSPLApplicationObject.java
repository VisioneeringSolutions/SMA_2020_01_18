package com.vspl.music.security;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vspl.music.model.eo.EOImage;
import com.vspl.music.model.eo.EOManagementUser;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.DataUtil;
import com.vspl.music.util.EOObjectUtil;
import com.vspl.music.util.HibernateUtil;
import com.vspl.music.util.VSPLUtil;

public class VSPLApplicationObject {

	public static Logger logger = LoggerFactory.getLogger(VSPLApplicationObject.class);
	private volatile static VSPLApplicationObject factory;

	public static VSPLApplicationObject factory() {
		if (factory == null) {
			synchronized (VSPLApplicationObject.class) {
				if (factory == null) {
					factory = new VSPLApplicationObject();
				}
			}
		}
		return factory;
	}

	public void startApplication() throws ParseException {

		this.loadApplicationSetups();
		prerequisiteData();
		

		// absentMail();
	}

	private void loadApplicationSetups() {
		VSPLUtil.loadFileConfiguration();
		logger.info("IN APPLICATION STARTUP");
		HibernateUtil.getSessionFactory();
		logger.info("DB LOADED...");
		EOObjectUtil.LoadAllEntityClassJson();
		DataUtil.factory().loadData();
		
		DataUtil.factory().startSlotCreationTimer();
	}
	
	public void prerequisiteData() throws ParseException {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		String queryStr1 = "From LKSecurityQuestion";
		List<Object> LKSecurityQuestionList = DBServices.get(queryStr1);
		if (LKSecurityQuestionList.size() == 0) {
			SQLQuery insertQuery1 = session.createSQLQuery(
					"INSERT INTO lksecurity_question(PRIMARY_KEY, QUESTION, IS_ACTIVE) VALUES (1,'What is your pet name?',true)");
			SQLQuery insertQuery2 = session.createSQLQuery(
					"INSERT INTO lksecurity_question(PRIMARY_KEY, QUESTION, IS_ACTIVE) VALUES (2,'What was your childhood nickname?',true)");
			SQLQuery insertQuery3 = session.createSQLQuery(
					"INSERT INTO lksecurity_question(PRIMARY_KEY, QUESTION, IS_ACTIVE) VALUES (3,'On what street did you live as a child?',true)");
			SQLQuery insertQuery4 = session.createSQLQuery(
					"INSERT INTO lksecurity_question(PRIMARY_KEY, QUESTION, IS_ACTIVE) VALUES (4,'What is the name of your favorite childhood friend?',true)");
			SQLQuery insertQuery5 = session.createSQLQuery(
					"INSERT INTO lksecurity_question(PRIMARY_KEY, QUESTION, IS_ACTIVE) VALUES (5,'In what city does your nearest sibling live?',true)");
			
			insertQuery1.executeUpdate();
			insertQuery2.executeUpdate();
			insertQuery3.executeUpdate();
			insertQuery4.executeUpdate();
			insertQuery5.executeUpdate();
			

		}
		
		String queryStr2 = "From LKBusinessType";
		List<Object> LKBusinessTypeList = DBServices.get(queryStr2);
		if (LKBusinessTypeList.size() == 0) {
			SQLQuery insertQuery1 = session.createSQLQuery(
					"INSERT INTO lkbusiness_type(PRIMARY_KEY, BUSINESS_TYPE, IS_ACTIVE) VALUES (1,'Sole Proprietorship',true)");
			SQLQuery insertQuery2 = session.createSQLQuery(
					"INSERT INTO lkbusiness_type(PRIMARY_KEY, BUSINESS_TYPE, IS_ACTIVE) VALUES (2,'A Partnership',true)");
			SQLQuery insertQuery3 = session.createSQLQuery(
					"INSERT INTO lkbusiness_type(PRIMARY_KEY, BUSINESS_TYPE, IS_ACTIVE) VALUES (3,'A Corporation',true)");
			SQLQuery insertQuery4 = session.createSQLQuery(
					"INSERT INTO lkbusiness_type(PRIMARY_KEY, BUSINESS_TYPE, IS_ACTIVE) VALUES (4,'Limited Liability Company',true)");
			
			insertQuery1.executeUpdate();
			insertQuery2.executeUpdate();
			insertQuery3.executeUpdate();
			insertQuery4.executeUpdate();
		}
		
		/*String queryStr3 = "From LKMusicType";
		List<Object> LKMusicTypeList = DBServices.get(queryStr3);
		if (LKMusicTypeList.size() == 0) {
			SQLQuery insertQuery1 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (1,'Violin',true)");
			SQLQuery insertQuery2 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (2,'Piono',true)");
			SQLQuery insertQuery3 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (3,'Hardanger violin',true)");
			SQLQuery insertQuery4 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (4,'Music theory',true)");		
			SQLQuery insertQuery5 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (5,'Pop Piano',true)");
			SQLQuery insertQuery6 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (6,'Clarinet',true)");
			SQLQuery insertQuery7 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (7,'Ensemble Course',true)");
			SQLQuery insertQuery8 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (8,'Salon Concert',true)");
			SQLQuery insertQuery9 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (9,'Bunne Method',true)");
			SQLQuery insertQuery10 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (10,'Singing & Recording',true)");
			SQLQuery insertQuery11 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (11,'Music Programming',true)");
			SQLQuery insertQuery12 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (12,'Robot Programming',true)");
			SQLQuery insertQuery13 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (13,'Others',true)");
			SQLQuery insertQuery14 = session.createSQLQuery(
					"INSERT INTO lkmusic_type(PRIMARY_KEY, MUSIC_TYPE, IS_ACTIVE) VALUES (14,'Music Composition',true)");
			
			insertQuery1.executeUpdate();
			insertQuery2.executeUpdate();
			insertQuery3.executeUpdate();
			insertQuery4.executeUpdate();
			insertQuery5.executeUpdate();
			insertQuery6.executeUpdate();
			insertQuery7.executeUpdate();
			insertQuery8.executeUpdate();
			insertQuery9.executeUpdate();
			insertQuery10.executeUpdate();
			insertQuery11.executeUpdate();
			insertQuery12.executeUpdate();
			insertQuery13.executeUpdate();
			insertQuery14.executeUpdate();
		}*/
		
		/*String queryStr4 = "From LKCategoryType";
		List<Object> LKCategoryTypeList = DBServices.get(queryStr4);
		if (LKCategoryTypeList.size() == 0) {
			SQLQuery insertQuery1 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (1,'A','Beginner',true)");
			SQLQuery insertQuery2 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (2,'B','Intermediate',true)");
			SQLQuery insertQuery3 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (3,'C','Advanced',true)");
			SQLQuery insertQuery4 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (4,'D','Professional',true)");
			SQLQuery insertQuery5 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (5,'E','Enjoy',true)");
			SQLQuery insertQuery6 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (6,'F','Free',true)");
			SQLQuery insertQuery7 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (7,'S','Senior',true)");
			SQLQuery insertQuery8 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (8,'T','Trial',true)");
			SQLQuery insertQuery9 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (9,'O','Others',true)");
			SQLQuery insertQuery10 = session.createSQLQuery(
					"INSERT INTO lkcategory_type(PRIMARY_KEY, CATEGORY_TYPE, DESCRIPTION, IS_ACTIVE) VALUES (10,'Z','All',true)");
			
			
			insertQuery1.executeUpdate();
			insertQuery2.executeUpdate();
			insertQuery3.executeUpdate();
			insertQuery4.executeUpdate();
			insertQuery5.executeUpdate();
			insertQuery6.executeUpdate();
			insertQuery7.executeUpdate();
			insertQuery8.executeUpdate();
			insertQuery9.executeUpdate();
			insertQuery10.executeUpdate();
		}*/
		
		String queryStr5 = "From LKClassDuration";
		List<Object> LKClassDurationList = DBServices.get(queryStr5);
		if (LKClassDurationList.size() == 0) {
			SQLQuery insertQuery1 = session.createSQLQuery(
					"INSERT INTO lkclass_duration(PRIMARY_KEY, DURATION, IS_ACTIVE) VALUES (1,'30 mins',true)");
			SQLQuery insertQuery2 = session.createSQLQuery(
					"INSERT INTO lkclass_duration(PRIMARY_KEY, DURATION, IS_ACTIVE) VALUES (2,'45 mins',true)");
			SQLQuery insertQuery3 = session.createSQLQuery(
					"INSERT INTO lkclass_duration(PRIMARY_KEY, DURATION, IS_ACTIVE) VALUES (3,'60 mins',true)");
			SQLQuery insertQuery4 = session.createSQLQuery(
					"INSERT INTO lkclass_duration(PRIMARY_KEY, DURATION, IS_ACTIVE) VALUES (4,'75 mins',true)");
			SQLQuery insertQuery5 = session.createSQLQuery(
					"INSERT INTO lkclass_duration(PRIMARY_KEY, DURATION, IS_ACTIVE) VALUES (5,'90 mins',true)");
			SQLQuery insertQuery6 = session.createSQLQuery(
					"INSERT INTO lkclass_duration(PRIMARY_KEY, DURATION, IS_ACTIVE) VALUES (6,'120 mins',true)");
			
			insertQuery1.executeUpdate();
			insertQuery2.executeUpdate();
			insertQuery3.executeUpdate();
			insertQuery4.executeUpdate();
			insertQuery5.executeUpdate();
			insertQuery6.executeUpdate();
		}
		
		
		String queryStr6 = "From LKColor";
		List<Object> LKColorList = DBServices.get(queryStr6);
		if (LKColorList.size() == 0) {
			SQLQuery insertQuery1 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (1,'#827717',true)");
			SQLQuery insertQuery2 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (2,'#BCAAA4',true)");
			SQLQuery insertQuery3 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (3,'#FFAB91',true)");
			SQLQuery insertQuery4 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (4,'#FF5722',true)");
			SQLQuery insertQuery5 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (5,'#FFCC80',true)");
			SQLQuery insertQuery6 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (6,'#795548',true)");
			SQLQuery insertQuery7 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (7,'#FFEE58',true)");
			SQLQuery insertQuery8 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (8,'#DCE775',true)");
			SQLQuery insertQuery9 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (9,'#9CCC65',true)");
			SQLQuery insertQuery10 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (10,'#C8E6C9',true)");
			SQLQuery insertQuery11 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (11,'#4DB6AC',true)");
			SQLQuery insertQuery12 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (12,'#00897B',true)");
			SQLQuery insertQuery13 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (13,'#80DEEA',true)");
			SQLQuery insertQuery14 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (14,'#29B6F6',true)");
			SQLQuery insertQuery15 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (15,'#E1BEE7',true)");
			SQLQuery insertQuery16 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (16,'#5C6BC0',true)");
			SQLQuery insertQuery17 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (17,'#BA68C8',true)");
			SQLQuery insertQuery18 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (18,'#F06292',true)");
			SQLQuery insertQuery19 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (19,'#E91E63',true)");
			SQLQuery insertQuery20 = session.createSQLQuery(
					"INSERT INTO lkcolor(PRIMARY_KEY, COLOR_CODE, IS_ACTIVE) VALUES (20,'#E0E0E0',true)");
			
			insertQuery1.executeUpdate();
			insertQuery2.executeUpdate();
			insertQuery3.executeUpdate();
			insertQuery4.executeUpdate();
			insertQuery5.executeUpdate();
			insertQuery6.executeUpdate();
			insertQuery7.executeUpdate();
			insertQuery8.executeUpdate();
			insertQuery9.executeUpdate();
			insertQuery10.executeUpdate();
			insertQuery11.executeUpdate();
			insertQuery12.executeUpdate();
			insertQuery13.executeUpdate();
			insertQuery14.executeUpdate();
			insertQuery15.executeUpdate();
			insertQuery16.executeUpdate();
			insertQuery17.executeUpdate();
			insertQuery18.executeUpdate();
			insertQuery19.executeUpdate();
			insertQuery20.executeUpdate();
		}
		
		session.getTransaction().commit();
		session.close();
		
		createAdminObject();
	}

	public void closeApplication() {
		logger.info("IN APPLICATION CLOSE");
		HibernateUtil.closeSessionFactory();
	}

	private void createAdminObject() {

		String queryStr3 = "From EOManagementUser";
		List<Object> staffList = DBServices.get(queryStr3);
		if (staffList.size() == 0) {

			HashMap<String, Object> map = new HashMap<>();
			map.put("prefix", "Mr.");
			map.put("firstName", "Admin");
			map.put("gender", "Male");
			map.put("email", "demoadmin@visioneering.com");
			map.put("phone", "9999999999");

			map.put("password", "f36136ae5e5863d813353c795f15cbf4");

			map.put("className", "EOManagementUser");
			String className = "EOManagementUser";

			EOImage eoImg = null;
			if (map.get("eoImage") == null) {// If image pk null then create
												// default
												// img
				eoImg = VSPLUtil.getAvitarImgForMale(className);
				map.put("eoImage", new Long(eoImg.primaryKey).intValue());
			}
			eoImg = (EOImage) EOObject.getObjectByPK("EOImage", (Integer) map.get("eoImage"));
			DBServices.create(EOObject.createObject(map));

			EOManagementUser eoUser = (EOManagementUser) EOObject.getLatestObject(className);
			eoImg.headerPk = eoUser.primaryKey;
			eoImg.postCreate(eoImg);
		}
		VSPLUtil.saveSMAlogo("SMAlogo");
	}

}
