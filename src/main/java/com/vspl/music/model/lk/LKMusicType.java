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
@Table(name = "LKMUSIC_TYPE")
@SequenceGenerator(name = "LKMUSIC_TYPE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "LKMUSIC_TYPE_SEQ")

public class LKMusicType extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LKMUSIC_TYPE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "MUSIC_TYPE")
	public String musicType;
	
	@Column(name = "IS_ACTIVE")
	public boolean isActive;
	
	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	public LKMusicType(){
		
	}

	public LKMusicType(String musicType, boolean isActive) {
		super();
		this.musicType = musicType;
		this.isActive = isActive;
	}
	
}
