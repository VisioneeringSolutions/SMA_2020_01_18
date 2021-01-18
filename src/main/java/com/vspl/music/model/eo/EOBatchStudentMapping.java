package com.vspl.music.model.eo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "EOBATCH_STUDENT_MAPPING")
@SequenceGenerator(name = "EOBATCH_STUDENT_MAPPING_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOBATCH_STUDENT_MAPPING_SEQ")

public class EOBatchStudentMapping extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOBATCH_STUDENT_MAPPING_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "STUDENT_PK")
	public String studentPk;
	
	@Column(name = "LKMUSIC_TYPE_PK")
	public String lkMusicTypePk;
	
	public EOBatchStudentMapping (){
		
	}

	public EOBatchStudentMapping(String studentPk, String lkMusicTypePk) {
		super();
		this.studentPk = studentPk;
		this.lkMusicTypePk = lkMusicTypePk;
	}

	
}

