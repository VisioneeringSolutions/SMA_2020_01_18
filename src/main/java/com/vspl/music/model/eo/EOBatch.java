package com.vspl.music.model.eo;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "EOBATCH")
@SequenceGenerator(name = "EOBATCH_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOBATCH_SEQ")
public class EOBatch extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOBATCH_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "BATCH_NAME")
	public String batchName;
	
	@Column(name = "BATCH_ID")
	public String batchId;
	
	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "START_DATE")
	public String startDate;
	
	@Column(name = "STATUS")
	public String status;
	
	@Column(name = "BATCH_TYPE")
	public String batchType;
	
	@Column(name = "BATCH_SEQ")
	public double batchSeq;
	
	@Column(name = "BATCH_ID_SEQ")
	public double batchIdSeq;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@OneToOne(optional = true)
	public EOCourses eoCourses;
	
	public EOBatch (){
		
	}

	public EOBatch(String batchName, String removingReason, String startDate, String status, Date createdDate,
			Date updatedDate, boolean isActive, EOCourses eoCourses) {
		super();
		this.batchName = batchName;
		this.removingReason = removingReason;
		this.startDate = startDate;
		this.status = status;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.isActive = isActive;
		this.eoCourses = eoCourses;
	}

}
