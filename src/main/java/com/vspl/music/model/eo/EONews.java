package com.vspl.music.model.eo;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "EONEWS")
@SequenceGenerator(name = "EONEWS_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EONEWS_SEQ")
public class EONews extends EOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EONEWS_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "NEWS_DATE")
	public String newsDate;

	@Column(name = "REMOVING_REASON")
	public String removingReason;
	
	@Column(name = "NEWS_DESC",  columnDefinition = "Text")
	public String newsDesc;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate = new Date(System.currentTimeMillis());
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate = new Date(System.currentTimeMillis());

	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;

	@ManyToMany(fetch = FetchType.EAGER)
	public List<EOImage> eoAttachmentArray = new LinkedList<>();
	
	@OneToOne(optional = true)
	public EOImage eoImage;
	
	public EONews(){
	
	}

	public EONews(String newsDate, String newsDesc, Date createdDate, Date updatedDate, boolean isActive,
			List<EOImage> eoAttachmentArray, EOImage eoImage) {
		super();
		this.newsDate = newsDate;
		this.newsDesc = newsDesc;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.isActive = isActive;
		this.eoAttachmentArray = eoAttachmentArray;
		this.eoImage = eoImage;
	}

	

	}
