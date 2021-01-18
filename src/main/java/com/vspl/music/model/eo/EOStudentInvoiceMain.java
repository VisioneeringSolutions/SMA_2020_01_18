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

@Entity
@Table(name = "EOSTUDENT_INVOICE_MAIN")
@SequenceGenerator(name = "EOSTUDENT_INVOICE_MAIN_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_INVOICE_MAIN_SEQ")
public class EOStudentInvoiceMain extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_INVOICE_MAIN_SEQ")
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
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@Column(name = "DUE_DATE")
	public String dueDate;
	
	@Column(name = "TOTAL")
	public double total;
	
	@Column(name = "GRAND_TOTAL")
	public double grandTotal;
	
	@Column(name = "DEPOSIT_AMOUNT")
	public double depositAmount;
	
	@Column(name = "DUE_AMOUNT")
	public double dueAmount;
	
	@Column(name = "CONSUMPTION_TAX")
	public double consumptionTax;
	
	@Column(name = "CANCELLATION_AMOUNT")
	public double cancellationAmount;
	
	@Column(name = "INVOICE_NO")
	public String invoiceNo;
	
	@Column(name = "RUNNING_NO")
	public double runningNo;
	
	@OneToOne(optional = true)
	public EOStudentUser eoStudentUser;
	
	@OneToOne(optional = true)
	public EOPdf eoPdf;
	
	@OneToMany(mappedBy = "eoStudentInvoiceMain", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JsonManagedReference
	public List<EOStudentInvoiceDetail> eoStudentInvoiceDetails = new LinkedList<>();
	
	@OneToMany(mappedBy = "eoStudentInvoiceMain", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JsonManagedReference
	public List<EOStudentCreditNote> eoStudentCreditNote = new LinkedList<>();
	
	@OneToMany(mappedBy = "eoStudentInvoiceMain", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JsonManagedReference
	public List<EOStudentInvoiceEditable> eoStudentInvoiceEditable = new LinkedList<>();
}
