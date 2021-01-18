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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonManagedReference;


@Entity
@Table(name = "EOSTUDENT_FREE_TEXT_INVOICE",uniqueConstraints = @UniqueConstraint(columnNames = { "month", "year", "student_pk"}) )
@SequenceGenerator(name = "EOSTUDENT_FREE_TEXT_INVOICE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_FREE_TEXT_INVOICE_SEQ")

public class EOStudentFreeTextInvoice extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_FREE_TEXT_INVOICE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "STUDENT_PK")
	public String studentPk; 
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@Column(name = "INVOICE_NO")
	public String invoiceNo;
	
	@Column(name = "RUNNING_NO")
	public double runningNo;
	
	@OneToMany(mappedBy = "eoStudentFreeTextInvoice", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JsonManagedReference
	public List<EOStudentFreeTextInvoiceMapping> eoStudentFreeTextInvoiceMapping = new LinkedList<>();
	
	public EOStudentFreeTextInvoice(){
		
	}

	public EOStudentFreeTextInvoice(boolean isActive, Date createdDate, Date updatedDate, String studentPk,
			String month, String year, String invoiceNo, double runningNo,
			List<EOStudentFreeTextInvoiceMapping> eoStudentFreeTextInvoiceMapping) {
		super();
		this.isActive = isActive;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.studentPk = studentPk;
		this.month = month;
		this.year = year;
		this.invoiceNo = invoiceNo;
		this.runningNo = runningNo;
		this.eoStudentFreeTextInvoiceMapping = eoStudentFreeTextInvoiceMapping;
	}
	
}

