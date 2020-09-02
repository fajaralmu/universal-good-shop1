package com.fajar.shoppingmart.service.report.builder;

import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.util.DateUtil;
public abstract class ReportBuilder { 
	protected XSSFSheet xsheet;
	protected XSSFWorkbook xssfWorkbook;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a";
	protected final ReportData reportData;
	protected String reportName;
	private OnProgress onProgressCallback;
	// optional
	protected ProgressService progressService;
	
	int totalProgress = 0;
	static final int MAX_PROGRESS = 80;

	public ReportBuilder(  ReportData reportData) {
		 
		this.reportData = reportData;
		init();
	}

	public void setProgressService(ProgressService progressService) {

		this.progressService = progressService;
	}

	protected abstract void init();
	
	protected void onProgress(int taskPropportion, int totalProportion, int generalProportion, String message) {
		double proportion = generalProportion/MAX_PROGRESS;
//		totalProgress+=proportion;
		if(null != onProgressCallback) {
			onProgressCallback.onProgress(taskPropportion, totalProportion, generalProportion, message);
		}
	}
	protected void onProgress( int generalProportion, String message) {
		onProgress(1,1, generalProportion, message);
	}

	protected String getDateTime() {
		return DateUtil.formatDate(new Date(), DATE_PATTERN);
	}

	protected void sendProgress(double taskProportion, double taskSize, double totalTaskProportion) {
		if (null == progressService)
			return;
		progressService.sendProgress(taskProportion, taskSize, totalTaskProportion, false, reportData.getRequestId());
	}

	public abstract XSSFWorkbook buildReport();
	
	public void setOnProgressCallback(OnProgress callback)
	{
		this.onProgressCallback = callback;
	}
}
