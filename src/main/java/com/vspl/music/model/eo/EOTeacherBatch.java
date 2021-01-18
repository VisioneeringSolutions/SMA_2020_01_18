package com.vspl.music.model.eo;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "EOTEACHER_BATCH")
@SequenceGenerator(name = "EOTEACHER_BATCH_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTEACHER_BATCH_SEQ")

public class EOTeacherBatch extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTEACHER_BATCH_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;

	@Column(name = "TEACHER_PK")
	public String teacherPk;
	
	@OneToOne(optional = true)
	public EOBatch eoBatch;
	
	@OneToOne(optional = true)
	public EODefinedSlot eoDefinedSlot;
	
	@Column(name = "HOME_WORK",  columnDefinition = "Text")
	public String homeWork;
	
	/*@OneToOne(optional = true)
	public EOImage homeWorkImage;*/
	
	@Column(name = "IMPROVEMENTS_COMMENTS")
	public String improvementComments;
	
	@Column(name = "COMMENTS")
	public String comments;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@ManyToMany(fetch = FetchType.EAGER)
	public List<EOImage> eoAttachmentArray = new LinkedList<>();
	
	
    public EOTeacherBatch () {
		
	}


	public EOTeacherBatch(String teacherPk, EOBatch eoBatch, EODefinedSlot eoDefinedSlot, String homeWork,
			String improvementComments, String comments, Date createdDate, Date updatedDate,
			List<EOImage> eoAttachmentArray) {
		super();
		this.teacherPk = teacherPk;
		this.eoBatch = eoBatch;
		this.eoDefinedSlot = eoDefinedSlot;
		this.homeWork = homeWork;
		this.improvementComments = improvementComments;
		this.comments = comments;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.eoAttachmentArray = eoAttachmentArray;
	}


}
	
