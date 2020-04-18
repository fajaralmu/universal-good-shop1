package com.fajar.service.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ReportCategory;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.dto.TransactionType;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.CashBalance;
import com.fajar.entity.CostFlow;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Transaction;
import com.fajar.repository.CapitalFlowRepository;
import com.fajar.repository.CostFlowRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.service.CashBalanceService;
import com.fajar.util.DateUtil;

@Service
public class PrintedReportService {
	
	@Autowired
	private CostFlowRepository costFlowRepository;
	@Autowired
	private CapitalFlowRepository capitalFlowRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private ExcelReportBuilder excelReportBuilder;
	
	private Map<Integer, List<BaseEntity>> dailyTransactions = new HashMap<>();
	private Map<ReportCategory, DailyReportRow> dailyReportSummary = new HashMap<>();
	private List<DailyReportRow > dailyReportRows = new LinkedList<>();
	

	public ShopApiResponse buildDailyReport(ShopApiRequest request) { 
		
		Filter filter = request.getFilter();
		CashBalance cashBalance = getBalance(filter);
		int month = filter.getMonth();
		int year = filter.getMonth() ;
		
		Integer[] months = DateUtil.getMonths(year);
		Integer dayCount = months[month - 1]; 
		getTransactions(month, year);
		populateDailyReportRows(dayCount, month);
		DailyReportRow totalDailyReportRow = totalDailyReportRow(cashBalance);
		
		excelReportBuilder.writeDailyReport(month, year, cashBalance, 
				dailyReportRows, dailyReportSummary, totalDailyReportRow);
		
		return ShopApiResponse.success();
	} 
	
	private DailyReportRow totalDailyReportRow(CashBalance latestBalance) { 
		DailyReportRow dailyReportRow = new DailyReportRow();
		long debitAmount = latestBalance.getActualBalance();
		long creditAmount = 0l;
		
		for (DailyReportRow dailyReportRow2 : dailyReportRows) {
			debitAmount+=dailyReportRow2.getDebitAmount();
			creditAmount+=dailyReportRow2.getCreditAmount();
		}
		
		dailyReportRow.setDebitAmount(debitAmount);
		dailyReportRow.setCreditAmount(creditAmount);
		
		return dailyReportRow ;
	}
	 
	private void populateDailyReportRows( int dayCount, int month) { 
		 
		dailyReportRows.clear();
		dailyReportSummary.clear();
		
		for (int i = 1; i <= dayCount; i++) {
			
			List<BaseEntity> transactionItems = dailyTransactions.get(i);
			if(null == transactionItems) {
				continue;
			}
			for (BaseEntity baseEntity : transactionItems) {
				DailyReportRow dailyReportRow = getDailyReportRow(i, month, baseEntity);
				addDailyReportRow(dailyReportRow);
				updateSummary(dailyReportRow);
			}
		}
	}
	
	private void addDailyReportRow(DailyReportRow dailyReportRow) {
		dailyReportRows.add(dailyReportRow);
	}
	
	private DailyReportRow getDailyReportRow(int day, int month, BaseEntity baseEntity) { 
		
		DailyReportRow dailyReportRow = new DailyReportRow();
		dailyReportRow.setDay(day);
		dailyReportRow.setMonth(month);
		long creditAmount = 0l;
		long debitAmount = 0l;
		String name = "N/A";
		ReportCategory reportCategory = ReportCategory.CAPITAL;
		
		if(baseEntity instanceof CapitalFlow) {
			CapitalFlow capitalFlow = (CapitalFlow) baseEntity;
			creditAmount = capitalFlow.getNominal();
			reportCategory = ReportCategory.CAPITAL;
			name = "Capital Flow";
			
		}else if(baseEntity instanceof ProductFlow) {
			ProductFlow productFlow = (ProductFlow) baseEntity;
			Transaction transaction = productFlow.getTransaction();
			reportCategory = ReportCategory.SHOP_ITEM;
			
			if(transaction.getType().equals(TransactionType.IN)) {
				debitAmount = productFlow.getCount() * productFlow.getPrice();
			}else {
				creditAmount = productFlow.getCount() * productFlow.getPrice();
			}
			name  = transaction.getCode() + transaction.getType();
			
		}else if(baseEntity instanceof CostFlow) { 
			CostFlow costFlow = (CostFlow) baseEntity;
			debitAmount = costFlow.getNominal();
			name  = costFlow.getDescription();
			reportCategory = ReportCategory.OPERATIONAL_COST;
		} 
		
		dailyReportRow.setCode(reportCategory);
		dailyReportRow.setCreditAmount(creditAmount);
		dailyReportRow.setDebitAmount(debitAmount);
		dailyReportRow.setName(name);
		
		return dailyReportRow ;
	}
	
	private void updateSummary(DailyReportRow dailyReportRow) {
		ReportCategory reportCategory = dailyReportRow.getCode();
		
		if(null == dailyReportSummary.get(reportCategory)) {
			dailyReportSummary.put(reportCategory, new DailyReportRow());
		}
		
		long creditAmount = dailyReportSummary.get(reportCategory).getCreditAmount() + dailyReportRow.getCreditAmount();
		long debitAmount = dailyReportSummary.get(reportCategory).getDebitAmount() + dailyReportRow.getDebitAmount();
		
		dailyReportSummary.get(reportCategory).setCreditAmount(creditAmount);
		dailyReportSummary.get(reportCategory).setDebitAmount(debitAmount);
	}

	/**
	 * put transaction item in dailyTransactions Map
	 * @param day
	 * @param baseEntity
	 */
	private void putTransaction(int day, BaseEntity baseEntity) {
		if(dailyTransactions.get(day) == null) {
			dailyTransactions.put(day, new ArrayList<>());
		}
		dailyTransactions.get(day).add(baseEntity);
	}

	/**
	 * get transaction items in specified month and year
	 * @param month
	 * @param year
	 * @return
	 */
	private List<BaseEntity> getTransactions(int month, int year) {
		dailyTransactions.clear();
		
		List<ProductFlow> transactionItems = productFlowRepository.findByTransactionPeriod(  month, year); 
		List<CapitalFlow> capitalFlows = capitalFlowRepository.findByPeriod(month, year);
		List<CostFlow> costFlows = costFlowRepository.findByPeriod(month, year);
		 
		
		List<BaseEntity> results = new ArrayList<BaseEntity>();
		
		results.addAll(costFlows);
		results.addAll(transactionItems);
		results.addAll(capitalFlows);
		
		for (BaseEntity baseEntity : results) {
			
			Date createdDate = baseEntity.getCreatedDate();
			Calendar calendar = DateUtil.cal(createdDate);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			putTransaction(day, baseEntity);
		}
		
		return results ;
	}




	private CashBalance getBalance(Filter filter) {
		int month = filter.getMonth() - 1 < 0 ? 12 : filter.getMonth() - 1;
		int year = filter.getMonth() - 1 < 0 ? filter.getYear() - 1 : filter.getYear();
		CashBalance cashBalance = cashBalanceService.getBalanceAtTheEndOf(month, year);		
		return cashBalance;
	}
	 
	
	
}
