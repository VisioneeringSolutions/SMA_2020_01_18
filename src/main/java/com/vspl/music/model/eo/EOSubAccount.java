package com.vspl.music.model.eo;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;

@Entity
@Table(name = "EOSubAccount")
@SequenceGenerator(name = "EOSubAccount_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSubAccount_SEQ")
public class EOSubAccount extends EOObject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSubAccount_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "ACCOUNT_NAME")
	public String accountName;
	
	@Column(name = "DESCRIPTION")
	public String descriptions; 
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@OneToOne
	public EOAccountType eoAccountType;

}
