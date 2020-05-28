package com.fajar.shoppingmart.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.CapitalFlow;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.entity.CostFlow;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.custom.BalanceJournalInfo;
import com.fajar.shoppingmart.entity.custom.FinancialEntity;
import com.fajar.shoppingmart.repository.CashBalanceRepository;
import com.fajar.shoppingmart.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CashBalanceService {
	@Autowired
	private CashBalanceRepository cashBalanceRepository; 
	
	public CashBalance getBalanceByTransactionItem(FinancialEntity baseEntity) {
		BalanceJournalInfo journalInfo = baseEntity.getBalanceJournalInfo();
		log.info("getBalanceByTransactionItem: {} {}", journalInfo.getId(), baseEntity.getClass());
	 
		CashBalance balance = cashBalanceRepository.findTop1ByTypeAndReferenceId(journalInfo.getCashType(), 
				String.valueOf(journalInfo.getId()));
		
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
		cashBalance.setCreditAmt(Long.valueOf(result[0].toString()));
		cashBalance.setDebitAmt(Long.valueOf(result[1].toString()));
		cashBalance.setActualBalance(Long.valueOf(result[2].toString()));
		
		return cashBalance;
	}
	
	/**
	 * set values for cash balance based on given entity
	 * @param baseEntity
	 * @return
	 */
	public static CashBalance mapCashBalance(FinancialEntity baseEntity) {
		
		BalanceJournalInfo journalInfo = baseEntity.getBalanceJournalInfo();
		return journalInfo.getBalanceObject();
	}
	
	/**
	 * update balance
	 * @param baseEntity
	 */
	public synchronized void updateCashBalance(FinancialEntity baseEntity) {
		
		if(baseEntity == null || baseEntity.getId() == null) {
			return;
		} 
		
		CashBalance existingRecord = getBalanceByTransactionItem(baseEntity);
		
		CashBalance cashBalance = existingRecord == null ? new CashBalance() : existingRecord;
		
		final CashBalance mappedCashBalanceInfo = mapCashBalance(baseEntity);
		final long creditAmount = mappedCashBalanceInfo.getCreditAmt();
		final long debitAmount = mappedCashBalanceInfo.getDebitAmt();
		final String info = mappedCashBalanceInfo.getReferenceInfo(); 
		final Date date = mappedCashBalanceInfo.getDate();
		
//		cashBalance.setFormerBalance(formerBalance);
		cashBalance.setDebitAmt(debitAmount);
		cashBalance.setCreditAmt(creditAmount);
//		cashBalance.setActualBalance(formerBalance + creditAmount - debitAmount);
		cashBalance.setReferenceInfo(info);
		cashBalance.setReferenceId(String.valueOf(baseEntity.getId()));
		cashBalance.setDate(date); 
		cashBalance.setModifiedDate(new Date()); 
		
		cashBalanceRepository.save(cashBalance);
	}
 

}
