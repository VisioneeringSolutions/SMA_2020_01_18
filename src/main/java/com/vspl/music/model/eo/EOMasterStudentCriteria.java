package com.vspl.music.model.eo;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "EOMASTER_STUDENT_CRITERIA")
@SequenceGenerator(name = "EOMASTER_STUDENT_CRITERIA_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOMASTER_STUDENT_CRITERIA_SEQ")


public class EOMasterStudentCriteria  extends EOObject{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOMASTER_STUDENT_CRITERIA_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "CRITERIA")
	public String criteria;
	
	@Column(name = "MAX_RATING")
	public String maxRating;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate = new Date(System.currentTimeMillis());
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate = new Date(System.currentTimeMillis());
	
	public EOMasterStudentCriteria(){
		
	}

	public EOMasterStudentCriteria(String criteria, String maxRating, boolean isActive, Date createdDate,
			Date updatedDate) {
		super();
		this.criteria = criteria;
		this.maxRating = maxRating;
		this.isActive = isActive;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}
	
	
}
