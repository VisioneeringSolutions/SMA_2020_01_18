package com.vspl.music.model.eo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonBackReference;

@Entity
@Table(name = "EOSTUDENT_BATCH_MAPPING")
@SequenceGenerator(name = "EOSTUDENT_BATCH_MAPPING_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOSTUDENT_BATCH_MAPPING_SEQ")
public class EOStudentBatchMapping extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOSTUDENT_BATCH_MAPPING_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "STUDENT_PK")
	public String studentPk;
	
	@Column(name = "BATCH_PK")
	public String batchPk;

	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonBackReference
	public EOStudentBatch eoStudentBatch;
	

}
