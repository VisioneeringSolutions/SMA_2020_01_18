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
@Table(name = "EOTIMESLOT")
@SequenceGenerator(name = "EOTIMESLOT_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTIMESLOT_SEQ")
public class EOTimeSlot extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTIMESLOT_SEQ")
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
	
	@Column(name = "STATUS")
	public String status;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	public EOTimeSlot (){
		
	}
	public EOTimeSlot(String day, String date, String startTime, String endTime, String status, Date createdDate,
			Date updatedDate) {
		super();
		this.day = day;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}

}
