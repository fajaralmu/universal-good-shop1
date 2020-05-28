package com.fajar.shoppingmart.entity.custom;

import com.fajar.shoppingmart.dto.CashType;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Transaction;

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
			cashType = CashType.PURCHASING;
		}else {
			//selling
			debitAmount 	= productFlow.getCount() * productFlow.getProduct().getPrice();
			cashType = CashType.SELLING;
		}
		
		referenceInfo 		= "TRAN_"+transaction.getType(); 
		reportCategory 		= ReportCategory.SHOP_ITEM;  
	}

}
