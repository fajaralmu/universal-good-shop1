package com.fajar.shoppingmart.service.report.builder;

import static com.fajar.shoppingmart.util.ExcelReportUtil.createRow;

import java.io.File;
import java.util.Map;

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

	@Override
	public File buildReport(ReportData reportData) {

		Filter filter = reportData.getFilter();
		String time = getDateTime();
		String sheetName = "BALANCE-" + filter.getYear();

		this.formerBalance = reportData.getDailyReportSummary();
		this.thisYearCashflow = reportData.getMonthyReportContent().get(filter.getYear());

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

		

	}

	private void writeReportCategoriesLabel(int rowNum, int colOffset) {
		ReportCategory[] reportCategories = ReportCategory.values();
		for (int i = 0; i < reportCategories.length; i++) {
			createRow(xsheet, rowNum, colOffset, (i+1), reportCategories[i].name);
			rowNum++;
		}
	}

	private void writeHorizontalColumNames(int rowNum, int colOffset) {

		Object[] colNames = { "No", "Nama Akun", "Neraca Awal", "Neraca Perubahan", "Neraca Sisa", "Penyesuaian",
				"NS Disesuaikan", "Rugi / Laba", "Neraca"

		}; 
		createRow(xsheet, rowNum, colOffset, colNames);
		rowNum++;
		
		Object[] colInfos = new Object[9];
		colInfos[0] = "";
		colInfos[1] = "";
		
		for(int i = 2; i < 9; i++) {
			colInfos[i] = i % 2 == 0?"D":"K";
		}
		createRow(xsheet, rowNum, colOffset, colInfos);

	}

}
