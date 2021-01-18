package com.vspl.music.model.lk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.vspl.music.model.eo.EOObject;

@Entity
@Table(name = "LKCLASS_DURATION")
@SequenceGenerator(name = "LKCLASS_DURATION_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "LKCLASS_DURATION_SEQ")

public class LKClassDuration extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LKCLASS_DURATION_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "DURATION")
	public String duration;
	
	@Column(name = "IS_ACTIVE")
	public boolean isActive;
	
	public LKClassDuration(){
		
	}

	public LKClassDuration(String duration, boolean isActive) {
		super();
		this.duration = duration;
		this.isActive = isActive;
	}
	
}
