package com.fajar.shoppingmart.service.entity;

import java.util.Date;

import com.fajar.shoppingmart.service.report.data.BalanceJournalInfo;

public interface FinancialEntity {

	public Date getTransactionDate();
	public String getTransactionName();
	public long getTransactionNominal();
	public BalanceJournalInfo getBalanceJournalInfo();
	public Long getId();
}
