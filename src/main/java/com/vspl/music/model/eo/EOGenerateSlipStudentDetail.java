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
@Table(name = "EOGENERATE_SLIP_STUDENT_DETAIL")
@SequenceGenerator(name = "EOGENERATE_SLIP_STUDENT_DETAIL_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOGENERATE_SLIP_STUDENT_DETAIL_SEQ")

public class EOGenerateSlipStudentDetail extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOGENERATE_SLIP_STUDENT_DETAIL_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "DAY")
	public String day;

	@Column(name = "DATE")
	public String date;

	@Column(name = "START_TIME")
	public String startTime;
	
	@Column(name = "END_TIME")
	public String endTime;
	
	@Column(name = "SLOT_PK")
	public String eoSlot;
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@Column(name = "DURATION")
	public String duration;
	
	@Column(name = "IS_ACTIVE")
	public boolean isActive = true;
	
	@OneToOne
	public EOTeacherUser eoTeacherUser;
	
	@OneToOne(optional = true)
	public EOStudentUser eoStudentUser;
	
	@OneToOne
	public EOBatch eoBatch;
	
	@OneToOne(optional = true)
	public LKMusicType lkMusicType;
	
	@OneToOne(optional = true)
	public LKCategoryType lkCategoryType;
	
	@OneToOne(optional = true)
	public EOMusicRoom eoMusicRoom;
	
	@OneToOne(optional = true)
	public EOGenerateSlip eoGenerateSlip;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
}

