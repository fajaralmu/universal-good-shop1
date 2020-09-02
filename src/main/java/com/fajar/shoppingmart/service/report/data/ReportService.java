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
import com.fajar.shoppingmart.service.report.builder.OnProgress;

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
	
	private void initProgress(HttpServletRequest httpRequest) {

		progressService.sendProgress(1, 1, 20, true, httpRequest);
	}

	public XSSFWorkbook buildDailyReport(WebRequest request, HttpServletRequest httpRequest) {
		log.info("buildDailyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getDailyReportData(request);
		initProgress(httpRequest);

		log.info("report row data size: {}", reportData.getDailyReportRows().size());
		
		DailyReportBuilder reportBuilder = new DailyReportBuilder(reportData);
		reportBuilder.setOnProgressCallback(onProgressCallback(httpRequest)); 
		XSSFWorkbook file = reportBuilder.buildReport();
		return file;
	}

	public XSSFWorkbook buildMonthlyReport(WebRequest request, HttpServletRequest httpRequest) {
		log.info("buildMonthlyReport, request: {}", request);
		
		ReportData reportData = reportDataService.getMonthlyReportData(request);
		initProgress(httpRequest);
		
		MonthlyReportBuilder reportBuilder = new MonthlyReportBuilder(reportData, true);
		reportBuilder.setOnProgressCallback(onProgressCallback(httpRequest));
		XSSFWorkbook file = reportBuilder.buildReport();
		return file;
	}
	
	private OnProgress onProgressCallback(HttpServletRequest httpRequest) {
		 
		return new OnProgress() {
			
			@Override
			public void onProgress(int taxProportion, int totalProportion, int generalProportion, String message) {
				 progressService.sendProgress(taxProportion, totalProportion, generalProportion, httpRequest);
				
			}
		};
	}

	public XSSFWorkbook generateEntityReport(WebRequest request, HttpServletRequest httpRequest) throws Exception {
		log.info("generateEntityReport, request: {}", request); 

		WebResponse response = entityService.filter(request); 
		initProgress(httpRequest);

		XSSFWorkbook file = entityReportService.getEntityReport(response.getEntities(), response.getEntityClass(), httpRequest);
		return file;
	}
 
	public XSSFWorkbook buildBalanceReport(WebRequest request, HttpServletRequest httpRequest) {
		log.info("buildBalanceReport, request: {}", request);
		
		ReportData reportData = balanceReportDataService.getBalanceReportData(request);
		initProgress(httpRequest);
		
		BalanceReportBuilder reportBuilder = new BalanceReportBuilder( reportData);
		reportBuilder.setOnProgressCallback(onProgressCallback(httpRequest));
		XSSFWorkbook file = reportBuilder.buildReport();
		return file;
	}

}
