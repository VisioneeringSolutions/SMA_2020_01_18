package com.vspl.music.model.eo;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "EOLOGIN_DETAILS",uniqueConstraints = @UniqueConstraint(columnNames = { "role", "user_name"}) )
@SequenceGenerator(name = "EOLOGIN_DETAILS_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOLOGIN_DETAILS_SEQ")

public class EOLoginDetails extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOLOGIN_DETAILS_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "USER_NAME")
	public String userName;
	
	@Column(name = "USER_NAME_2")
	public String userName2;
	
	@Column(name = "PASSWORD")
	public String password;

	@Column(name = "ROLE")
	public String role;

	@Column(name = "LAST_LOGIN")
	public int lastLogin;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	public EOLoginDetails() {
		
	}

	public EOLoginDetails(String userName, String password, String role, int lastLogin, Date createdDate,
			Date updatedDate, boolean isActive) {
		super();
		this.userName = userName;
		this.password = password;
		this.role = role;
		this.lastLogin = lastLogin;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.isActive = isActive;
	} 
		
}
