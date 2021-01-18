package com.vspl.music.model.eo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "EOMESSAGE")
@SequenceGenerator(name = "EOMESSAGE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOMESSAGE_SEQ")
public class EOMessage extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOMESSAGE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "SENDER")
	public long sender;
	
	@Column(name = "RECEIVER")
	public long receiver;
	
	@Column(name = "SENDER_CLASS_NAME")
	public String senderClassName;
	
	@Column(name = "RECEIVER_CLASS_NAME")
	public String receiverClassName;
	
	@Column(name = "MESSAGE_ID")
	public String messageID;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate = new Date(System.currentTimeMillis());


}
