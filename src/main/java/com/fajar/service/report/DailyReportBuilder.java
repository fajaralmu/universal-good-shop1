package com.fajar.service.report;

import static com.fajar.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.util.ExcelReportUtil.autosizeColumn;
import static com.fajar.util.ExcelReportUtil.createRow;
import static com.fajar.util.ExcelReportUtil.curr;
import static com.fajar.util.ExcelReportUtil.removeBorder;
import static com.fajar.util.ExcelReportUtil.setBorderTop;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import com.fajar.entity.CashBalance;
import com.fajar.service.WebConfigService;
import com.fajar.util.DateUtil;
import com.fajar.util.MyFileUtil;
import com.fajar.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DailyReportBuilder {
	
	private static final String BLANK = "";
	private static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a";
	
	@Autowired
	private WebConfigService webConfigService;
	
	private String reportPath;
	
	@PostConstruct
	public void init() {
		this.reportPath = webConfigService.getReportPath();
	}
	
	
	/**
	 * ====================================
	 * 				Daily Report
	 * ====================================
	 * 
	 */
	
	/**
	 * get daily report file
	 * @param reportRequest
	 * @return
	 */
	public File getDailyReportFile (ReportRequest reportRequest) {

		Filter filter = reportRequest.getFilter();
		String time = DateUtil.formatDate(new Date(), DATE_PATTERN);
		String sheetName = "Daily-"+filter.getMonth()+"-"+filter.getYear();
		
		String reportName = reportPath + "/" + sheetName + "_"+ time+ ".xlsx";
		XSSFWorkbook xwb  = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName ); 
		
		writeDailyReportOneMonth(xsheet, reportRequest);
		
		File file = MyFileUtil.getFile(xwb, reportName);
		return file;
	}
	
	/**
	 * write daily report for one month
	 * @param xsheet
	 * @param reportRequest
	 */
	private void writeDailyReportOneMonth(XSSFSheet xsheet, ReportRequest reportRequest) {
		
		Filter filter = reportRequest.getFilter();
		CashBalance initialBalance = reportRequest.getInitialBalance();
		List<DailyReportRow> dailyReportRows = reportRequest.getDailyReportRows();
		Map<ReportCategory, DailyReportRow> dailyReportSummary = reportRequest.getDailyReportSummary(); 
		DailyReportRow totalDailyReportRow = reportRequest.getTotalDailyReportRow();
		
		int row = 0;
		final int columnOffset = 0;
		//final int firstDate = 1;
		
		/**
		 * Report title
		 */
		
		createOneMonthReportTitle(xsheet, row, columnOffset, filter); 
		
		row++;
		row++;
		 
		final XSSFRow headerRow = createOneMonthReportHeader(xsheet, row, columnOffset ); 
		
		row++;
		
		/**
		 * =========================================
		 * 				Daily Content
		 * =========================================
		 
		 * First Row (Previous Balance)
		 */ 
		int dailyRow = row;
		writeOneMonthReportBalanceRow(xsheet, dailyRow, columnOffset, initialBalance );
		dailyRow++;
		
		int currentDay = 1;  
		
		for (int i = 0; i < dailyReportRows.size(); i++) {
			DailyReportRow dailyReportRow = dailyReportRows.get(i);  
			writeOneMonthDailyTransactionRow(xsheet, dailyRow, columnOffset, currentDay, dailyReportRow );  
			
			currentDay = dailyReportRow.getDay();
			log.info("writing row: {} of {}, day = {}", i, dailyReportRows.size(), dailyReportRow.getDay());
			dailyRow++;
		}
		
		/**
		 * Daily Summary
		 */
		writeOneMonthDailyTransactionTotal(xsheet, dailyRow, columnOffset, totalDailyReportRow ); 
		
		/**
		 * ======================================================
		 * 					RECAPITULATION CONTENT
		 * ======================================================
		 */
		 
		int summaryRow = row;
		int number = 1;
		
		for (ReportCategory reportCategory : ReportCategory.values()) {
			final DailyReportRow summary = dailyReportSummary.get(reportCategory) == null?
					new DailyReportRow(reportCategory) : dailyReportSummary.get(reportCategory) ;
			 
			final int rowNum  = summaryRow;  
			writeOneMonthTransacionSummaryRow(xsheet, rowNum, columnOffset + 7, number, summary, initialBalance);   
			
			summaryRow++;
			number++;
		}
		 
		writeOneMonthTransactionSummaryTotal(xsheet, summaryRow, columnOffset + 7, totalDailyReportRow ); 
		
		/**
		 * auto size columns
		 */ 
		autosizeColumn(headerRow, headerRow.getLastCellNum() - headerRow.getFirstCellNum(), BorderStyle.DOUBLE, HorizontalAlignment.CENTER);

	}   
	
	private void writeOneMonthTransactionSummaryTotal(XSSFSheet xsheet, int summaryRow, int columnOffset,
			DailyReportRow	totalDailyReportRow) {
		createRow(xsheet, summaryRow, columnOffset, BLANK, "Jumlah", BLANK, 
				curr(totalDailyReportRow.getDebitAmount()),
				curr(totalDailyReportRow.getCreditAmount())); 
				summaryRow++;
				
		createRow(xsheet, summaryRow, columnOffset, BLANK, "Saldo Kas Bulan ini", BLANK, BLANK,
				curr(totalDailyReportRow.getDebitAmount() - totalDailyReportRow.getCreditAmount())); 
		
	}


	private void writeOneMonthTransacionSummaryRow(XSSFSheet xsheet, int rowNum, int columnOffset, int number,
			  DailyReportRow summary, CashBalance initialBalance) {
		 
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


	private void writeOneMonthDailyTransactionTotal(XSSFSheet xsheet, int dailyRow, int columnOffset,
			DailyReportRow totalDailyReportRow) {
		 
		createRow(xsheet, dailyRow, columnOffset,
				BLANK,BLANK, "Jumlah", BLANK,
				curr(totalDailyReportRow.getDebitAmount()),
				curr(totalDailyReportRow.getCreditAmount()),
				curr(totalDailyReportRow.getDebitAmount() - totalDailyReportRow.getCreditAmount()));
	}


	private void writeOneMonthDailyTransactionRow(XSSFSheet xsheet, int dailyRow, int columnOffset, int currentDay,
			DailyReportRow dailyReportRow) {
		 
		final boolean sameDay = dailyReportRow.getDay() == currentDay; 
		createRow(xsheet, dailyRow, columnOffset,
		 		BLANK, sameDay ? BLANK : dailyReportRow.getDay(), dailyReportRow.getName(), dailyReportRow.getCategory().code,
				 curr(dailyReportRow.getDebitAmount()),
				 curr(dailyReportRow.getCreditAmount()),
				 "-" );  
	}


	private void writeOneMonthReportBalanceRow(XSSFSheet xsheet, int dailyRow, int columnOffset,
			CashBalance initialBalance) {
		int FIRST_DAY = 1;
		createRow(xsheet, dailyRow, columnOffset,
			 	BLANK, FIRST_DAY,"Saldo Awal", ReportCategory.CASH_BALANCE.code, 
				 curr(initialBalance.getActualBalance()), 0, curr(initialBalance.getActualBalance()));
	}


	private XSSFRow createOneMonthReportHeader(XSSFSheet xsheet, int row, int columnOffset) {
		final Object[] columnNames = new Object[] {
				 "No", "Tgl", "Uraian", "Kode", "Debet", "Kredit","Saldo" ,
				 "No", "Perkiraan", "Kode", "Debet", "Kredit"
		}; 
		final XSSFRow headerRow = createRow(xsheet, row, columnOffset, columnNames ); 
		return headerRow;
	}


	private void createOneMonthReportTitle(XSSFSheet xsheet, int row, int columnOffset, Filter filter) {
		 
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
