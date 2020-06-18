package com.fajar.shoppingmart.service.report.builder;

import java.io.File;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.util.DateUtil;

public abstract class ReportBuilder {
	protected final WebConfigService webConfigService;
	protected XSSFSheet xsheet;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a"; 
	protected final ReportData reportData;
	
	public ReportBuilder(WebConfigService configService, ReportData reportData) {
		this.webConfigService = configService;
		this.reportData = reportData;
	}

	protected String getDateTime() {
		return DateUtil.formatDate(new Date(), DATE_PATTERN);
	}
	
	public abstract File buildReport( );
}