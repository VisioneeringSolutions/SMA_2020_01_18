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

import com.vspl.music.model.lk.LKCategoryType;
import com.vspl.music.model.lk.LKClassDuration;
import com.vspl.music.model.lk.LKMusicType;


@Entity
@Table(name = "EOCOURSES")
@SequenceGenerator(name = "EOCOURSES_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOCOURSES_SEQ")
public class EOCourses extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOCOURSES_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "COURSE_CODE")
	public String courseCode;

	@Column(name = "COURSE_NAME")
	public String courseName;

	@Column(name = "DETAILS", columnDefinition = "Text")   
	public String details;
	
	@Column(name = "URL")
	public String url;
	
	@Column(name = "START_DATE")
	public String startDate;
	
	@Column(name = "SESSION")
	public int session;
	
	@Column(name = "FEE_TYPE")
	public String feeType;
	
	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "FEES")
	public int fees;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@OneToOne(optional = true)
	public LKMusicType lkMusicType;
	
	@OneToOne(optional = true)
	public LKClassDuration lkClassDuration;
	
	@OneToOne(optional = true)
	public LKCategoryType lkCategoryType;
	
	public EOCourses (){
		
	}

	public EOCourses(String courseCode, String courseName, String details, String startDate,  String url,
			int session, LKClassDuration lkClassDuration, String feeType, int fees, Date createdDate, Date updatedDate,
			boolean isActive, LKMusicType lkMusicType, LKCategoryType lkCategoryType) {
		super();
		this.courseCode = courseCode;
		this.courseName = courseName;
		this.details = details;
		this.url = url;
		this.startDate = startDate;
		this.session = session;
		this.lkClassDuration = lkClassDuration;
		this.feeType = feeType;
		this.fees = fees;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.isActive = isActive;
		this.lkMusicType = lkMusicType;
		this.lkCategoryType = lkCategoryType;
	}
	
}
