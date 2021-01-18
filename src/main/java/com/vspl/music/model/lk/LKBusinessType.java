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
@Table(name = "LKBUSINESS_TYPE")
@SequenceGenerator(name = "LKBUSINESS_TYPE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "LKBUSINESS_TYPE_SEQ")
public class LKBusinessType extends EOObject {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LKBUSINESS_TYPE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "BUSINESS_TYPE")
	public String businessType;
	
	@Column(name = "IS_ACTIVE")
	public boolean isActive;

	public LKBusinessType(){
		
	}
	
	public LKBusinessType(String businessType, boolean isActive) {
		super();
		this.businessType = businessType;
		this.isActive = isActive;
	}
	
}
