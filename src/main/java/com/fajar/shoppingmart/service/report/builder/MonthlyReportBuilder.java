package com.fajar.shoppingmart.service.report.builder;

import static com.fajar.shoppingmart.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.shoppingmart.util.ExcelReportUtil.autosizeColumn;
import static com.fajar.shoppingmart.util.ExcelReportUtil.createRow;
import static com.fajar.shoppingmart.util.ExcelReportUtil.curr;
import static com.fajar.shoppingmart.util.ExcelReportUtil.setBorderTop;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.service.report.data.ReportRowData;
import com.fajar.shoppingmart.util.DateUtil;
import com.fajar.shoppingmart.util.StringUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j 
public class MonthlyReportBuilder extends ReportBuilder{   
	 
	private long grandTotalDebit = 0L;
	private long grandTotalCredit = 0L;
	private Map<ReportCategory, ReportRowData> totalEachCategory = new HashMap<>(); 
	private final boolean writeExcel;
	
	private final ReportCategory[] reportCategories = ReportCategory.values();
	
	public MonthlyReportBuilder( ReportData reportData, boolean  writeExcel) {
		super(  reportData);
		this.writeExcel = writeExcel;
	}
	public Map<ReportCategory, ReportRowData> getTotalEachCategory(){
		return totalEachCategory;
	}

	/**
	 * ============================================ Montly Report
	 * ============================================
	 */
	/**
	 * 
	 * @param reportData
	 * @return
	 */
	@Override
	public XSSFWorkbook buildReport() {
		Filter filter = reportData.getFilter();
		String time = getDateTime();
		String sheetName = "Monthly-" + filter.getYear();

		String reportName = /* webConfigService.getReportPath() + "/" + */ sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName);

		writeMonthlyReport(reportData, reportName);

//		File file = MyFileUtil.getFile(xwb, reportName);
		refresh();

		return xwb;
	}

	private void refresh() {

		grandTotalDebit = 0L;
		grandTotalCredit = 0L;
		if(writeExcel) {
			totalEachCategory = new HashMap<>();
		}
	}

	/**
	 * 
	 * @param xsheet
	 * @param reportData
	 * @param reportName
	 */
	private void writeMonthlyReport( ReportData reportData, String reportName) {

		Map<Integer, Map<ReportCategory, ReportRowData>> reportContent = reportData.getMonthyReportContent();
		
		int offsetRow = 4;
//		int offsetColumn = 1;

		int totalRowNum = offsetRow + reportCategories.length;

		preprareCells();
		createLeftColumnLabels(offsetRow);
		createLeftColumnHeader(totalRowNum);
		createHorizontalMonthNameColumns();

		/**
		 * Values
		 */
		for (int i = 1; i <= 12; i++) {
			
			log.info("Writing month : {}", i);
			Map<ReportCategory, ReportRowData> monthData = reportContent.get(i);
			writeOneMonthData(monthData, totalRowNum, offsetRow, i);
		}

		/**
		 * Grand Total
		 */
		int totalColumn = 2 + 2 * 12;
		writeTotalColumnLabel(totalColumn);
		writeTotalValues(totalColumn, offsetRow, totalRowNum);
		
		/**
		 * auto size and add border
		 */ 
		if(writeExcel) {
			autosizeCells(offsetRow);
			setBorderTop(xsheet.getRow(1), BorderStyle.DOUBLE);
		}
	}

	private void autosizeCells(int offsetRow) {
		 if(!writeExcel) {
			 return;
		 }
		 int rows = offsetRow + reportCategories.length;
		 for (int i = 0; i <= rows; i++) {
			XSSFRow xssfRow = xsheet.getRow(i);
			autosizeColumn(xssfRow, 4 + 12 * 2, BorderStyle.THIN, HorizontalAlignment.CENTER);
		 }	
	}

	private void writeTotalValues(int totalColumn, int offsetRow, int totalRowNum) {
		 
		for (int i = 0; i < reportCategories.length; i++) {
			ReportCategory reportCategory = reportCategories[i];
			ReportRowData totalData = totalEachCategory.get(reportCategory);
			writeRow(xsheet, offsetRow + i, totalColumn, curr(totalData.getDebitAmount()),
					curr(totalData.getCreditAmount()));
		}
		//grand total
		writeRow(xsheet, totalRowNum, 26, curr(grandTotalDebit), curr(grandTotalCredit));

	}

	private void writeTotalColumnLabel(int totalColumn) { 
		mergeRegion(xsheet, new CellRangeAddress(1, 2, totalColumn, 27));
		writeRow(xsheet, 1, totalColumn, "Jumlah");
		writeRow(xsheet, 3, totalColumn, "D (K)", "K (D)");
	}

	private void writeOneMonthData(Map<ReportCategory, ReportRowData> monthData, int totalRowNum,
			int offsetRow, int monthIndex) {

		long totalDebit = 0L;
		long totalCredit = 0L; 

		for (int j = 0; j < reportCategories.length; j++) {
			ReportCategory reportCategory = reportCategories[j];
			ReportRowData categoryData = getRowDataForOneCategory(monthData,reportCategory, monthIndex, offsetRow); 

			totalDebit += categoryData.getDebitAmount();
			totalCredit += categoryData.getCreditAmount();

			updateTotalEachCategory(reportCategory, /* totalEachCategory, */ categoryData);
		}

		grandTotalDebit += totalDebit;
		grandTotalCredit += totalCredit;

		writeRow(xsheet, totalRowNum, monthIndex * 2, curr(totalCredit), curr(totalDebit));
	}
	
	private XSSFRow writeRow(XSSFSheet xsheet, int rowNum, int offsetIndex, Object ...values){
		if(!writeExcel) {
			 return null;
		 }
		return createRow(xsheet, rowNum, offsetIndex, values);
	}
	
	private void mergeRegion(XSSFSheet xsheet, CellRangeAddress ...cellRangeAddresses ) {
		addMergedRegion(xsheet, cellRangeAddresses);
	}
	
	private int getEnumIndex(ReportCategory reportCategory) {
		for(int i = 0; i <reportCategories.length ; i++) {
			if(reportCategories[i].equals(reportCategory)) {
				return i;
			}
		}
		return 0;
	}

	private ReportRowData getRowDataForOneCategory(Map<ReportCategory, ReportRowData> monthData,
			ReportCategory reportCategory, int monthIndex, int offsetRow) {
		ReportRowData categoryData = monthData.get(reportCategory);
		
		int reportCategoryIndex = getEnumIndex(reportCategory);
		long totalDebitForCashBalance = totalExceptCashBalance(monthData, 0);
		long totalCreditForCashBalance = totalExceptCashBalance(monthData, 1); 

		if (reportCategory.equals(ReportCategory.CASH_BALANCE)) {
			categoryData = new ReportRowData();
			categoryData.setDebitAmount(totalDebitForCashBalance);
			categoryData.setCreditAmount(totalCreditForCashBalance);
		}

		if (null == categoryData) {
			categoryData = new ReportRowData();
		}
		writeRow(xsheet, offsetRow + reportCategoryIndex, monthIndex * 2, curr(categoryData.getCreditAmount()),
				curr(categoryData.getDebitAmount()));
		return categoryData;
	}

	private void createHorizontalMonthNameColumns() {
		/**
		 * Month Names
		 */
		int quarterlyLabel = 0;
		for (int i = 1; i <= 12; i++) {
			String monthName = DateUtil.MONTH_NAMES[i - 1];
			mergeRegion(xsheet, new CellRangeAddress(2, 2, i * 2, i * 2 + 1));

			writeRow(xsheet, 2, i * 2, monthName);
			writeRow(xsheet, 3, i * 2, "D (K)", "K (D)");

			if (i % 3 == 0) {
				mergeRegion(xsheet, new CellRangeAddress(1, 1, (i - 2) * 2, i * 2 + 1));
				writeRow(xsheet, 1, (i - 2) * 2, "Triwulan " + StringUtil.GREEK_NUMBER[quarterlyLabel]);

				quarterlyLabel++;
			}
		}
	}

	private void createLeftColumnHeader(int totalRowNum) {
		log.info("create column headers");
		/**
		 * Title Label
		 */
		mergeRegion(xsheet, new CellRangeAddress(1, 3, 0, 0), new CellRangeAddress(1, 3, 1, 1));
		writeRow(xsheet, 1, 0, "Kode Akun", "Nama Akun");
		writeRow(xsheet, totalRowNum, 0, "", "Jumlah");
	}

	private void createLeftColumnLabels(int offsetRow) {
		log.info("create column labels");
		/**
		 * Report Categories
		 */  
		for (int i = 0; i < reportCategories.length; i++) {
			ReportCategory reportCategory = reportCategories[i];
			writeRow(xsheet, offsetRow + i, 0, reportCategory.code, reportCategory.name);
		}
	}

	private void preprareCells( ) { 
		log.info("prepare cells");
		for (int i = 0; i < 4 + 12 * 2; i++) {
			for (int j = 0; j < 3 + ReportCategory.values().length; j++) {
				writeRow(xsheet, 1 + j, i, "");
			}
		}
	}

	public void updateTotalEachCategory(ReportCategory reportCategory,
			/* Map<ReportCategory, DailyReportRow> totalEachCategory, */ ReportRowData categoryData) {
		if (totalEachCategory.get(reportCategory) == null) {
			totalEachCategory.put(reportCategory, new ReportRowData());
		}
		ReportRowData existingRowData = totalEachCategory.get(reportCategory);
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
	public static long totalExceptCashBalance(Map<ReportCategory, ReportRowData> summary, int mode) {
		long total = 0L;
		Set<ReportCategory> keys = summary.keySet();

		for (ReportCategory reportCategory : keys) {
			if (reportCategory.equals(ReportCategory.CASH_BALANCE))
				continue;

			ReportRowData reportData = summary.get(reportCategory);

			if (mode == 1) {
				total += reportData.getDebitAmount();
			} else if (mode == 0) {
				total += reportData.getCreditAmount();
			}
		}

		return total;
	}
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

}
