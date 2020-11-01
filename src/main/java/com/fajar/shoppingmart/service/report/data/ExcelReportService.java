package com.fajar.shoppingmart.service.report.data;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.entity.EntityService;
import com.fajar.shoppingmart.service.report.builder.BalanceReportBuilder;
import com.fajar.shoppingmart.service.report.builder.CustomWorkbook;
import com.fajar.shoppingmart.service.report.builder.DailyReportBuilder;
import com.fajar.shoppingmart.service.report.builder.EntityReportService;
import com.fajar.shoppingmart.service.report.builder.MonthlyReportBuilder;
import com.fajar.shoppingmart.service.report.builder.OnProgress;
import com.fajar.shoppingmart.service.report.builder.ReportBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelReportService { 
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
	
	private void initProgress(HttpServletRequest httpRequest) {

		progressService.sendProgress(1, 1, 20, true, httpRequest);
	}

	public CustomWorkbook buildDailyReport(WebRequest request, HttpServletRequest httpRequest) {
		log.info("buildDailyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getDailyReportData(request, httpRequest);
		initProgress(httpRequest);

		log.info("report row data size: {}", reportData.getDailyReportRows().size());
		
		return writeTransactionReport(new DailyReportBuilder(reportData), httpRequest);
	}

	public CustomWorkbook buildMonthlyReport(WebRequest request, HttpServletRequest httpRequest) {
		log.info("buildMonthlyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getMonthlyReportData(request);
		initProgress(httpRequest);
		
		return writeTransactionReport(new MonthlyReportBuilder(reportData, true), httpRequest);
	}
	
	public CustomWorkbook buildBalanceReport(WebRequest request, HttpServletRequest httpRequest) {
		log.info("buildBalanceReport, request: {}", request);
		
		ReportData reportData = balanceReportDataService.getBalanceReportData(request);
		initProgress(httpRequest);
		 
		return writeTransactionReport(new BalanceReportBuilder(reportData), httpRequest);
	}
	
	private CustomWorkbook writeTransactionReport(ReportBuilder reportBuilder, HttpServletRequest httpRequest) {
		
		reportBuilder.setOnProgressCallback(onProgressCallback(httpRequest));
		CustomWorkbook workbook = reportBuilder.buildReport();
		return workbook;
	}
	
	private OnProgress onProgressCallback(HttpServletRequest httpRequest) {
		 
		return new OnProgress() {
			
			@Override
			public void onProgress(int taskProportion, int totalProportion, int generalProportion, String message) {
				 progressService.sendProgress(taskProportion, totalProportion, generalProportion, httpRequest);
				
			}
		};
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	public CustomWorkbook generateEntityReport(WebRequest request, HttpServletRequest httpRequest) throws Exception {
		log.info("generateEntityReport, request: {}", request); 

		WebResponse response = entityService.filter(request, null); 
		initProgress(httpRequest);

		CustomWorkbook file = entityReportService.getEntityReport(response.getEntities(), response.getEntityClass(), httpRequest);
		return file;
	}
 
	

}
