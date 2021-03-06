package com.fajar.shoppingmart.service.report.builder;

import static com.fajar.shoppingmart.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.shoppingmart.util.ExcelReportUtil.autosizeColumn;
import static com.fajar.shoppingmart.util.ExcelReportUtil.createRow;
import static com.fajar.shoppingmart.util.ExcelReportUtil.curr;
import static com.fajar.shoppingmart.util.ExcelReportUtil.removeBorder;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.service.report.data.ReportRowData;
import com.fajar.shoppingmart.util.DateUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DailyReportBuilder extends ReportBuilder{ 
	
	static final Object[] oneMonthReportHeader = new Object[] {
			 "No", "Tgl", "Uraian", "Kode", "Debet", "Kredit","Saldo" ,
			 "No", "Perkiraan", "Kode", "Debet", "Kredit"
	}; 
	
	 public DailyReportBuilder( ReportData reportData) {
		super(  reportData);
	 } 
	
	/**
	 * get daily report file
	 * @param reportData
	 * @return
	 */
	 @Override
	public CustomWorkbook buildReport () {

		Filter filter = reportData.getFilter();
		String time = getDateTime();
		String sheetName = "Daily-"+filter.getMonth()+"-"+filter.getYear();
		
		String reportName = /* webConfigService.getReportPath() + "/" + */ sheetName + "_"+ time+ ".xlsx";
		CustomWorkbook xwb  = new CustomWorkbook();
		xsheet = xwb.createSheet(sheetName ); 
		xwb.setFileName(reportName);
		writeDailyReportOneMonth( reportData);
		
//		File file = MyFileUtil.getFile(xwb, reportName);
		return xwb;
	}
	
	/**
	 * write daily report for one month
	 * @param xsheet
	 * @param reportData
	 */
	private void writeDailyReportOneMonth( ReportData reportData) {
		
		Filter filter = reportData.getFilter();
		CashBalance initialBalance = reportData.getInitialBalance();
		List<ReportRowData> dailyReportRows = reportData.getDailyReportRows();
		Map<ReportCategory, ReportRowData> dailyReportSummary = reportData.getDailyReportSummary(); 
		ReportRowData totalDailyReportRow = reportData.getTotalDailyReportRow();
		
		int row = 0;
		final int columnOffset = 0;
		//final int firstDate = 1;
		log.info("Prepare column labels");
		/**
		 * Report title
		 */
		
		createOneMonthReportTitle(row, columnOffset, filter); 
		onProgress(10, null);
		
		row++;
		row++;
		 
		final XSSFRow headerRow = createOneMonthReportHeader(row, columnOffset ); 
		
		row++;
		log.info("Writing content");
		log.info("dailyReportRows.size(): {}", dailyReportRows.size());
		/**
		 * =========================================
		 * 				Daily Content
		 * =========================================
		 
		 * First Row (Previous Balance)
		 */ 
		int dailyRow = row;
		writeOneMonthReportBalanceRow(dailyRow, columnOffset, initialBalance );
		dailyRow++;
		
		int currentDay = 1;  
		
		for (int i = 0; i < dailyReportRows.size(); i++) {
			ReportRowData dailyReportRow = dailyReportRows.get(i);  
			writeOneMonthDailyTransactionRow( dailyRow, columnOffset, currentDay, dailyReportRow );  
			
			currentDay = dailyReportRow.getDay();
			log.info("writing row: {} of {}, day = {}", i, dailyReportRows.size(), dailyReportRow.getDay());
			dailyRow++;
			
			onProgress(1,dailyReportRows.size(), 30, null);
		}
		
		log.info("Writing summary");
		/**
		 * Daily Summary
		 */
		writeOneMonthDailyTransactionTotal(dailyRow, columnOffset, totalDailyReportRow ); 
		onProgress(10, null);
		/**
		 * ======================================================
		 * 					RECAPITULATION CONTENT
		 * ======================================================
		 */
		log.info("Writing recapitulation");
		int summaryRow = row;
		int number = 1;
		
		for (ReportCategory reportCategory : ReportCategory.values()) {
			final ReportRowData summary = dailyReportSummary.get(reportCategory) == null?
					new ReportRowData(reportCategory) : dailyReportSummary.get(reportCategory) ;
			 
			final int rowNum  = summaryRow;  
			writeOneMonthTransacionSummaryRow(rowNum, columnOffset + 7, number, summary, initialBalance);   
			
			summaryRow++;
			number++;
			
			onProgress(1, ReportCategory.values().length, 30, null);
		}
		 
		writeOneMonthTransactionSummaryTotal(summaryRow, columnOffset + 7, totalDailyReportRow ); 
		onProgress(10, null);
		/**
		 * auto size columns
		 */ 
		autosizeColumn(headerRow, headerRow.getLastCellNum() - headerRow.getFirstCellNum(), BorderStyle.DOUBLE, HorizontalAlignment.CENTER);

	}   
	
	private void writeOneMonthTransactionSummaryTotal(  int summaryRow, int columnOffset,
			ReportRowData	totalDailyReportRow) {
		createRow(xsheet, summaryRow, columnOffset, BLANK, "Jumlah", BLANK, 
				curr(totalDailyReportRow.getDebitAmount()),
				curr(totalDailyReportRow.getCreditAmount())); 
				summaryRow++;
				
		createRow(xsheet, summaryRow, columnOffset, BLANK, "Saldo Kas Bulan ini", BLANK, BLANK,
				curr(totalDailyReportRow.getDebitAmount() - totalDailyReportRow.getCreditAmount())); 
		
	}


	private void writeOneMonthTransacionSummaryRow( int rowNum, int columnOffset, int number,
			  ReportRowData summary, CashBalance initialBalance) {
		 
		long debitAmount = summary.getDebitAmount();
		long creditAmount = summary.getCreditAmount();
		ReportCategory reportCategory = summary.getCategory();
		
		if(reportCategory.equals(ReportCategory.CASH_BALANCE)) {
			debitAmount = initialBalance.getActualBalance();
		}
		
		createRow(xsheet, rowNum, 
				columnOffset , number, reportCategory.name, reportCategory.code,
				curr(debitAmount), curr(creditAmount)  );  
		
		log.info("summary record No. {}", number);
	}


	private void writeOneMonthDailyTransactionTotal(int dailyRow, int columnOffset,
			ReportRowData totalDailyReportRow) {
		 
		createRow(xsheet, dailyRow, columnOffset,
				BLANK,BLANK, "Jumlah", BLANK,
				curr(totalDailyReportRow.getDebitAmount()),
				curr(totalDailyReportRow.getCreditAmount()),
				curr(totalDailyReportRow.getDebitAmount() - totalDailyReportRow.getCreditAmount()));
	}


	private void writeOneMonthDailyTransactionRow(int dailyRow, int columnOffset, int currentDay,
			ReportRowData dailyReportRow) {
		 
		final boolean sameDay = dailyReportRow.getDay() == currentDay; 
		createRow(xsheet, dailyRow, columnOffset,
		 		BLANK, sameDay ? BLANK : dailyReportRow.getDay(), dailyReportRow.getName(), dailyReportRow.getCategory().code,
				 curr(dailyReportRow.getDebitAmount()),
				 curr(dailyReportRow.getCreditAmount()),
				 "-" );  
	}


	private void writeOneMonthReportBalanceRow(int dailyRow, int columnOffset,
			CashBalance initialBalance) {
		int FIRST_DAY = 1;
		createRow(xsheet, dailyRow, columnOffset,
			 	BLANK, FIRST_DAY,"Saldo Awal", ReportCategory.CASH_BALANCE.code, 
				 curr(initialBalance.getActualBalance()), 0, curr(initialBalance.getActualBalance()));
	}


	private XSSFRow createOneMonthReportHeader(int row, int columnOffset) {
		
		final XSSFRow headerRow = createRow(xsheet, row, columnOffset, oneMonthReportHeader ); 
		return headerRow;
	}


	private void createOneMonthReportTitle(int row, int columnOffset, Filter filter) {
		 
		String period = DateUtil.MONTH_NAMES[filter.getMonth()-1] + " " + filter.getYear();
		addMergedRegion(xsheet, new CellRangeAddress(row, row, 0, 6), new CellRangeAddress(row, row, 7, 11));
		
		XSSFRow reportTitleRow = createRow(xsheet, row, columnOffset,
				 "Buku Harian Bulan "+ period , null, null, null, null, null, null,
				 "Rekapitulasi Harian Bulan "+ period);
		
		final int actualCells = reportTitleRow.getPhysicalNumberOfCells();
		for (int i = 0; i < actualCells; i++) {
			reportTitleRow.getCell(i).getCellStyle().setAlignment(HorizontalAlignment.CENTER);
			removeBorder(reportTitleRow.getCell(i).getCellStyle());
		}
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}


	
	
 
	

}
