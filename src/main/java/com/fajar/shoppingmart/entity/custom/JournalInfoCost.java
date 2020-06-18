package com.fajar.shoppingmart.entity.custom;

import com.fajar.shoppingmart.dto.CashType;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.entity.CostFlow;

public class JournalInfoCost extends BalanceJournalInfo<CostFlow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6427982548966366819L; 

	public JournalInfoCost(CostFlow costFlow) {
		super(costFlow); 
		
		buildBalanceObject();
	}
 
	@Override
	public void buildBalanceObject() {  
		creditAmount = financialEntity.getNominal(); 
		cashType = determineCashType();
		referenceInfo = cashType+"_"+financialEntity.getCostType().getName();
		
		reportCategory = ReportCategory.OPERATIONAL_COST;
	}
	
	private CashType determineCashType() { 
		return CashType.GENERAL_COST;
	}

	 

}
