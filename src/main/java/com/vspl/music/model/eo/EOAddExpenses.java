
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

@Entity
@Table(name = "EOADD_EXPENSES")
@SequenceGenerator(name = "EOADD_EXPENSES_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOADD_EXPENSES_SEQ")

public class EOAddExpenses extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOADD_EXPENSES_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "EXPENSE_DATE")
	public Date expenseDate;
	
	@Column(name = "ACCOUNT_TYPE")
	public String accountType;
	
	@Column(name = "ACCOUNT_NAME")
	public String accountName;
	
	@Column(name = "DESCRIPTION")
	public String descriptions; 
	
	@Column(name = "MONTH")
	public String month;
	
	@Column(name = "YEAR")
	public String year;
	
	@Column(name = "AMOUNT")
	public Double amount; 
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	public EOAddExpenses(){
		
	}

	
}

