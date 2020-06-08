package com.fajar.shoppingmart.service.report.builder;

import java.io.File;

import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.data.ReportData;

public class BalanceReportBuilder extends ReportBuilder{

	public BalanceReportBuilder(WebConfigService configService) {
		super(configService);
		 
	}

	@Override
	public File buildReport(ReportData reportData) {
		 
		return null;
	}

}
