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
@Table(name = "LKCOLOR")
@SequenceGenerator(name = "LKCOLOR_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "LKCOLOR_SEQ")

public class LKColor extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LKCOLOR_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "COLOR_CODE")
	public String colorCode;
	
	@Column(name = "IS_ACTIVE")
	public boolean isActive;
	
	
}
