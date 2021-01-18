package com.vspl.music.model.eo;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.vspl.music.model.lk.LKCategoryType;
import com.vspl.music.model.lk.LKMusicType;

@Entity
@Table(name = "EOTEACHER_USER")
@SequenceGenerator(name = "EOTEACHER_USER_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTEACHER_USER_SEQ")


public class EOTeacherUser extends EOObject{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTEACHER_USER_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "TITLE")
	public String title;
	
	@Column(name = "FIRST_NAME")
	public String firstName;
	
	@Column(name = "LAST_NAME")
	public String lastName;
	
	@Column(name = "FIRST_NAME_JAP")
	public String firstNameJap;
	
	@Column(name = "LAST_NAME_JAP")
	public String lastNameJap;
	
	@Column(name = "TEACHER_ID")
	public String teacherId;

	@Column(name = "GENDER")
	public String gender;

	@Column(name = "QUALIFICATION",  columnDefinition = "Text")
	public String qualification;
	
	@Column(name = "EXPERIENCE",  columnDefinition = "Text")
	public String experience;

	@Column(name = "PROFILE",  columnDefinition = "Text")
	public String profile;
	
	@Column(name = "AWARDS",  columnDefinition = "Text")
	public String awards;
	
	@Column(name = "EMAIL")
	public String email;
	
	@Column(name = "PHONE")
	public String phone;
	
	@Column(name = "SALARY_TYPE")
	public String salaryType;
	
	@Column(name = "MUSIC_CATEGORY_PK")
	public String musicCategoryPk; 		//music primaryKey '_' category primaryKey
	
	@Column(name = "ALTERNATE_EMAIL")
	public String alternateEmail;
	
	@Column(name = "ALTERNATE_PHONE")
	public String alternatePhone;
	
	@Column(name = "ADDRESS_LINE_1")
	public String addressLine1;
	
	@Column(name = "ADDRESS_LINE_2")
	public String addressLine2;
	
	@Column(name = "JOINING_DATE")
	public String joiningDate;
	
	@Column(name = "COLOR_CODE")
	public String colorCode;
	
	@Column(name = "BACKGROUND_COLOR")
	public String backgroundColor;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "COURSE_ARRAY")
	public String courseArray;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@OneToOne(optional = true)
	public EOImage eoImage;
	
	@OneToOne(optional = true)
	public EOLoginDetails eoLoginDetails;
	
	public EOTeacherUser(){
		
	}
	
	public EOTeacherUser(String title, String firstName, String lastName, String firstNameJap, String lastNameJap,
			String teacherId, String gender, String qualification, String experience, String profile, String awards,
			String email, String phone, String musicCategoryPk, String alternateEmail, String alternatePhone,
			String addressLine1, String addressLine2, String joiningDate, String colorCode, String backgroundColor,
			Date createdDate, Date updatedDate, String removingReason, boolean isActive, EOImage eoImage,
			EOLoginDetails eoLoginDetails) {
		super();
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.firstNameJap = firstNameJap;
		this.lastNameJap = lastNameJap;
		this.teacherId = teacherId;
		this.gender = gender;
		this.qualification = qualification;
		this.experience = experience;
		this.profile = profile;
		this.awards = awards;
		this.email = email;
		this.phone = phone;
		this.musicCategoryPk = musicCategoryPk;
		this.alternateEmail = alternateEmail;
		this.alternatePhone = alternatePhone;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.joiningDate = joiningDate;
		this.colorCode = colorCode;
		this.backgroundColor = backgroundColor;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.removingReason = removingReason;
		this.isActive = isActive;
		this.eoImage = eoImage;
		this.eoLoginDetails = eoLoginDetails;
	}


	public String getFullName (){
		String studName = "";
		
		if(this.firstName != null && this.lastName != null){
			studName = firstName.trim() + " "+lastName.trim();
		}
	
		return studName;
	}
	
	public String getFullNameJapanese (){
		String studName = "";
		
		if(this.firstNameJap != null && this.lastNameJap != null){
			studName = lastNameJap.trim() + " "+firstNameJap.trim();
		}
	
		return studName;
	}
	
	
}
