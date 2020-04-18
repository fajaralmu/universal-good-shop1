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

@Service
public class CashBalanceService {
	@Autowired
	private CashBalanceRepository cashBalanceRepository;
	
	public CashBalance getLatestCashBalance() {
		
		return cashBalanceRepository.findTop1ByOrderByIdDesc();
	}
	
	
	/**
	 * get balance at the end of month
	 * @param month
	 * @param year
	 * @return
	 */
	public CashBalance getBalanceAtTheEndOf(int month, int year) {
		
		int nextMonth = month + 1 > 12 ? 1 : month + 1;
		int theYear = month + 1 > 12 ? year + 1 : year;
		
		Date date = DateUtil.getDate(theYear, nextMonth - 1, 1);
		String dateString = DateUtil.formatDate(date, "yyyy-MM-dd");
		
		CashBalance CashBalance = cashBalanceRepository.getCashAtMonthAndYear(dateString );
		
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
