package com.vspl.music.model.eo;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.vspl.music.model.lk.LKCategoryType;
import com.vspl.music.model.lk.LKMusicType;

@Entity
@Table(name = "EOTIME_SLOT_ALLOCATION")
@SequenceGenerator(name = "EOTIME_SLOT_ALLOCATION_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOTIME_SLOT_ALLOCATION_SEQ")
public class EOTimeSlotAllocation extends EOObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOTIME_SLOT_ALLOCATION_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "STATUS")
	public String status;
	
	@OneToOne(optional = true)
	public EOTeacherUser eoTeacherUser;
	
	@OneToOne(optional = true)
	public EOStudentUser eoStudentUser;
	
	@OneToOne(optional = true)
	public EOTimeSlot eoTimeSlot;
	
	@OneToOne(optional = true)
	public EOBatch eoBatch;
	
	@OneToOne(optional = true)
	public EOMusicRoom eoRoom;
	
	@Column(name = "STUDENT_CANCELLATION" ,columnDefinition = "boolean default false")
	public boolean studentCancellation = false;
	
	@Column(name = "TEACHER_CANCELLATION" ,columnDefinition = "boolean default false")
	public boolean teacherCancellation = false;
	
	@Column(name = "ADMIN_CANCELLATION" ,columnDefinition = "boolean default false")
	public boolean adminCancellation = false;
	
	@Column(name = "CANCELLATION_AMOUNT")
	public double cancellationAmount;
	
	@OneToOne(optional = true)
	public LKMusicType lkMusicType;
	
	@OneToOne(optional = true)
	public LKCategoryType lkCategoryType;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "CANCELLATION_REQUEST_DATE")
	public String cancellationRequestDate;
	
	@Column(name = "CANCELLATION_PERCENTAGE")
	public String cancellationPercentage;
	
	@Column(name = "IS_TEACHER_PAID" ,columnDefinition = "boolean default true")
	public boolean isTeacherPaid = true;
	
	public EOTimeSlotAllocation (){
		
	}

	public EOTimeSlotAllocation(String status, EOTeacherUser eoTeacherUser, EOStudentUser eoStudentUser,
			EOTimeSlot eoTimeSlot, EOBatch eoBatch, EOMusicRoom eoRoom, boolean studentCancellation,
			boolean teacherCancellation, double cancellationAmount, LKMusicType lkMusicType, Date createdDate,
			Date updatedDate) {
		super();
		this.status = status;
		this.eoTeacherUser = eoTeacherUser;
		this.eoStudentUser = eoStudentUser;
		this.eoTimeSlot = eoTimeSlot;
		this.eoBatch = eoBatch;
		this.eoRoom = eoRoom;
		this.studentCancellation = studentCancellation;
		this.teacherCancellation = teacherCancellation;
		this.cancellationAmount = cancellationAmount;
		this.lkMusicType = lkMusicType;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}


}
