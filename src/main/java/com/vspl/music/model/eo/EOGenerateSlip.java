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

import com.vspl.music.model.lk.LKClassDuration;


@Entity
@Table(name = "EOGENERATE_SLIP")
@SequenceGenerator(name = "EOGENERATE_SLIP_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOGENERATE_SLIP_SEQ")

public class EOGenerateSlip extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOGENERATE_SLIP_SEQ")
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
	
	@Column(name = "TRANSPORT_AMOUNT")
	public double transportAmount; 
	
	@Column(name = "TRANSPORT_DAYS")
	public double transportDays; 
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@Column(name = "ADD")
	public double add; 
	
	@Column(name = "SUB")
	public double sub; 
	
	@Column(name = "ADD_DESC")
	public String addDesc; 
	
	@Column(name = "SUB_DESC")
	public String subDesc; 
	
	@Column(name = "TOTAL_SALARY")
	public double totalSalary; 
	
	@Column(name = "TEACHER_REMARKS")
	public String teacherRemarks;
	
	@Column(name = "SALARY_TYPE")
	public String salaryType;
	
	@Column(name = "AMOUNT")
	public double amount; 
	
	@OneToOne
	public EOTeacherUser eoTeacherUser;
	
	@OneToOne(optional = true)
	public EOPdf eoPdf;


}

