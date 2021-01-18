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

import com.vspl.music.model.lk.LKMusicType;

@Entity
@Table(name = "EOSTUDENT_BATCH")
@SequenceGenerator(name = "EOSTUDENT_BATCH_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_BATCH_SEQ")
public class EOStudentBatch extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_BATCH_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "BATCH_TYPE")
	public String batchType;
	
	@Column(name = "STUDENTS_PK")
	public String studentsPk;

	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@OneToOne(optional = true)
	public EOBatch eoBatch;	
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@OneToMany(mappedBy = "eoStudentBatch", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JsonManagedReference
	public List<EOStudentBatchMapping> eoStudentBatchMapping = new LinkedList<>();
	
	public EOStudentBatch (){
		
	}

	public EOStudentBatch(String batchType, String studentsPk, String removingReason, Date createdDate,
			Date updatedDate, EOBatch eoBatch, boolean isActive) {
		super();
		this.batchType = batchType;
		this.studentsPk = studentsPk;
		this.removingReason = removingReason;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.eoBatch = eoBatch;
		this.isActive = isActive;
	}

}
