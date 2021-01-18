
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

@Entity
@Table(name = "EOACCOUNT_TYPE")
@SequenceGenerator(name = "EOACCOUNT_TYPE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOACCOUNT_TYPE_SEQ")

public class EOAccountType extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOACCOUNT_TYPE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "ACCOUNT_TYPE")
	public String accountType;
	
	@Column(name = "ACCOUNT_NAME")
	public String accountName;
	
	@Column(name = "DESCRIPTION")
	public String descriptions; 
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	

	public EOAccountType(){
		
	}

	
	public EOAccountType(String accountType, String accountName, String descriptions) {
		super();
		this.accountType = accountType;
		this.accountName = accountName;
		this.descriptions = descriptions;
		
	}

	
}

