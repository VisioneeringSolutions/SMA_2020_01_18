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
@Table(name = "EOTEACHER_RATING")
@SequenceGenerator(name = "EOTEACHER_RATING_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTEACHER_RATING_SEQ")

public class EOTeacherRating extends EOObject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTEACHER_RATING_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@OneToOne
	public EODefinedSlot eoDefinedSlot;
	
	@OneToOne(optional = true)
	public EOStudentUser eoStudentUser;
	
	@OneToOne
	public EOBatch eoBatch;
	
	
	@Column(name = "AVG_OPTED_RATING", columnDefinition = "Decimal(20,2) default '0.00'")
	public Double avgOptedRating;
	
	
	@Column(name = "DATE")
	public Date date;
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@OneToOne
	public EOTeacherUser eoTeacherUser;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate = new Date(System.currentTimeMillis());
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate = new Date(System.currentTimeMillis());
	
	@OneToMany(mappedBy = "eoTeacherRating", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JsonManagedReference
	public List<EOTeacherCriteria> eoTeacherCriteria = new LinkedList<>();
	
	

}
