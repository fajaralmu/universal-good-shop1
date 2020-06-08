package com.fajar.shoppingmart.service.report.data;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.builder.BalanceReportBuilder;
import com.fajar.shoppingmart.service.report.builder.DailyReportBuilder;
import com.fajar.shoppingmart.service.report.builder.EntityReportBuilder;
import com.fajar.shoppingmart.service.report.builder.MonthlyReportBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportService {
	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ReportDataService reportDataService; 
	@Autowired
	private BalanceReportDataService balanceReportDataService;

	public File buildDailyReport(WebRequest request) {
		log.info("buildDailyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getDailyReportData(request);
		DailyReportBuilder reportBuilder = new DailyReportBuilder(webConfigService, reportData);
		log.info("report row data size: {}", reportData.getDailyReportRows().size());
		
		return reportBuilder.buildReport();
	}

	public File buildMonthlyReport(WebRequest request) {
		log.info("buildMonthlyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getMonthlyReportData(request);
		MonthlyReportBuilder reportBuilder = new MonthlyReportBuilder(webConfigService, reportData, true);
		return reportBuilder.buildReport();
	}

	public File buildEntityReport(WebRequest request) {
		log.info("buildEntityReport, request: {}", request);
		
		ReportData reportData = reportDataService.getEntityReportData(request);
		EntityReportBuilder reportBuilder = new EntityReportBuilder(webConfigService, reportData);
		return reportBuilder.buildReport();
	}
	
	public File buildBalanceReport(WebRequest request) {
		log.info("buildBalanceReport, request: {}", request);
		
		ReportData reportData = balanceReportDataService.getBalanceReportData(request);
		BalanceReportBuilder reportBuilder = new BalanceReportBuilder(webConfigService, reportData);
		return reportBuilder.buildReport();
	}

}
