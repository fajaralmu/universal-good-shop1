package com.fajar.shoppingmart.service.report.builder;

import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.util.DateUtil;
public abstract class ReportBuilder { 
	protected XSSFSheet xsheet;
	protected XSSFWorkbook xssfWorkbook;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a";
	protected final ReportData reportData;
	protected String reportName;

	// optional
	protected ProgressService progressService;

	public ReportBuilder(  ReportData reportData) {
		 
		this.reportData = reportData;
		init();
	}

	public void setProgressService(ProgressService progressService) {

		this.progressService = progressService;
	}

	protected abstract void init();

	protected String getDateTime() {
		return DateUtil.formatDate(new Date(), DATE_PATTERN);
	}

	protected void sendProgress(double taskProportion, double taskSize, double totalTaskProportion) {
		if (null == progressService)
			return;
		progressService.sendProgress(taskProportion, taskSize, totalTaskProportion, false, reportData.getRequestId());
	}

	public abstract XSSFWorkbook buildReport();
}
