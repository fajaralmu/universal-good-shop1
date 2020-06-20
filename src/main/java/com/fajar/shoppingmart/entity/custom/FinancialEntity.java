package com.fajar.shoppingmart.entity.custom;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface FinancialEntity extends Serializable {
	@JsonIgnore
	public Date getTransactionDate();
	@JsonIgnore
	public String getTransactionName();
	@JsonIgnore
	public long getTransactionNominal();
	@JsonIgnore
	public BalanceJournalInfo<? extends FinancialEntity> getBalanceJournalInfo();
	public Long getId();
}
