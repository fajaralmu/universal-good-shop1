package com.fajar.shoppingmart.service.report.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.service.TransactionService;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.builder.MonthlyReportBuilder;
import com.fajar.shoppingmart.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BalanceReportDataService {
	
	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ReportDataService reportDataService;
	
	public ReportData getBalanceReportData(WebRequest webRequest) {
		
		int selectedYear = webRequest.getFilter().getYear();
		int formerYear = selectedYear - 1;
		int firstYear = transactionService.getMinTransactionYear();
		int nowYear = DateUtil.getCalendarYear(new Date());
		if(selectedYear < nowYear) {
			nowYear = selectedYear;
		}
		
		log.info("From year: {} to: {}", firstYear, nowYear);
		
		Map<Integer, Map<ReportCategory, ReportRowData>> summaryDatas = new HashMap<>();
		
		for(int year = firstYear; year <= nowYear; year++) { 
			log.info("collecting data for year: {}", year);
			  
			Map<ReportCategory, ReportRowData> totalEachCategory = getMonthlyTotalEachCategory(year);
			swapCreditAndDebit(totalEachCategory);
			summaryDatas.put(year, totalEachCategory);
			
			log.info("totalEachCategory keys - {}: {}", year, totalEachCategory.keySet().size()); 
		}
		
		log.info("End collecting data");
		
		Map<ReportCategory, ReportRowData> formerData = summaryDatas.get(formerYear);
		log.info("formerData keys: {}", formerData == null ? null : formerData.keySet().size());
		
		ReportData reportData = new ReportData();
		reportData.setFilter(webRequest.getFilter());
		reportData.setMonthyReportContent(summaryDatas); 
		reportData.setDailyReportSummary(formerData);
		return reportData;
	}

	private static void swapCreditAndDebit(Map<ReportCategory, ReportRowData> rowDatas) {
		for (ReportCategory reportCategory : rowDatas.keySet()) {
			ReportRowData rowData = rowDatas.get(reportCategory);
			final long debitAmount = rowData.getDebitAmount();
			final long creditAmount = rowData.getCreditAmount();
			
			rowDatas.get(reportCategory).setDebitAmount(creditAmount);
			rowDatas.get(reportCategory).setCreditAmount(debitAmount);
			
		}
		
	}

	private Map<ReportCategory, ReportRowData> getMonthlyTotalEachCategory(int year) { 
		MonthlyReportBuilder builder = getMonthlyReportBuilder(year);
		Map<ReportCategory, ReportRowData> totalEachCategory = builder.getTotalEachCategory();
		return totalEachCategory;
	}

	private MonthlyReportBuilder getMonthlyReportBuilder(int year) {
		ReportData monthlyData = getMonthlyData(year); 
		MonthlyReportBuilder builder = new MonthlyReportBuilder(monthlyData, false);
		builder.buildReport();
		return builder;
	}

	private ReportData getMonthlyData(int year) {
		WebRequest request = WebRequest.builder().filter(Filter.builder().year(year).build()).build();
		ReportData monthlyData = reportDataService.getMonthlyReportData(request );
		return monthlyData;
	}

}
