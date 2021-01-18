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
@Table(name = "LKSECURITY_QUESTION")
@SequenceGenerator(name = "LKSECURITY_QUESTION_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "LKSECURITY_QUESTION_SEQ")
public class LKSecurityQuestion extends EOObject {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LKSECURITY_QUESTION_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "QUESTION")
	public String question;
	
	@Column(name = "IS_ACTIVE")
	public boolean isActive;

	public LKSecurityQuestion(){
		
	}
	
	public LKSecurityQuestion(String question, boolean isActive) {
		super();
		this.question = question;
		this.isActive = isActive;
	}
	
	
}
	

