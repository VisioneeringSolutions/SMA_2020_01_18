package com.vspl.music.model.eo;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;

import com.vspl.music.model.lk.LKCategoryType;
import com.vspl.music.model.lk.LKClassDuration;
import com.vspl.music.model.lk.LKMusicType;

@Entity
@Table(name = "EOTEACHER_SALARY")
@SequenceGenerator(name = "EOTEACHER_SALARY_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTEACHER_SALARY_SEQ")

public class EOTeacherSalary extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTEACHER_SALARY_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "STATUS")
	public String status;
	
	@Column(name = "SALARY_TYPE")
	public String salaryType;
	
	@Column(name = "TRANSPORT_AMOUNT")
	public double transportAmount; 
	
	@Column(name = "AMOUNT")
	public double amount; 
	
	@OneToOne
	public EOTeacherUser eoTeacherUser;
	
}

