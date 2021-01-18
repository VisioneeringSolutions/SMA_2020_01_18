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
@Table(name = "EOSTUDENT_USER")
@SequenceGenerator(name = "EOSTUDENT_USER_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_USER_SEQ")

public class EOStudentUser extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_USER_SEQ")
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

	@Column(name = "STUDENT_ID")
	public String studentId;
	
	@Column(name = "GENDER")
	public String gender;

	@Column(name = "DATE_OF_BIRTH")
	public String dateOfBirth;
	
	@Column(name = "ENROLLMENT_DATE")
	public String enrollmentDate;

	@Column(name = "EMAIL")
	public String email;
	
	@Column(name = "ILLNESS")
	public String illness;
	
	@Column(name = "PRESCRIBED_MEDICINE")
	public String prescribedMedication;
	
	@Column(name = "PHONE")
	public String phone;
	
	@Column(name = "MUSIC_PK")
	public String musicPk;
	
	@Column(name = "ALTERNATE_EMAIL")
	public String alternateEmail;
	
	@Column(name = "ALTERNATE_PHONE")
	public String alternatePhone;
	
	@Column(name = "ADDRESS_LINE_1")
	public String addressLine1;
	
	@Column(name = "ADDRESS_LINE_2")
	public String addressLine2;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "GUARDIAN_TITLE")
	public String guardianTitle;
	
	@Column(name = "GUARDIAN_FIRST_NAME")
	public String guardianFirstName;
	
	@Column(name = "GUARDIAN_LAST_NAME")
	public String guardianLastName;

	@Column(name = "RELATIONSHIP")
	public String relationship;

	@Column(name = "GUARDIAN_EMAIL")
	public String guardianEmail;
	
	@Column(name = "GUARDIAN_PHONE")
	public String guardianPhone;
	
	@Column(name = "GUARDIAN_ADDRESS_LINE_1")
	public String guardianAddressLine1;
	
	@Column(name = "GUARDIAN_ADDRESS_LINE_2")
	public String guardianAddressLine2;
	
	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "REGISTRATION_AMOUNT")
	public String registrationAmount;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "IS_VISIBLE" ,columnDefinition = "boolean default true")
	public boolean isVisible = true;
	
	@Column(name = "IS_TRIAL" ,columnDefinition = "boolean default false")
	public boolean isTrial= false;
	
	@OneToOne(optional = true)
	public EOImage eoImage;
	
	@OneToOne(optional = true)
	public EOLoginDetails eoLoginDetails;
	
	public EOStudentUser(){
	
	}

	
	public EOStudentUser(String firstName, String lastName, String firstNameJap, String lastNameJap, String studentId,
			String gender, String dateOfBirth, String enrollmentDate, String email, String illness,
			String prescribedMedication, String phone, String musicPk, String alternateEmail, String alternatePhone,
			String addressLine1, String addressLine2, Date createdDate, Date updatedDate, String guardianTitle,
			String guardianFirstName, String guardianLastName, String relationship, String guardianEmail,
			String guardianPhone, String guardianAddressLine1, String guardianAddressLine2, String removingReason,
			String registrationAmount, boolean isActive,boolean isVisible, EOImage eoImage, EOLoginDetails eoLoginDetails) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.firstNameJap = firstNameJap;
		this.lastNameJap = lastNameJap;
		this.studentId = studentId;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.enrollmentDate = enrollmentDate;
		this.email = email;
		this.illness = illness;
		this.prescribedMedication = prescribedMedication;
		this.phone = phone;
		this.musicPk = musicPk;
		this.alternateEmail = alternateEmail;
		this.alternatePhone = alternatePhone;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.guardianTitle = guardianTitle;
		this.guardianFirstName = guardianFirstName;
		this.guardianLastName = guardianLastName;
		this.relationship = relationship;
		this.guardianEmail = guardianEmail;
		this.guardianPhone = guardianPhone;
		this.guardianAddressLine1 = guardianAddressLine1;
		this.guardianAddressLine2 = guardianAddressLine2;
		this.removingReason = removingReason;
		this.registrationAmount = registrationAmount;
		this.isActive = isActive;
		this.isVisible = isVisible;
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
			studName =lastNameJap.trim() + " "+ firstNameJap.trim() ;
		}
	
		return studName;
	}
	
}
