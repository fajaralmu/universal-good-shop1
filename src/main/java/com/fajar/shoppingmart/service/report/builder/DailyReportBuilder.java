package com.fajar.shoppingmart.service.report.builder;

import static com.fajar.shoppingmart.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.shoppingmart.util.ExcelReportUtil.autosizeColumn;
import static com.fajar.shoppingmart.util.ExcelReportUtil.createRow;
import static com.fajar.shoppingmart.util.ExcelReportUtil.curr;
import static com.fajar.shoppingmart.util.ExcelReportUtil.removeBorder;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.data.ReportRowData;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.util.DateUtil;
import com.fajar.shoppingmart.util.MyFileUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DailyReportBuilder extends ReportBuilder{ 
	
	 public DailyReportBuilder(WebConfigService webConfigService) {
		super(webConfigService);
	 } 
	
	/**
	 * get daily report file
	 * @param reportRequest
	 * @return
	 */
	 @Override
	public File buildReport (ReportData reportRequest) {

		Filter filter = reportRequest.getFilter();
		String time = getDateTime();
		String sheetName = "Daily-"+filter.getMonth()+"-"+filter.getYear();
		
		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_"+ time+ ".xlsx";
		XSSFWorkbook xwb  = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName ); 
		
		writeDailyReportOneMonth( reportRequest);
		
		File file = MyFileUtil.getFile(xwb, reportName);
		return file;
	}
	
	/**
	 * write daily report for one month
	 * @param xsheet
	 * @param reportRequest
	 */
	private void writeDailyReportOneMonth( ReportData reportRequest) {
		
		Filter filter = reportRequest.getFilter();
		CashBalance initialBalance = reportRequest.getInitialBalance();
		List<ReportRowData> dailyReportRows = reportRequest.getDailyReportRows();
		Map<ReportCategory, ReportRowData> dailyReportSummary = reportRequest.getDailyReportSummary(); 
		ReportRowData totalDailyReportRow = reportRequest.getTotalDailyReportRow();
		
		int row = 0;
		final int columnOffset = 0;
		//final int firstDate = 1;
		
		/**
		 * Report title
		 */
		
		createOneMonthReportTitle(row, columnOffset, filter); 
		
		row++;
		row++;
		 
		final XSSFRow headerRow = createOneMonthReportHeader(row, columnOffset ); 
		
		row++;
		
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
		}
		
		/**
		 * Daily Summary
		 */
		writeOneMonthDailyTransactionTotal(dailyRow, columnOffset, totalDailyReportRow ); 
		
		/**
		 * ======================================================
		 * 					RECAPITULATION CONTENT
		 * ======================================================
		 */
		 
		int summaryRow = row;
		int number = 1;
		
		for (ReportCategory reportCategory : ReportCategory.values()) {
			final ReportRowData summary = dailyReportSummary.get(reportCategory) == null?
					new ReportRowData(reportCategory) : dailyReportSummary.get(reportCategory) ;
			 
			final int rowNum  = summaryRow;  
			writeOneMonthTransacionSummaryRow(rowNum, columnOffset + 7, number, summary, initialBalance);   
			
			summaryRow++;
			number++;
		}
		 
		writeOneMonthTransactionSummaryTotal(summaryRow, columnOffset + 7, totalDailyReportRow ); 
		
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
		final Object[] columnNames = new Object[] {
				 "No", "Tgl", "Uraian", "Kode", "Debet", "Kredit","Saldo" ,
				 "No", "Perkiraan", "Kode", "Debet", "Kredit"
		}; 
		final XSSFRow headerRow = createRow(xsheet, row, columnOffset, columnNames ); 
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


	
	
 
	

}
