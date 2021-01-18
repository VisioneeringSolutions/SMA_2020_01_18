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

import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.codehaus.jackson.annotate.JsonBackReference;

@Entity
@Table(name = "EOSTUDENT_CRITERIA")
@SequenceGenerator(name = "EOSTUDENT_CRITERIA_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_CRITERIA_SEQ")


public class EOStudentCriteria extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_CRITERIA_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@OneToOne(optional = true)
	public EOMasterStudentCriteria eoMasterStudentCriteria;
	
	@OneToOne(optional = true)
	public EODefinedSlot eoDefinedSlot;
	
	@OneToOne(optional = true)
	public EOStudentUser eoStudentUser;
	
	@OneToOne(optional = true)
	public EOBatch eoBatch;
	
	@Column(name = "OPTED_RATING", columnDefinition = "Decimal(20,2) default '0.00'")
	public Double optedRating;
	
	@OneToOne(optional = true)
	public EOTeacherUser eoTeacherUser;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate = new Date(System.currentTimeMillis());
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate = new Date(System.currentTimeMillis());
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	public EOStudentRating eoStudentRating;
	


}
