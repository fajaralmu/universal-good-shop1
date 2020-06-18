package com.fajar.shoppingmart.entity.custom;

import java.io.Serializable;
import java.util.Date;

import com.fajar.shoppingmart.dto.CashType;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.entity.CashBalance;

import lombok.Data;

@Data
public abstract class BalanceJournalInfo<FinancialEntityType extends FinancialEntity> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8648679391834607263L;
	protected final Date date;
	protected long debitAmount;
	protected long creditAmount;
	protected String referenceInfo;
	protected CashType cashType;
	protected CashBalance balanceObject; 
	protected ReportCategory reportCategory;
	 
	protected final String transactionName;
	protected final FinancialEntityType financialEntity;
	
	public BalanceJournalInfo(FinancialEntityType financialEntity) {
		 
		this.financialEntity = financialEntity;
		this.date = financialEntity.getTransactionDate();
		this.transactionName  = financialEntity.getTransactionName();
	}

	public CashBalance getBalanceObject() {
		return CashBalance.builder().date(date).creditAmt(creditAmount).debitAmt(debitAmount)
				.type(cashType)
				.referenceInfo(referenceInfo).referenceId(String.valueOf(financialEntity.getId())).build();
	}

	public abstract void buildBalanceObject();
	
	public long getId() {
		return financialEntity.getId();
	}

}
