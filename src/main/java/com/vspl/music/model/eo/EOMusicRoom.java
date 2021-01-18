package com.vspl.music.model.eo;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "EOMUSIC_ROOM")
@SequenceGenerator(name = "EOMUSIC_ROOM_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOMUSIC_ROOM_SEQ")
public class EOMusicRoom extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOMUSIC_ROOM_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "ROOM_NAME")
	public String roomName;
	
	@Column(name = "ROOM_ID")
	public String roomId;

	@Column(name = "MAX_STUDENT")
	public int maxStudent;

	@Column(name = "DETAILS")
	public String details;
	
	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	public EOMusicRoom (){
		
	}

	public EOMusicRoom(String roomName, String roomId, int maxStudent, String details, String removingReason,
			Date createdDate, Date updatedDate, boolean isActive) {
		super();
		this.roomName = roomName;
		this.roomId = roomId;
		this.maxStudent = maxStudent;
		this.details = details;
		this.removingReason = removingReason;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.isActive = isActive;
	}

}
