package com.fajar.shoppingmart.entity.custom;

import com.fajar.shoppingmart.dto.CashType;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.entity.CapitalFlow;

public class JournalInfoCapitalFlow extends BalanceJournalInfo<CapitalFlow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8279681237082762899L; 
	
	public JournalInfoCapitalFlow(CapitalFlow capitalFlow) {
		super(capitalFlow);
		 
		buildBalanceObject();
	}

	@Override
	public void buildBalanceObject() {   
		cashType = CashType.CAPITAL;
		debitAmount = financialEntity.getNominal(); 
		referenceInfo = "CAPITAL_"+financialEntity.getCapitalType().getName(); 
		reportCategory = ReportCategory.CAPITAL; 
	}

}
