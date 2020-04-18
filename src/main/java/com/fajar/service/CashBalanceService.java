package com.fajar.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.TransactionType;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.CashBalance;
import com.fajar.entity.CostFlow;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Transaction;
import com.fajar.repository.CashBalanceRepository;
import com.fajar.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CashBalanceService {
	@Autowired
	private CashBalanceRepository cashBalanceRepository;
	
	public CashBalance getLatestCashBalance() {
		
		return cashBalanceRepository.findTop1ByOrderByIdDesc();
	}
	
	
	/**
	 * get balance at the end of month
	 * @param month starts at 1
	 * @param year
	 * @return
	 */
	public CashBalance getBalanceAt (int month, int year) { 
		
		CashBalance CashBalance = cashBalanceRepository.getCashBalanceAt(month, year );
		
		return CashBalance;
	}
	
	/**
	 * update balance
	 * @param baseEntity
	 */
	public synchronized void update(BaseEntity baseEntity) {
		
		if(baseEntity == null || baseEntity.getId() == null) {
			return;
		}
		CashBalance latestCashbalance = cashBalanceRepository.findTop1ByOrderByIdDesc();
		
		long formerBalance = latestCashbalance == null? 0l : latestCashbalance.getActualBalance();
		long creditAmount = 0l;
		long debitAmount = 0l;
		String info = "-";
		
		CashBalance cashBalance = new CashBalance();
		
		if(baseEntity instanceof ProductFlow) {
			
			ProductFlow productFlow = (ProductFlow) baseEntity;
			Transaction transaction = productFlow.getTransaction(); 
			
			if(transaction.getType().equals(TransactionType.IN)) {
				debitAmount = productFlow.getCount() * productFlow.getPrice();
			}else {
				creditAmount = productFlow.getCount() * productFlow.getProduct().getPrice();
			}
			
			info = "TRAN_"+transaction.getType();
			
		}else if(baseEntity instanceof CostFlow) {
			
			CostFlow costFlow = (CostFlow) baseEntity;
			debitAmount = costFlow.getNominal();
			
			info = "COST_"+costFlow.getCostType().getName();
			
		}else if(baseEntity instanceof CapitalFlow) {
			
			CapitalFlow capitalFlow = (CapitalFlow) baseEntity;
			creditAmount = capitalFlow.getNominal();
			
			info = "CAPITAL_"+capitalFlow.getCapitalType().getName();
		}else {
			return;
		}
		
		if(latestCashbalance == null) {
			formerBalance = 0l;
		}
		cashBalance.setFormerBalance(formerBalance);
		cashBalance.setDebitAmount(debitAmount);
		cashBalance.setCreditAmount(creditAmount);
		cashBalance.setActualBalance(formerBalance + creditAmount - debitAmount);
		cashBalance.setReferenceInfo(info);
		cashBalance.setReferenceId(baseEntity.getId().toString());
		cashBalance.setDate(new Date()); 
		
		cashBalanceRepository.save(cashBalance);
	}
 

}
