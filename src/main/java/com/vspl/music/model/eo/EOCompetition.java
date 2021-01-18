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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;

@Entity
@Table(name = "EOCOMPETITION")
@SequenceGenerator(name = "EOCOMPETITION_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOCOMPETITION_SEQ")


public class EOCompetition extends EOObject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOCOMPETITION_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "NAME")
	public String name;
	
	@Column(name = "COMPETITION_DATE")
	public String competitionDate;
	
	@Column(name = "DETAILS",  columnDefinition = "Text")
	public String details;
	
	@Column(name = "MUSIC_PK")
	public String musicPk;
	
	@Column(name = "PREREQUISITE",  columnDefinition = "Text")
	public String prerequisite;
	
	@Column(name = "ORGANIZING_AUTHORITY",  columnDefinition = "Text")
	public String organizingAuthority;
	
	@Column(name = "VENUE",  columnDefinition = "Text")
	public String venue;
	
	@Column(name = "BROCHURE_Url",  columnDefinition = "Text")
	public String brochureUrl;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	/*@Column(name = "STUDENTS_PK")
	public String studentsPk;
	
	@Column(name = "TEACHERS_PK")
	public String teachersPk;*/
	
	@OneToMany(mappedBy = "eoCompetition", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JsonManagedReference
	public List<EOCompetitionMapping> eoCompetitionMapping = new LinkedList<>();
	
	public EOCompetition () {
		
	}

	public EOCompetition(String name, String competitionDate, String details, String musicPk, String prerequisite,
			String organizingAuthority, String venue, String brochureUrl, Date createdDate, Date updatedDate,
			boolean isActive, List<EOCompetitionMapping> eoCompetitionMapping) {
		super();
		this.name = name;
		this.competitionDate = competitionDate;
		this.details = details;
		this.musicPk = musicPk;
		this.prerequisite = prerequisite;
		this.organizingAuthority = organizingAuthority;
		this.venue = venue;
		this.brochureUrl = brochureUrl;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.isActive = isActive;
		this.eoCompetitionMapping = eoCompetitionMapping;
	}

}
