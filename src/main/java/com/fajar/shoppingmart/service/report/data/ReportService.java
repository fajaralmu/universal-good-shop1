package com.fajar.shoppingmart.service.report.data;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.builder.DailyReportBuilder;
import com.fajar.shoppingmart.service.report.builder.EntityReportBuilder;
import com.fajar.shoppingmart.service.report.builder.MonthlyReportBuilder;

@Service
public class ReportService {
	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ReportDataService reportDataService; 

	public File buildDailyReport(WebRequest request) {
		 
		ReportData reportData = reportDataService.getDailyReportData(request);
		DailyReportBuilder reportBuilder = new DailyReportBuilder(webConfigService);
		return reportBuilder.buildReport(reportData);
	}

	public File buildMonthlyReport(WebRequest request) {

		ReportData reportData = reportDataService.getMonthlyReportData(request);
		MonthlyReportBuilder reportBuilder = new MonthlyReportBuilder(webConfigService);
		return reportBuilder.buildReport(reportData);
	}

	public File buildEntityReport(WebRequest request) {
		ReportData reportData = reportDataService.getEntityReportData(request);
		EntityReportBuilder reportBuilder = new EntityReportBuilder(webConfigService);
		return reportBuilder.buildReport(reportData);
	}
	

}
