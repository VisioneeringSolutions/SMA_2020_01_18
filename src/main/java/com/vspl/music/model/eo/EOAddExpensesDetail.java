
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
@Table(name = "EOADD_EXPENSES_DETAIL")
@SequenceGenerator(name = "EOADD_EXPENSES_DETAIL_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOADD_EXPENSES_DETAIL_SEQ")

public class EOAddExpensesDetail extends EOObject{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOADD_EXPENSES_DETAIL_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name = "SUB_ACCOUNT_NAME")
	public String subAccountName;
	
	@Column(name = "EXPENSE_DATE")
	public Date expenseDate;
	
	@Column(name = "DESCRIPTION")
	public String descriptions; 
	
	@Column(name = "AMOUNT")
	public Double amount; 
	
	@Column(name = "IS_ACTIVE" ,columnDefinition = "boolean default true")
	public boolean isActive = true;
	
	@OneToOne
	public EOAddExpenses eoAddExpenses;
	
	public EOAddExpensesDetail(){
		
	}

	
}

