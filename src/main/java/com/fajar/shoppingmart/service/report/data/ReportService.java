package com.fajar.shoppingmart.service.report.data;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.EntityService;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.builder.BalanceReportBuilder;
import com.fajar.shoppingmart.service.report.builder.DailyReportBuilder;
import com.fajar.shoppingmart.service.report.builder.EntityReportBuilder;
import com.fajar.shoppingmart.service.report.builder.EntityReportService;
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
	@Autowired
	private EntityReportService entityReportService;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private EntityService entityService;

	public XSSFWorkbook buildDailyReport(WebRequest request) {
		log.info("buildDailyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getDailyReportData(request);
		DailyReportBuilder reportBuilder = new DailyReportBuilder(reportData);
		log.info("report row data size: {}", reportData.getDailyReportRows().size());
		
		return reportBuilder.buildReport();
	}

	public XSSFWorkbook buildMonthlyReport(WebRequest request) {
		log.info("buildMonthlyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getMonthlyReportData(request);
		MonthlyReportBuilder reportBuilder = new MonthlyReportBuilder(reportData, true);
		return reportBuilder.buildReport();
	}
	
	public XSSFWorkbook generateEntityReport(WebRequest request, HttpServletRequest httpRequest) throws Exception {
		log.info("generateEntityReport");
//		request.getFilter().setLimit(0); 

		WebResponse response = entityService.filter(request);

		progressService.sendProgress(1, 1, 20, true, httpRequest);

		XSSFWorkbook file = entityReportService.getEntityReport(response.getEntities(), response.getEntityClass(), httpRequest);

		return file;
	}

	public XSSFWorkbook buildEntityReport(WebRequest request) throws Exception {
		log.info("buildEntityReport, request: {}", request);
		
		ReportData reportData =  reportDataService.getEntityReportData(request);
		EntityReportBuilder reportBuilder = new EntityReportBuilder(reportData);
		return reportBuilder.buildReport();
	}
	
	public XSSFWorkbook buildBalanceReport(WebRequest request) {
		log.info("buildBalanceReport, request: {}", request);
		
		ReportData reportData = balanceReportDataService.getBalanceReportData(request);
		BalanceReportBuilder reportBuilder = new BalanceReportBuilder( reportData);
		return reportBuilder.buildReport();
	}

}
