package com.vspl.music.model.eo;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.vspl.music.util.DateUtil;

@Entity
@Table(name = "EOMESSAGE_HISTORY")
@SequenceGenerator(name = "EOMESSAGE_HISTORY_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOMESSAGE_HISTORY_SEQ")
public class EOMessageHistory extends EOObject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOMESSAGE_HISTORY_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "MESSAGE", columnDefinition = "Text")
	public String message;
	
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
	
	
	@Column(name = "IS_READ")
	public boolean isRead = false;
	
	@OneToOne
	public EOMessage eoMessage;
	
	@Column(name = "CREATED_DATE_TIME")
	public String createdDateTime = DateUtil.formatedCurrentDateTime();

}
