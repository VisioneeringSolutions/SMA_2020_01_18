package com.vspl.music.model.eo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "EOPDF_JSON")
@SequenceGenerator(name = "EOPDF_JSON_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOPDF_JSON_SEQ")
public class EOPdfJson extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOPDF_JSON_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "KEY")
	public String key;

	@Column(name = "VALUE")
	public String value;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	}
