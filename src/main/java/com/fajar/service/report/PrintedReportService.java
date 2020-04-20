package com.fajar.service.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import com.fajar.service.LogProxyFactory;
import com.fajar.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	
	private long debitAmount = 0;
	private long count = 0;
	
	private Map<Integer, List<BaseEntity>> dailyTransactions = new HashMap<>();
	private Map<ReportCategory, DailyReportRow> dailyReportSummary = new HashMap<>();
	private List<DailyReportRow > dailyReportRows = new LinkedList<>();
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public ShopApiResponse buildDailyReport(ShopApiRequest request) { 
		
		try {
			clear();  
			Filter filter = request.getFilter();
			int month = filter.getMonth();
			int year = filter.getYear() ;
			
			CashBalance cashBalance = getBalance(filter); 
			Integer[] months = DateUtil.getMonthsDay(year);
			Integer dayCount = months[month - 1]; 
			
			getTransactionsData(month, year);
			populateDailyReportRows(dayCount, month);
			DailyReportRow totalDailyReportRow = totalDailyReportRow(cashBalance);
			
			excelReportBuilder.writeDailyReport(month, year, cashBalance, 
					dailyReportRows, dailyReportSummary, totalDailyReportRow);
			
			
			return ShopApiResponse.success();
		}catch (Exception e) { 
			e.printStackTrace();
			return ShopApiResponse.failed(e.getMessage());
		}finally {
			clear(); 
			System.out.println("DEBIT AMOUNT: "+this.debitAmount);
			System.out.println("COUNT ITEM: "+this.count);
		}
	} 
	
	/**
	 * calculate summary
	 * @param latestBalance
	 * @return
	 */
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
	 
	/**
	 * getting data for daily report row 
	 * @param dayCount
	 * @param month
	 */
	private void populateDailyReportRows( int dayCount, int month) {  
		
		for (int i = 1; i <= dayCount; i++) {
			
			List<BaseEntity> transactionItems = getDailyTransactions(i);
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
	
	/**
	 * clear dailyReportRows and dailyReportSummary
	 */
	private void clear() { 

		count = 0l;
		debitAmount = 0l; 
		dailyReportRows.clear();
		dailyReportSummary.clear();
	}

	/**
	 * get transaction items in the given day
	 * @param day
	 * @return
	 */
	private List<BaseEntity> getDailyTransactions(int day) { 
		 
		List<BaseEntity> rawTransactions = dailyTransactions.get(day); 
		return rawTransactions;
	}
	
	/**
	 * check if row data with given category and day is exist
	 * @param reportCategory
	 * @param day
	 * @return
	 */
	private int isExistInDailyReportRow(ReportCategory reportCategory, int day) {
		int i = 0;
		for (DailyReportRow dailyReportRow : dailyReportRows) {
			if(dailyReportRow.getCategory().equals(reportCategory) && dailyReportRow.getDay() == day ) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * add row data to list
	 * @param newDailyReportRow
	 */
	private void addDailyReportRow(DailyReportRow newDailyReportRow) { 
		
		if(newDailyReportRow.getCategory().equals(ReportCategory.SHOP_ITEM)) {
			int index = isExistInDailyReportRow(ReportCategory.SHOP_ITEM, newDailyReportRow.getDay());
			if(index >= 0) { 
				dailyReportRows.get(index).addCreditAmount( newDailyReportRow.getCreditAmount());
				dailyReportRows.get(index).addDebitAmount( newDailyReportRow.getDebitAmount());
				
			}else {
				dailyReportRows.add(newDailyReportRow);
			}
		}else {
			dailyReportRows.add(newDailyReportRow);
		}
	}
	
	/**
	 * build row data
	 * @param day
	 * @param month
	 * @param baseEntity
	 * @return
	 */
	private DailyReportRow getDailyReportRow(int day, int month, BaseEntity baseEntity) { 
		
		DailyReportRow dailyReportRow = new DailyReportRow();
		dailyReportRow.setDay(day);
		dailyReportRow.setMonth(month);
		
		long creditAmount = 0l;
		long debitAmount = 0l;
		String name = "N/A";
		ReportCategory reportCategory = ReportCategory.CAPITAL;
		
		if(baseEntity instanceof CapitalFlow) {
			final CapitalFlow capitalFlow = (CapitalFlow) baseEntity;
			debitAmount = capitalFlow.getNominal();
			reportCategory = ReportCategory.CAPITAL;
			name = "Dana "+capitalFlow.getCapitalType().getName();
			
		}else if(baseEntity instanceof ProductFlow) {
			final ProductFlow productFlow = (ProductFlow) baseEntity;
			final Transaction transaction = productFlow.getTransaction();
			reportCategory = ReportCategory.SHOP_ITEM;
			
			if(transaction.getType().equals(TransactionType.IN)) {
				creditAmount = productFlow.getCount() * productFlow.getPrice();
			}else if(transaction.getType().equals(TransactionType.OUT)) {
				debitAmount = productFlow.getCount() * productFlow. getPrice();
			}
			name  = "SELLING/PURCHASING";
			
		}else if(baseEntity instanceof CostFlow) { 
			final CostFlow costFlow = (CostFlow) baseEntity;
			creditAmount = costFlow.getNominal();
			name  = costFlow.getDescription();
			reportCategory = ReportCategory.OPERATIONAL_COST;
		} 
		
		dailyReportRow.setCategory(reportCategory);
		dailyReportRow.setCreditAmount(creditAmount);
		dailyReportRow.setDebitAmount(debitAmount);
		dailyReportRow.setName(name);
		this.count++;
		this.debitAmount+=debitAmount;
		
		return dailyReportRow ;
	}
	
	/**
	 * set debit and credit in summary row
	 * @param reportCategory
	 * @param creditAmount
	 * @param debitAmount
	 */
	public void updateCreditAndDebitSummary(ReportCategory reportCategory, long creditAmount, long debitAmount) {
		dailyReportSummary.get(reportCategory).setCreditAmount(creditAmount);
		dailyReportSummary.get(reportCategory).setDebitAmount(debitAmount);
	}
	
	/**
	 * update accumulation for debit and credit
	 * @param dailyReportRow
	 */
	private void updateSummary(DailyReportRow dailyReportRow) {
		ReportCategory reportCategory = dailyReportRow.getCategory();
		DailyReportRow existingSummaryRow = getSummary(reportCategory); 
		
		long creditAmount = existingSummaryRow.getCreditAmount() + dailyReportRow.getCreditAmount();
		long debitAmount = existingSummaryRow.getDebitAmount() + dailyReportRow.getDebitAmount();
		
		updateCreditAndDebitSummary(reportCategory, creditAmount, debitAmount); 
	}

	/**
	 * get summary row from Map, if null then return new instance
	 * @param reportCategory
	 * @return
	 */
	private DailyReportRow getSummary(ReportCategory reportCategory) { 
		if(null == dailyReportSummary.get(reportCategory)) {
			dailyReportSummary.put(reportCategory, new DailyReportRow());
		}
		
		return this.dailyReportSummary.get(reportCategory);
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
	private List<BaseEntity> getTransactionsData(int month, int year) {
		
		log.info("getTransactions, month: {}, year: {}", month, year);
		dailyTransactions.clear();
		
		List<ProductFlow> transactionItems = productFlowRepository.findByTransactionPeriod(  month, year); 
		List<CapitalFlow> capitalFlows = capitalFlowRepository.findByPeriod(month, year);
		List<CostFlow> costFlows = costFlowRepository.findByPeriod(month, year); 
		
		final List<BaseEntity> results = new ArrayList<BaseEntity>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -9006495340734852418L;

			{
				addAll(costFlows); 
				addAll(transactionItems); 
				addAll(capitalFlows);
			}
		};  
		
		log.info("results count: {}", results.size());
		
		for (BaseEntity baseEntity : results) { 
			
			//don't access the credit & debt value
			Date transactionDate = CashBalanceService.mapCashBalance(baseEntity).getDate(); 
			
			Calendar calendar = DateUtil.cal(transactionDate);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			 
			putTransaction(day, baseEntity);
		}
		
		return results ;
	}

	/**
	 * get cash balance at given period filter
	 * @param filter
	 * @return
	 */
	private CashBalance getBalance(Filter filter) {
		int prevMonth = filter.getMonth();// - 1 < 1 ? 12 : filter.getMonth() - 1;
		int year = filter.getYear();// filter.getMonth() - 1 < 1 ? filter.getYear() - 1 : filter.getYear();
		CashBalance cashBalance = cashBalanceService.getBalanceBefore (prevMonth, year);	 
		return cashBalance;
	}
	 
	
	
}
