package com.vspl.music.model.lk;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.vspl.music.model.eo.EOObject;

@Entity
@Table(name = "LKCATEGORY_TYPE")
@SequenceGenerator(name = "LKCATEGORY_TYPE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "LKCATEGORY_TYPE_SEQ")

public class LKCategoryType extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LKCATEGORY_TYPE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "CATEGORY_TYPE")
	public String categoryType;
	
	@Column(name = "DESCRIPTION")
	public String description;
	
	@Column(name = "IS_ACTIVE")
	public boolean isActive;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	
	public LKCategoryType(){
		
	}

	public LKCategoryType(String categoryType, String description, boolean isActive) {
		super();
		this.categoryType = categoryType;
		this.description = description;
		this.isActive = isActive;
	}

	
}
