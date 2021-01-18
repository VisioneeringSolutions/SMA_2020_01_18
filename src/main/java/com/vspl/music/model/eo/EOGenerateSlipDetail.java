package com.vspl.music.model.eo;

import java.sql.Date;

import javax.persistence.CascadeType;
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
import com.vspl.music.model.lk.LKClassDuration;
import com.vspl.music.model.lk.LKMusicType;


@Entity
@Table(name = "EOGENERATE_SLIP_DETAIL")
@SequenceGenerator(name = "EOGENERATE_SLIP_DETAIL_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOGENERATE_SLIP_DETAIL_SEQ")

public class EOGenerateSlipDetail extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOGENERATE_SLIP_DETAIL_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "AMOUNT")
	public double amount;
	
	@Column(name = "TOTAL_MINS")
	public double totalMins;
	
	@Column(name = "SESSION")
	public double session;
	
	@Column(name = "CANCELLATION_AMOUNT" ,columnDefinition = "Decimal(20,2) default '0.00'")
	public double cancellationAmount;
	
	@OneToOne
	public EOGenerateSlip eoGenerateSlip;
	
	@OneToOne(optional = true)
	public LKClassDuration lkClassDuration;
	
	@OneToOne(optional = true)
	public LKMusicType lkMusicType;
	
	@OneToOne(optional = true)
	public LKCategoryType lkCategoryType;
}

