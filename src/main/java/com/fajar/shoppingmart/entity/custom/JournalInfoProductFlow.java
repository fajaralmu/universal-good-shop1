package com.fajar.shoppingmart.entity.custom;

import com.fajar.shoppingmart.dto.CashType;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Transaction;

public class JournalInfoProductFlow extends BalanceJournalInfo<ProductFlow> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2983590409837039514L; 
	
	public JournalInfoProductFlow(ProductFlow productFlow) {
		super(productFlow); 
		
		buildBalanceObject();
	}

	@Override
	public void buildBalanceObject() {
		 
		Transaction transaction = financialEntity.getTransaction(); 
		
		if(transaction.getType().equals(TransactionType.PURCHASING)) {
			//purchase from supplier
			creditAmount 	= financialEntity.getCount() * financialEntity.getPrice();
			cashType = CashType.PURCHASING;
		}else {
			//selling
			debitAmount 	= financialEntity.getCount() * financialEntity.getProduct().getPrice();
			cashType = CashType.SELLING;
		}
		
		referenceInfo 		= "TRAN_"+transaction.getType(); 
		reportCategory 		= ReportCategory.SHOP_ITEM;  
	}

}
