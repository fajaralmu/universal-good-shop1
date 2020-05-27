package com.fajar.service.report;

import com.fajar.dto.ReportCategory;
import com.fajar.dto.TransactionType;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Transaction;

public class ProductFlowJournalInfo extends BalanceJournalInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2983590409837039514L;
	private final ProductFlow productFlow;
	
	public ProductFlowJournalInfo(ProductFlow productFlow) {
		super(productFlow);
		this.productFlow = productFlow;
		
		buildBalanceObject();
	}

	@Override
	public void buildBalanceObject() {
		 
		Transaction transaction = productFlow.getTransaction(); 
		
		if(transaction.getType().equals(TransactionType.IN)) {
			//purchase from supplier
			creditAmount 	= productFlow.getCount() * productFlow.getPrice();
		}else {
			//selling
			debitAmount 	= productFlow.getCount() * productFlow.getProduct().getPrice();
		}
		
		referenceInfo 		= "TRAN_"+transaction.getType(); 
		reportCategory 		= ReportCategory.SHOP_ITEM;  
	}

}
