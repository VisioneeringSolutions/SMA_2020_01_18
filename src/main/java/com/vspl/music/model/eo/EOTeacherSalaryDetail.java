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
@Table(name = "EOTEACHER_SALARY_DETAIL")
@SequenceGenerator(name = "EOTEACHER_SALARY_DETAIL_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTEACHER_SALARY_DETAIL_SEQ")

public class EOTeacherSalaryDetail extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTEACHER_SALARY_DETAIL_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "AMOUNT")
	public double amount;
	
	@OneToOne
	public EOTeacherSalary eoTeacherSalary;
	
	@OneToOne(optional = true)
	public LKClassDuration lkClassDuration;
	
	@OneToOne(optional = true)
	public LKMusicType lkMusicType;
	
	@OneToOne(optional = true)
	public LKCategoryType lkCategoryType;
	
	@OneToOne(optional = true)
	public EOCourses eoCourses;
}

