package com.vspl.music.model.eo;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonBackReference;

import com.vspl.music.model.lk.LKClassDuration;
import com.vspl.music.model.lk.LKMusicType;


@Entity
@Table(name = "EOSTUDENT_INVOICE_DETAIL")
@SequenceGenerator(name = "EOSTUDENT_INVOICE_DETAIL_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_INVOICE_DETAIL_SEQ")

public class EOStudentInvoiceDetail extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_INVOICE_DETAIL_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;

	@Column(name = "SESSION")
	public double session; 
	
	@Column(name = "FEES")
	public double fees;  
	
	@Column(name = "FEE_TYPE")
	public String feeType; 
	
	@OneToOne(optional = true)
	public EOCourses eoCourses;
	
	@OneToOne(optional = true)
	public EOBatch eoBatch;
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@Column(name = "TOTAL_MINS")
	public double totalMins;
	
	@OneToOne(optional = true)
	public LKClassDuration lkClassDuration;
	
	@OneToOne(optional = true)
	public LKMusicType lkMusicType;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonBackReference
	public EOStudentInvoiceMain eoStudentInvoiceMain;
	
}

