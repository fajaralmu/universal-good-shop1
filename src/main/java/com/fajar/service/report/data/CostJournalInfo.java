package com.fajar.service.report.data;

import com.fajar.dto.CashType;
import com.fajar.dto.ReportCategory;
import com.fajar.entity.CostFlow;

public class CostJournalInfo extends BalanceJournalInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6427982548966366819L;
	final CostFlow costFlow;

	public CostJournalInfo(CostFlow costFlow) {
		super(costFlow);
		this.costFlow = costFlow;
		
		buildBalanceObject();
	}
 
	@Override
	public void buildBalanceObject() {  
		creditAmount = costFlow.getNominal(); 
		cashType = determineCashType();
		referenceInfo = cashType+"_"+costFlow.getCostType().getName();
		
		reportCategory = ReportCategory.OPERATIONAL_COST;
	}
	
	private CashType determineCashType() { 
		return CashType.GENERAL_COST;
	}

	 

}
