package com.vspl.music.model.eo;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonBackReference;


@Entity
@Table(name = "EOSTUDENT_INVOICE_EDITABLE")
@SequenceGenerator(name = "EOSTUDENT_INVOICE_EDITABLE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_INVOICE_EDITABLE_SEQ")

public class EOStudentInvoiceEditable extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_INVOICE_EDITABLE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "DESCRIPTION")
	public String description;  
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "AMOUNT")
	public String amount; 
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonBackReference
	public EOStudentInvoiceMain eoStudentInvoiceMain;
	
}

