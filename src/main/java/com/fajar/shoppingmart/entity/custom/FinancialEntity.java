package com.fajar.shoppingmart.entity.custom;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface FinancialEntity {
	@JsonIgnore
	public Date getTransactionDate();
	@JsonIgnore
	public String getTransactionName();
	@JsonIgnore
	public long getTransactionNominal();
	@JsonIgnore
	public BalanceJournalInfo getBalanceJournalInfo();
	public Long getId();
}
