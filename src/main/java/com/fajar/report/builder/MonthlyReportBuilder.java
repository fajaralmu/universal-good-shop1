package com.fajar.report.builder;

import static com.fajar.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.util.ExcelReportUtil.autosizeColumn;
import static com.fajar.util.ExcelReportUtil.createRow;
import static com.fajar.util.ExcelReportUtil.curr;
import static com.fajar.util.ExcelReportUtil.setBorderTop;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ReportCategory;
import com.fajar.service.WebConfigService;
import com.fajar.service.report.data.DailyReportRow;
import com.fajar.service.report.data.ReportData;
import com.fajar.util.DateUtil;
import com.fajar.util.MyFileUtil;
import com.fajar.util.StringUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MonthlyReportBuilder { 
	
	private final WebConfigService webConfigService;
	private long grandTotalDebit = 0L;
	private long grandTotalCredit = 0L;
	private Map<ReportCategory, DailyReportRow> totalEachCategory = new HashMap<>();
	private XSSFSheet xsheet;
	private final ReportCategory[] reportCategories = ReportCategory.values();
	
	public MonthlyReportBuilder(WebConfigService webConfigService) {
		this.webConfigService = webConfigService;
	}

	/**
	 * ============================================ Montly Report
	 * ============================================
	 */
	/**
	 * 
	 * @param reportRequest
	 * @return
	 */
	public File getMonthyReport(ReportData reportRequest) {
		Filter filter = reportRequest.getFilter();
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Monthly-" + filter.getYear();

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName);

		writeMonthlyReport(reportRequest, reportName);

		File file = MyFileUtil.getFile(xwb, reportName);
		refresh();

		return file;
	}

	private void refresh() {

		grandTotalDebit = 0L;
		grandTotalCredit = 0L;
		totalEachCategory = new HashMap<>();
	}

	/**
	 * 
	 * @param xsheet
	 * @param reportRequest
	 * @param reportName
	 */
	private void writeMonthlyReport( ReportData reportRequest, String reportName) {

		Map<Integer, Map<ReportCategory, DailyReportRow>> reportContent = reportRequest.getMonthyReportContent();
		
		int offsetRow = 4;
		int offsetColumn = 1;

		int totalRowNum = offsetRow + reportCategories.length;

		preprareCells();
		createLeftColumnLabels(offsetRow);
		createLeftColumnHeader(totalRowNum);
		createMonthNameColumns();

		/**
		 * Values
		 */
		for (int i = 1; i <= 12; i++) {
			
			log.info("Writing month : {}", i);
			Map<ReportCategory, DailyReportRow> monthData = reportContent.get(i);
			writeOneMonthData(monthData, totalRowNum, offsetRow, i);
		}

		/**
		 * Grand Total
		 */
		int totalColumn = 2 + 2 * 12;
		writeTotalColumnLabel(totalColumn);
		writeTotalValues(totalColumn, offsetRow, totalRowNum);
		
		/**
		 * auto size
		 */
		autosizeCells(offsetRow);

		setBorderTop(xsheet.getRow(1), BorderStyle.DOUBLE);

	}

	private void autosizeCells(int offsetRow) {
		 
		int rows = offsetRow + reportCategories.length;
		for (int i = 0; i <= rows; i++) {
			XSSFRow xssfRow = xsheet.getRow(i);
			autosizeColumn(xssfRow, 4 + 12 * 2, BorderStyle.THIN, HorizontalAlignment.CENTER);
		}
	}

	private void writeTotalValues(int totalColumn, int offsetRow, int totalRowNum) {
		 
		for (int i = 0; i < reportCategories.length; i++) {
			ReportCategory reportCategory = reportCategories[i];
			DailyReportRow totalData = totalEachCategory.get(reportCategory);
			createRow(xsheet, offsetRow + i, totalColumn, curr(totalData.getDebitAmount()),
					curr(totalData.getCreditAmount()));
		}
		createRow(xsheet, totalRowNum, 26, curr(grandTotalDebit), curr(grandTotalCredit));

	}

	private void writeTotalColumnLabel(int totalColumn) { 
		addMergedRegion(xsheet, new CellRangeAddress(1, 2, totalColumn, 27));
		createRow(xsheet, 1, totalColumn, "Jumlah");
		createRow(xsheet, 3, totalColumn, "D (K)", "K (D)");
	}

	private void writeOneMonthData(Map<ReportCategory, DailyReportRow> monthData, int totalRowNum,
			int offsetRow, int monthIndex) {

		long totalDebit = 0L;
		long totalCredit = 0L; 

		for (int j = 0; j < reportCategories.length; j++) {
			ReportCategory reportCategory = reportCategories[j];
			DailyReportRow categoryData = getRowDataForOneCategory(monthData,reportCategory, monthIndex, offsetRow); 

			totalDebit += categoryData.getDebitAmount();
			totalCredit += categoryData.getCreditAmount();

			updateTotalEachCategory(reportCategory, totalEachCategory, categoryData);
		}

		grandTotalDebit += totalDebit;
		grandTotalCredit += totalCredit;

		createRow(xsheet, totalRowNum, monthIndex * 2, curr(totalCredit), curr(totalDebit));
	}
	
	private int getEnumIndex(ReportCategory reportCategory) {
		for(int i = 0; i <reportCategories.length ; i++) {
			if(reportCategories[i].equals(reportCategory)) {
				return i;
			}
		}
		return 0;
	}

	private DailyReportRow getRowDataForOneCategory(Map<ReportCategory, DailyReportRow> monthData,
			ReportCategory reportCategory, int monthIndex, int offsetRow) {
		DailyReportRow categoryData = monthData.get(reportCategory);
		
		int reportCategoryIndex = getEnumIndex(reportCategory);
		long totalDebitForCashBalance = totalExceptCashBalance(monthData, 0);
		long totalCreditForCashBalance = totalExceptCashBalance(monthData, 1); 

		if (reportCategory.equals(ReportCategory.CASH_BALANCE)) {
			categoryData = new DailyReportRow();
			categoryData.setDebitAmount(totalDebitForCashBalance);
			categoryData.setCreditAmount(totalCreditForCashBalance);
		}

		if (null == categoryData) {
			categoryData = new DailyReportRow();
		}
		createRow(xsheet, offsetRow + reportCategoryIndex, monthIndex * 2, curr(categoryData.getCreditAmount()),
				curr(categoryData.getDebitAmount()));
		return categoryData;
	}

	private void createMonthNameColumns() {
		/**
		 * Month Names
		 */
		int triwulan = 0;
		for (int i = 1; i <= 12; i++) {
			String monthName = DateUtil.MONTH_NAMES[i - 1];
			addMergedRegion(xsheet, new CellRangeAddress(2, 2, i * 2, i * 2 + 1));

			createRow(xsheet, 2, i * 2, monthName);
			createRow(xsheet, 3, i * 2, "D (K)", "K (D)");

			if (i % 3 == 0) {
				addMergedRegion(xsheet, new CellRangeAddress(1, 1, (i - 2) * 2, i * 2 + 1));
				XSSFRow row = createRow(xsheet, 1, (i - 2) * 2, "Triwulan " + StringUtil.GREEK_NUMBER[triwulan]);

				triwulan++;
			}
		}
	}

	private void createLeftColumnHeader(int totalRowNum) {
		log.info("create column headers");
		/**
		 * Title Label
		 */
		addMergedRegion(xsheet, new CellRangeAddress(1, 3, 0, 0), new CellRangeAddress(1, 3, 1, 1));
		createRow(xsheet, 1, 0, "Kode Akun", "Nama Akun");
		createRow(xsheet, totalRowNum, 0, "", "Jumlah");
	}

	private void createLeftColumnLabels(int offsetRow) {
		log.info("create column labels");
		/**
		 * Report Categories
		 */  
		for (int i = 0; i < reportCategories.length; i++) {
			ReportCategory reportCategory = reportCategories[i];
			createRow(xsheet, offsetRow + i, 0, reportCategory.code, reportCategory.name);
		}
	}

	private void preprareCells( ) { 
		log.info("prepare cells");
		for (int i = 0; i < 4 + 12 * 2; i++) {
			for (int j = 0; j < 3 + ReportCategory.values().length; j++) {
				createRow(xsheet, 1 + j, i, "");
			}
		}
	}

	public static void updateTotalEachCategory(ReportCategory reportCategory,
			Map<ReportCategory, DailyReportRow> totalEachCategory, DailyReportRow categoryData) {
		if (totalEachCategory.get(reportCategory) == null) {
			totalEachCategory.put(reportCategory, new DailyReportRow());
		}
		DailyReportRow existingRowData = totalEachCategory.get(reportCategory);
		existingRowData.setCreditAmount(existingRowData.getCreditAmount() + categoryData.getCreditAmount());
		existingRowData.setDebitAmount(existingRowData.getDebitAmount() + categoryData.getDebitAmount());
		totalEachCategory.put(reportCategory, existingRowData);

	}

	/**
	 * 
	 * @param summary
	 * @param mode    0 for debit, 1 for credit
	 * @return
	 */
	public static long totalExceptCashBalance(Map<ReportCategory, DailyReportRow> summary, int mode) {
		long total = 0L;
		Set<ReportCategory> keys = summary.keySet();

		for (ReportCategory reportCategory : keys) {
			if (reportCategory.equals(ReportCategory.CASH_BALANCE))
				continue;

			DailyReportRow reportData = summary.get(reportCategory);

			if (mode == 1) {
				total += reportData.getDebitAmount();
			} else if (mode == 0) {
				total += reportData.getCreditAmount();
			}
		}

		return total;
	}

}
