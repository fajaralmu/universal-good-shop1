package com.fajar.shoppingmart.entity.custom;

import java.util.Date;

public interface FinancialEntity {

	public Date getTransactionDate();
	public String getTransactionName();
	public long getTransactionNominal();
	public BalanceJournalInfo getBalanceJournalInfo();
	public Long getId();
}
