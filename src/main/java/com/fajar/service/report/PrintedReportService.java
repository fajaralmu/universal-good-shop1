package com.fajar.service.report;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ReportCategory;
import com.fajar.dto.WebRequest;
import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.CashBalance;
import com.fajar.entity.CostFlow;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.setting.EntityProperty;
import com.fajar.repository.CapitalFlowRepository;
import com.fajar.repository.CostFlowRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.service.CashBalanceService;
import com.fajar.service.EntityService;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.entity.FinancialEntity;
import com.fajar.util.DateUtil;
import com.fajar.util.EntityUtil;

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
	@Autowired
	private EntityService entityService;
	@Autowired
	private EntityReportBuilder entityReportBuilder;
	
	private long debitAmount = 0;
	private long count = 0;
	
	/**
	 * daily report
	 */
	private final Map<Integer, List<FinancialEntity>> dailyTransactions = new HashMap<>();
	private final Map<ReportCategory, DailyReportRow> dailyReportSummary = new HashMap<>();
	private final List<DailyReportRow > dailyReportRows = new LinkedList<>();
	private DailyReportRow dailyReportRowTotal = new DailyReportRow();
	private CashBalance dailyReportInitialBalance = new CashBalance();
	
	/**
	 * monthly report
	 */
	private final Map<Integer, Map<ReportCategory, DailyReportRow>> monthyReportContent = new HashMap<>();
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public File buildDailyReport(WebRequest request) { 
		
		try {
			
			Filter filter = request.getFilter();   
			
			getTransactionRecords(filter);
			
			ReportRequest reportRequest = generateDailyReportRequest(filter);
			
			File result = excelReportBuilder.getDailyReportFile(reportRequest);
			
			
			return result;
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}finally {
			clearDailyReport(); 
			System.out.println("DEBIT AMOUNT: "+this.debitAmount);
			System.out.println("COUNT ITEM: "+this.count);
		}
	} 
	
	/**
	 * generate report request for daily report
	 * @param filter
	 * @return
	 */
	private ReportRequest generateDailyReportRequest(Filter filter) {
		ReportRequest reportRequest = new ReportRequest();
		reportRequest.setFilter(filter);
		reportRequest.setDailyReportRows(dailyReportRows);
		reportRequest.setDailyReportSummary(dailyReportSummary);
		reportRequest.setInitialBalance(dailyReportInitialBalance);
		reportRequest.setTotalDailyReportRow(dailyReportRowTotal);
		return reportRequest;
	}
	
	/**
	 * gather required data from database
	 * @param filter
	 */
	private void getTransactionRecords(Filter filter ) { 
		int month = filter.getMonth();
		int year = filter.getYear() ;
		
		Integer[] months = DateUtil.getMonthsDay(year);
		Integer dayCount = months[month - 1];
		clearDailyReport();
		getInitialBalance(filter);  
		getTransactionsData(month, year);
		populateDailyReportRows(dayCount, month);
		calculateTotalSummary( );
		
	}

	/**
	 * calculate summary
	 * @param latestBalance
	 * @return
	 */
	private DailyReportRow calculateTotalSummary( ) { 
		DailyReportRow dailyReportRow = new DailyReportRow();
		long debitAmount = dailyReportInitialBalance.getActualBalance();
		long creditAmount = 0l;
		
		for (DailyReportRow dailyReportRow2 : dailyReportRows) {
			debitAmount+=dailyReportRow2.getDebitAmount();
			creditAmount+=dailyReportRow2.getCreditAmount();
		}
		
		dailyReportRow.setDebitAmount(debitAmount);
		dailyReportRow.setCreditAmount(creditAmount);
		 
		dailyReportRowTotal = dailyReportRow; 
		return dailyReportRow;
	}
	 
	/**
	 * getting data for daily report row 
	 * @param dayCount
	 * @param month
	 */
	private void populateDailyReportRows( int dayCount, int month) {  
		
		for (int i = 1; i <= dayCount; i++) {
			
			List<FinancialEntity> transactionItems = getDailyTransactions(i);
			if(null == transactionItems) {
				continue;
			}
			for (FinancialEntity baseEntity : transactionItems) {
				DailyReportRow dailyReportRow = mapDailyReportRow(i, month, baseEntity);
				addDailyReportRow(dailyReportRow);
				updateSummary(dailyReportRow);
			}
		}
	}
	
	/**
	 * clear dailyReportRows and dailyReportSummary
	 */
	private void clearDailyReport() { 

		count = 0l;
		debitAmount = 0l; 
		dailyReportRows.clear();
		dailyReportSummary.clear();
	}
	
	private void clearMontlyReport() {

		monthyReportContent.clear();
	}

	/**
	 * get transaction items in the given day
	 * @param day
	 * @return
	 */
	private List<FinancialEntity> getDailyTransactions(int day) { 
		 
		List<FinancialEntity> rawTransactions = dailyTransactions.get(day); 
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
	private static DailyReportRow mapDailyReportRow(int day, int month, FinancialEntity baseEntity) { 
		
		DailyReportRow dailyReportRow = new DailyReportRow();
		dailyReportRow.setDay(day);
		dailyReportRow.setMonth(month);
		
		BalanceJournalInfo journalInfo = baseEntity.getBalanceJournalInfo();
		
		long creditAmount =  journalInfo.getCreditAmount();
		long debitAmount = journalInfo.getDebitAmount();
		String transactioName = journalInfo.getTransactionName(); 
		ReportCategory reportCategory = journalInfo.getReportCategory();
		
		dailyReportRow.setCategory(reportCategory );
		dailyReportRow.setCreditAmount(creditAmount);
		dailyReportRow.setDebitAmount(debitAmount);
		dailyReportRow.setName(transactioName); 
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
	private void putTransaction(int day, FinancialEntity baseEntity) {
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
	private List<FinancialEntity> getTransactionsData(int month, int year) {
		
		log.info("getTransactions, month: {}, year: {}", month, year);
		dailyTransactions.clear();
		
		List<ProductFlow> transactionItems = productFlowRepository.findByTransactionPeriod(  month, year); 
		List<CapitalFlow> capitalFlows = capitalFlowRepository.findByPeriod(month, year);
		List<CostFlow> costFlows = costFlowRepository.findByPeriod(month, year); 
		
		final List<FinancialEntity> results = new ArrayList<FinancialEntity>() { 
			private static final long serialVersionUID = -9006495340734852418L;

			{
				addAll(costFlows); 
				addAll(transactionItems); 
				addAll(capitalFlows);
			}
		};  
		
		log.info("results count: {}", results.size());
		
		for (FinancialEntity baseEntity : results) {  
			//don't access the credit & debt value
			log.info("Access {}", baseEntity.getClass());
			Date transactionDate = CashBalanceService.mapCashBalance(baseEntity).getDate(); 
			int day = DateUtil.getCalendarItem(transactionDate, Calendar.DAY_OF_MONTH);  
			putTransaction(day, baseEntity);
		}
		
		return results ;
	}

	/**
	 * get cash balance at given period filter
	 * @param filter
	 * @return
	 */
	private CashBalance getInitialBalance(Filter filter) {
		int prevMonth = filter.getMonth();// - 1 < 1 ? 12 : filter.getMonth() - 1;
		int year = filter.getYear();// filter.getMonth() - 1 < 1 ? filter.getYear() - 1 : filter.getYear();
		CashBalance cashBalance = cashBalanceService.getBalanceBefore (prevMonth, year);
		dailyReportInitialBalance = cashBalance;
		return cashBalance;
	}

	/**
	 * get one year report having each month debit & credit summary
	 * @param request
	 * @return
	 */
	public File buildMonthlyReport(WebRequest request) {
		
		Filter filter = request.getFilter();
		Integer year = filter.getYear();
		clearMontlyReport();
		
		/**
		 * Get daily report for 12 months
		 */
		for(int i = 1; i <= 12; i++) {
			Filter monthFilter = Filter.builder().month(i).year(year).build();
			getTransactionRecords(monthFilter); 
			Map<ReportCategory, DailyReportRow> dailyReportSummaryCloned = (Map<ReportCategory, DailyReportRow>) SerializationUtils.clone((Serializable) dailyReportSummary);
			monthyReportContent.put(i,  dailyReportSummaryCloned);
			 
		}
		
		for(Integer key: monthyReportContent.keySet()) {
			System.out.println("==================="+key);
			Map<ReportCategory, DailyReportRow> daily = monthyReportContent.get(key);
			for(ReportCategory reportCategory : daily.keySet()) {
				System.out.println(reportCategory.toString()+". D: "+daily.get(reportCategory).getDebitAmount()+" | K: "+daily.get(reportCategory).getCreditAmount());
			}
		}
		File result = excelReportBuilder.getMonthyReport(ReportRequest.builder()
				.filter(filter).monthyReportContent(monthyReportContent).build());
		
		return result;
	}
	 
	/**
	 * ===========================
	 * 		Entity Report
	 * ===========================
	 */
	
	public File getEntityReport(List<BaseEntity> entities, Class<? extends BaseEntity> entityClass) {
		log.info("entities count: {}", entities.size());
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityClass, null);
		
		File result = entityReportBuilder.getEntityReport(entities, entityProperty);
		return result;
	}

	public File buildEntityReport(WebRequest request) { 
//		request.getFilter().setLimit(0);
		WebResponse response = entityService.filter(request);
		
		File file = getEntityReport(response.getEntities(), response.getEntityClass());
		return file ;
	}
	
}
