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
import com.vspl.music.model.lk.LKMusicType;

@Entity
@Table(name = "EOTEACHER_APPLICABILITY")
@SequenceGenerator(name = "EOTEACHER_APPLICABILITY_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTEACHER_APPLICABILITY_SEQ")

public class EOTeacherApplicability extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTEACHER_APPLICABILITY_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@OneToOne(optional = true)
	public LKMusicType lkMusicType;
	
	@OneToOne(optional = true)
	public LKCategoryType lkCategoryType;
	
	public EOTeacherApplicability (){
		
	}

	public EOTeacherApplicability(boolean isActive, Date createdDate, Date updatedDate, LKMusicType lkMusicType,
			LKCategoryType lkCategoryType) {
		super();
		this.isActive = isActive;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.lkMusicType = lkMusicType;
		this.lkCategoryType = lkCategoryType;
	}
	
}
