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
	
//	public CashBalance getLatestCashBalance() {
//		
//		return cashBalanceRepository.findTop1ByOrderByIdDesc();
//	}
	
	public CashBalance getBalanceByTransactionItem(BaseEntity baseEntity) {
		
		log.info("getBalanceByTransactionItem: {} {}", baseEntity.getId(), baseEntity.getClass());
		
		String reffInfo = "CAPITAL";
		if(baseEntity instanceof ProductFlow) {
			
			ProductFlow productFlow = (ProductFlow) baseEntity;
			Transaction transaction = productFlow.getTransaction(); 
			
			reffInfo = "TRAN_"+transaction.getType();
			
		}else if(baseEntity instanceof CostFlow) {
			 
			reffInfo = "COST";
			
		}else if(baseEntity instanceof CapitalFlow) { 
			
			reffInfo = "CAPITAL";
		}
		CashBalance balance = cashBalanceRepository.getByReferenceInfoAndReferenceId(reffInfo, 
				String.valueOf(baseEntity.getId()));
		
		log.info("existing balance:{}", balance);
		
		return balance;
	}
	
	/**
	 * get balance at the end of month
	 * @param month starts at 1
	 * @param year
	 * @return
	 */
	public CashBalance getBalanceBefore (int month, int year) { 
		
		Date date = DateUtil.getDate(year, month-1, 1);
		String dateString = DateUtil.formatDate(date, "yyyy-MM-dd");
		
		Object object = cashBalanceRepository.getBalanceBefore(dateString ); 
		Object[] result = (Object[]) object;
		
		CashBalance cashBalance = new CashBalance();
		cashBalance.setCreditAmount(Long.valueOf(result[0].toString()));
		cashBalance.setDebitAmount(Long.valueOf(result[1].toString()));
		cashBalance.setActualBalance(Long.valueOf(result[2].toString()));
		
		return cashBalance;
	}
	
	private static CashBalance mapCashBalance(BaseEntity baseEntity) {
		long creditAmount = 0l;
		long debitAmount = 0l;
		String info = "";
		Date date = new Date();
		
		if(baseEntity instanceof ProductFlow) {
			
			ProductFlow productFlow = (ProductFlow) baseEntity;
			Transaction transaction = productFlow.getTransaction(); 
			
			if(transaction.getType().equals(TransactionType.IN)) {
				debitAmount = productFlow.getCount() * productFlow.getPrice();
			}else {
				creditAmount = productFlow.getCount() * productFlow.getProduct().getPrice();
			}
			
			info = "TRAN_"+transaction.getType();
			date = productFlow.getTransaction().getTransactionDate();
			
		}else if(baseEntity instanceof CostFlow) {
			
			CostFlow costFlow = (CostFlow) baseEntity;
			debitAmount = costFlow.getNominal();
			
			info = "COST_"+costFlow.getCostType().getName();
			date = costFlow.getDate();
			
		}else if(baseEntity instanceof CapitalFlow) {
			
			CapitalFlow capitalFlow = (CapitalFlow) baseEntity;
			creditAmount = capitalFlow.getNominal();
			
			info = "CAPITAL_"+capitalFlow.getCapitalType().getName();
			date = capitalFlow.getDate();
		}
		return CashBalance.builder().date(date).creditAmount(creditAmount).debitAmount(debitAmount).referenceInfo(info).build();
	}
	
	/**
	 * update balance
	 * @param baseEntity
	 */
	public synchronized void updateCashBalance(BaseEntity baseEntity) {
		
		if(baseEntity == null || baseEntity.getId() == null) {
			return;
		} 
		
		CashBalance existingRecord = getBalanceByTransactionItem(baseEntity);
		
		CashBalance cashBalance = existingRecord == null ? new CashBalance() : existingRecord;
		
		final CashBalance mappedCashBalanceInfo = mapCashBalance(baseEntity);
		final long creditAmount = mappedCashBalanceInfo.getCreditAmount();
		final long debitAmount = mappedCashBalanceInfo.getDebitAmount();
		final String info = mappedCashBalanceInfo.getReferenceInfo(); 
		final Date date = mappedCashBalanceInfo.getDate();
		
//		cashBalance.setFormerBalance(formerBalance);
		cashBalance.setDebitAmount(debitAmount);
		cashBalance.setCreditAmount(creditAmount);
//		cashBalance.setActualBalance(formerBalance + creditAmount - debitAmount);
		cashBalance.setReferenceInfo(info);
		cashBalance.setReferenceId(String.valueOf(baseEntity.getId()));
		cashBalance.setDate(date); 
		cashBalance.setModifiedDate(new Date()); 
		
		cashBalanceRepository.save(cashBalance);
	}
 

}
