package com.fajar.shoppingmart.service.report.builder;

import static com.fajar.shoppingmart.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.shoppingmart.util.ExcelReportUtil.createRow;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.service.report.data.ReportRowData;
import com.fajar.shoppingmart.util.MyFileUtil;

public class BalanceReportBuilder extends ReportBuilder {

	private Map<ReportCategory, ReportRowData> formerBalance;
	private Map<ReportCategory, ReportRowData> thisYearCashflow;

	public BalanceReportBuilder(WebConfigService configService) {
		super(configService);

	}
	
	private void setFormerBalance(Map<ReportCategory, ReportRowData> formerBalance) {
		if(null == formerBalance) {
			formerBalance = emptyCategories();
		}
		this.formerBalance = formerBalance;
	}
	
	private static Map<ReportCategory, ReportRowData> emptyCategories(){ 
		
		Map<ReportCategory, ReportRowData> map = new HashMap<ReportCategory, ReportRowData>();
		for(ReportCategory reportCategory : ReportCategory.values()) {
			ReportRowData rowData = new ReportRowData();
			rowData.setCategory(reportCategory);
			
			map.put(reportCategory, rowData );
		}
		return map ;
	}
	
	private void setThisYearCashflow(Map<ReportCategory, ReportRowData> thisYearCashflow) {
		this.thisYearCashflow = thisYearCashflow;
	}

	@Override
	public File buildReport(ReportData reportData) {

		Filter filter = reportData.getFilter();
		String time = getDateTime();
		String sheetName = "BALANCE-" + filter.getYear();

		setFormerBalance(reportData.getDailyReportSummary());
		setThisYearCashflow(reportData.getMonthyReportContent().get(filter.getYear()));

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName);

		writeBalanceReport(reportData);

		File file = MyFileUtil.getFile(xwb, reportName);
		return file;
	}

	private void writeBalanceReport(ReportData reportData) {

		int rowNum = 1;
		int colOffset = 1;
		writeHorizontalColumNames(rowNum, colOffset);
		rowNum += 2;
		writeReportCategoriesLabel(rowNum, colOffset);
		writeFormerBalance(rowNum, colOffset);

	}

	private void writeFormerBalance(int rowNum, int colOffset) {
		Set<ReportCategory> keys = formerBalance.keySet();
		long totalCredit = 0l;
		long totalDebit = 0l;
		for (ReportCategory reportCategory : keys) {
			
			ReportRowData rowData = formerBalance.get(reportCategory);
			
			long creditAmount = rowData.getCreditAmount();
			long debitAmount = rowData.getDebitAmount();
			
			totalCredit+=creditAmount;
			totalDebit+=debitAmount;
			
			createRow(xsheet, rowNum, colOffset + 2, debitAmount, creditAmount);
			rowNum++;
		}
		
		createRow(xsheet, rowNum, colOffset + 1, totalDebit, totalCredit);
	}

	private void writeReportCategoriesLabel(int rowNum, int colOffset) {
		ReportCategory[] reportCategories = ReportCategory.values();
		for (int i = 0; i < reportCategories.length; i++) {
			createRow(xsheet, rowNum, colOffset, (i + 1), reportCategories[i].name);
			rowNum++;
		}
		createRow(xsheet, rowNum, colOffset, (reportCategories.length + 1), "Jumlah");
	}

	private void writeHorizontalColumNames(final int rowNum, int colOffset) {

		/**
		 * Column title
		 */
		Object[] colNames = { "No", "Nama Akun", "Neraca Awal", "", "Neraca Perubahan", "", "Neraca Sisa", "",
				"Penyesuaian", "", "NS Disesuaikan", "", "Rugi / Laba", "", "Neraca", ""

		};
		createRow(xsheet, rowNum, colOffset, colNames);
		int titleCount = 7;
		for (int i = 1; i <= titleCount; i++) {
			int firstCol = colOffset + (i * 2);
			CellRangeAddress cellRange = new CellRangeAddress(rowNum, rowNum, firstCol, firstCol + 1);
			addMergedRegion(xsheet, cellRange);
		}
		
		/**
		 * Column info
		 */
		int infoRowNum = rowNum + 1;

		Object[] colInfos = new Object[16]; 

		for (int i = 2; i < colInfos.length; i++) {
			colInfos[i] = i % 2 == 0 ? "D" : "K";
		}
		createRow(xsheet, infoRowNum, colOffset, colInfos);

		

	}

}
