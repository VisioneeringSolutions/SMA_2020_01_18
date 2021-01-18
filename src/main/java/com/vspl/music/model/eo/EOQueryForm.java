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

@Entity
@Table(name = "EOQUERY_FORM")
@SequenceGenerator(name = "EOQUERY_FORM_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOQUERY_FORM_SEQ")


public class EOQueryForm extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOQUERY_FORM_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "FIRST_NAME")
	public String firstName;
	
	@Column(name = "LAST_NAME")
	public String lastName;

	@Column(name = "FIRST_NAME_JAP")
	public String firstNameJap;
	
	@Column(name = "LAST_NAME_JAP")
	public String lastNameJap;
	
	@Column(name = "GENDER")
	public String gender;

	@Column(name = "DATE_OF_BIRTH")
	public String dateOfBirth;
	
	@Column(name = "ENQUIRY_DATE")
	public String enquiryDate;
	
	@Column(name = "NEXT_FOLLOWUP_DATE")
	public String nextFollowupDate;

	@Column(name = "EMAIL")
	public String email;
	
	@Column(name = "PHONE")
	public String phone;
	
	@Column(name = "ADDRESS")
	public String address;
	
	@Column(name = "REMARKS",  columnDefinition = "Text")
	public String remarks;
	
	/*@Column(name = "COURSE",  columnDefinition = "Text")
	public String course;*/
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "MUSIC_PK")
	public String musicPk;
	
	@Column(name = "COURSE_PK")
	public String coursePk;
	
	/*@Column(name = "STATUS")
	public String status;*/
	
	@OneToOne(optional = true)
	public EOCourses eoCourses;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "IS_CONVERT" ,columnDefinition = "boolean default true")
	public boolean isConvert = true;
	
	@Column(name = "CONVERT_DATE")
	public String convertDate;
	
	@Column(name = "USER_NAME")
	public String userName;
	
	public EOQueryForm () {
			
	}

	public EOQueryForm(String firstName, String lastName, String gender, String dateOfBirth, String enquiryDate,
			String nextFollowupDate, String email, String phone, String address, String remarks, Date createdDate,
			Date updatedDate, String musicPk, EOCourses eoCourses, boolean isActive , boolean isConvert,String convertDate) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.enquiryDate = enquiryDate;
		this.nextFollowupDate = nextFollowupDate;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.remarks = remarks;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.musicPk = musicPk;
		this.eoCourses = eoCourses;
		this.isActive = isActive;
		
		this.isConvert = isConvert;
		this.convertDate = convertDate;
	}
	
	public String getFullName (){
		String studName = "";
		
		if(this.firstName != null && this.lastName != null){
			studName = firstName.trim() + " "+lastName.trim();
		}
	
		return studName;
	}
}
