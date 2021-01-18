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
@Table(name = "EOSTUDENT_FREE_TEXT_INVOICE_MAPPING")
@SequenceGenerator(name = "EOSTUDENT_FREE_TEXT_INVOICE_MAPPING_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_FREE_TEXT_INVOICE_MAPPING_SEQ")


public class EOStudentFreeTextInvoiceMapping extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_FREE_TEXT_INVOICE_MAPPING_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "AMOUNT")
	public double amount;  
	
	@Column(name = "DESCRIPTION")
	public String description;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonBackReference
	public EOStudentFreeTextInvoice eoStudentFreeTextInvoice;
	
	public EOStudentFreeTextInvoiceMapping(){
		
	}

	public EOStudentFreeTextInvoiceMapping(Date createdDate, Date updatedDate, double amount, String description,
			boolean isActive, EOStudentFreeTextInvoice eoStudentFreeTextInvoice) {
		super();
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.amount = amount;
		this.description = description;
		this.isActive = isActive;
		this.eoStudentFreeTextInvoice = eoStudentFreeTextInvoice;
	}
	
	
}
