package com.vspl.music.model.eo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonBackReference;

import com.vspl.music.model.lk.LKCategoryType;

@Entity
@Table(name = "EOCOMPETITION_MAPPING")
@SequenceGenerator(name = "EOCOMPETITION_MAPPING_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOCOMPETITION_MAPPING_SEQ")

public class EOCompetitionMapping extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOCOMPETITION_MAPPING_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@OneToOne(optional = true)
	public EOStudentUser eoStudentUser;
	
	@OneToOne(optional = true)
	public EOTeacherUser eoTeacherUser;

	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonBackReference
	public EOCompetition eoCompetition;
	
	
}
