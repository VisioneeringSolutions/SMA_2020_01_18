package com.vspl.music.model.eo;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.vspl.music.model.lk.LKSecurityQuestion;

@Entity
@Table(name = "EOMANAGEMENT_USER")
@SequenceGenerator(name = "EOMANAGEMENT_USER_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOMANAGEMENT_USER_SEQ")
public class EOManagementUser extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOMANAGEMENT_USER_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "PREFIX")
	public String prefix;
	
	@Column(name = "FIRST_NAME")
	public String firstName;

	@Column(name = "LAST_NAME")
	public String lastName;

	@Column(name = "GENDER")
	@Resource(name = "GENDER", description = "Gender")
	public String gender;

	@Column(name = "EMAIL")
	public String email;

	@Column(name = "PHONE")
	public String phone;

	@Column(name = "PASSWORD", nullable = true)
	public String password;
	
	@Column(name = "ADDRESS_1")
	public String address1;
	
	@Column(name = "ADDRESS_2")
	public String address2;
	
	@Column(name = "POSTAL_CODE")
	public String postalCode;
	
	@OneToOne
	public LKSecurityQuestion lkSecurityQuestion;
	
	@Column(name = "SECURITY_ANSWER")
	public boolean securityAnswer;

	@OneToOne(optional = true)
	@JoinColumn(name = "eoimage")
	public EOImage eoImage;
	
	
	public String getFullName() {
		String name = prefix + " " +firstName;
		
		if (this.lastName != null) {
			name = name + " " + lastName;
		} 

		return name;
	}

}
