package com.fajar.shoppingmart.entity.custom;

import com.fajar.shoppingmart.dto.CashType;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.entity.CapitalFlow;

public class CapitalFlowJournalInfo extends BalanceJournalInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8279681237082762899L;
	final CapitalFlow capitalFlow;
	
	public CapitalFlowJournalInfo(CapitalFlow capitalFlow) {
		super(capitalFlow);
		this.capitalFlow = capitalFlow; 
		 
		buildBalanceObject();
	}

	@Override
	public void buildBalanceObject() {   
		cashType = CashType.CAPITAL;
		debitAmount = capitalFlow.getNominal(); 
		referenceInfo = "CAPITAL_"+capitalFlow.getCapitalType().getName(); 
		reportCategory = ReportCategory.CAPITAL; 
	}

}
