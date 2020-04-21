package com.fajar.service.report;

import static com.fajar.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.util.ExcelReportUtil.autosizeColumn;
import static com.fajar.util.ExcelReportUtil.createRow;
import static com.fajar.util.ExcelReportUtil.removeBorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ReportCategory;
import com.fajar.entity.CashBalance;
import com.fajar.service.WebConfigService;
import com.fajar.util.DateUtil;
import com.fajar.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelReportBuilder {
	
	private static final String BLANK = "";
	@Autowired
	private WebConfigService webConfigService;
	
	private String reportPath;
	
	@PostConstruct
	public void init() {
		this.reportPath = webConfigService.getReportPath();
	}
	/**
	 * get daily report file
	 * @param reportRequest
	 * @return
	 */
	public File getDailyReportFile (ReportRequest reportRequest) {

		Filter filter = reportRequest.getFilter();
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Daily-"+filter.getMonth()+"-"+filter.getYear();
		
		String reportName = reportPath + "/" + sheetName + "_"+ time+ ".xlsx";
		XSSFWorkbook xwb  = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName ); 
		
		writeDailyReport(xsheet, reportRequest);
		
		File file = getFile(xwb, reportName);
		return file;
	}
	
	/**
	 * write daily report for one month
	 *
	 * 
	 * @param xsheet
	 * @param reportRequest
	 */
	private void writeDailyReport(XSSFSheet xsheet, ReportRequest reportRequest) {
		
		Filter filter = reportRequest.getFilter();
		CashBalance initialBalance = reportRequest.getInitialBalance();
		List<DailyReportRow> dailyReportRows = reportRequest.getDailyReportRows();
		Map<ReportCategory, DailyReportRow> dailyReportSummary = reportRequest.getDailyReportSummary(); 
		DailyReportRow totalDailyReportRow = reportRequest.getTotalDailyReportRow();
		
		int row = 0;
		final int columnOffset = 0;
		final int firstDate = 1;
		
		/**
		 * Report title
		 */
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
		
		row++;
		row++;
		
		final Object[] columnNames = new Object[] {
				 "No", "Tgl", "Uraian", "Kode", "Debet", "Kredit","Saldo" ,
				 "No", "Perkiraan", "Kode", "Debet", "Kredit"
		};
		/**
		 * Report Table Columns
		 */
		final XSSFRow headerRow = createRow(xsheet, row, columnOffset, columnNames ); 
		
		row++;
		
		/**
		 * =========================================
		 * 				Daily Content
		 * =========================================
		 
		 * First Row (Previous Balance)
		 */ 
		int dailyRow = row;
		createRow(xsheet, dailyRow, columnOffset,
				 	BLANK, firstDate,"Saldo Awal", ReportCategory.CASH_BALANCE.code, 
					 curr(initialBalance.getActualBalance()), 0, curr(initialBalance.getActualBalance()));
		dailyRow++;
		
		int currentDay = firstDate;  
		
		for (int i = 0; i < dailyReportRows.size(); i++) {
			DailyReportRow dailyReportRow = dailyReportRows.get(i);
			
			final boolean sameDay = dailyReportRow.getDay() == currentDay; 
			currentDay = dailyReportRow.getDay();  
			 
			createRow(xsheet, dailyRow, columnOffset,
			 		BLANK, sameDay ? BLANK : currentDay, dailyReportRow.getName(), dailyReportRow.getCategory().code,
					 curr(dailyReportRow.getDebitAmount()),
					 curr(dailyReportRow.getCreditAmount()),
					 "-" );  
			 
			log.info("writing row: {} of {}", i, dailyReportRows.size());
			dailyRow++;
		}
		
		/**
		 * Daily Summary
		 */
		createRow(xsheet, dailyRow, columnOffset,
						BLANK,BLANK,"Jumlah",BLANK,
						curr(totalDailyReportRow.getDebitAmount()),
						curr(totalDailyReportRow.getCreditAmount()),
						curr(totalDailyReportRow.getDebitAmount() - totalDailyReportRow.getCreditAmount())); 
		
		/**
		 * ======================================================
		 * 					RECAPITULATION CONTENT
		 * ======================================================
		 */
		 
		int summaryRow = row;
		int number = 1;
		
		for (ReportCategory reportCategory : ReportCategory.values()) {
			final DailyReportRow summary = dailyReportSummary.get(reportCategory) == null?
					new DailyReportRow() : dailyReportSummary.get(reportCategory) ;
			 
			final int rowNum  = summaryRow; 
			long debitAmount = summary.getDebitAmount();
			long creditAmount = summary.getCreditAmount();
			 
			if(reportCategory.equals(ReportCategory.CASH_BALANCE)) {
				debitAmount = initialBalance.getActualBalance();
			}
			
			createRow(xsheet, rowNum, 
					columnOffset + 7, number, reportCategory.name, reportCategory.code,
					curr(debitAmount), curr(creditAmount)  );  
			
			log.info("summary record No. {}", number);
			
			summaryRow++;
			number++;
		}
		 
		createRow(xsheet, summaryRow, columnOffset + 7, BLANK, "Jumlah", BLANK, 
				curr(totalDailyReportRow.getDebitAmount()),
				curr(totalDailyReportRow.getCreditAmount())); 
				summaryRow++;
		createRow(xsheet, summaryRow , columnOffset + 7, BLANK, " Saldo Kas Bulan ini", BLANK, BLANK,
				curr(totalDailyReportRow.getDebitAmount() -  totalDailyReportRow.getCreditAmount()));
		  
		
		/**
		 * auto size columns
		 */ 
		autosizeColumn(headerRow, columnNames.length, BorderStyle.DOUBLE, HorizontalAlignment.CENTER);
		
		
	}  
	
	public static CurrencyCell curr(long value) {
		return new CurrencyCell(value);
	}
	
	/**
	 * Monthly
	 *  
	 */
	
	public void getMonthyReport(ReportRequest reportRequest) { 
		Filter filter = reportRequest.getFilter();
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Monthly-"+filter.getYear();
		
		String reportName = reportPath + "/" + sheetName + "_"+ time+ ".xlsx";
		XSSFWorkbook xwb  = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName ); 
		
		writeMonthlyReport(xsheet, reportRequest, reportName);
		
		getFile(xwb, reportName);
	}
	
	public static void main(String[ ]aa) {
		ExcelReportBuilder excelReportBuilder = new ExcelReportBuilder();
		excelReportBuilder.reportPath = "D:\\Development\\Files\\Web\\Shop1\\Reports";
		excelReportBuilder.getMonthyReport(ReportRequest.builder().filter(Filter.builder().year(2018).build()).build());
	}
	
	private void writeMonthlyReport(XSSFSheet xsheet, ReportRequest reportRequest, String reportName) {
		// TODO Auto-generated method stub
		Map<Integer, Map<ReportCategory, DailyReportRow>> reportContent = reportRequest.getMonthyReportContent();
		
		/**
		 * Static Label
		 */
		addMergedRegion(xsheet, new CellRangeAddress(1, 3, 0, 0), new CellRangeAddress(1, 3, 1, 1));
		createRow(xsheet, 1, 0, "Kode Akun", "Nama Akun");
		
		ReportCategory[] reportCategories = ReportCategory.values();
		int offsetRow = 4;
		int offsetColumn = 1;
		
		for (int i = 0; i < reportCategories.length; i++) {
			ReportCategory reportCategory = reportCategories[i];
			createRow(xsheet, offsetRow + i, 0, reportCategory.code, reportCategory.name);
		}
		
		int totalRowNum = offsetRow + reportCategories.length;
		createRow(xsheet, totalRowNum, 0, "", "Jumlah");
		
		/**
		 * Month Names
		 */
		int triwulan = 0;
		 for(int i = 1; i <= 12; i++) {
			 String monthName = DateUtil.MONTH_NAMES[i - 1];
			 addMergedRegion(xsheet, new CellRangeAddress(2, 2, i * 2, i * 2 + 1));
			 
			 createRow(xsheet, 2, i * 2, monthName);
			 createRow(xsheet, 3, i * 2, "D (K)", "K (D)"); 
			 
			 if(i % 3 == 0) {
				 addMergedRegion(xsheet, new CellRangeAddress(1, 1, (i - 2)*2 , i*2  + 1));
				 createRow(xsheet, 1,(i - 2)*2, "Triwulan "+StringUtil.GREEK_NUMBER[triwulan]);
				 triwulan++;
			 }
		 }
		 
		 /**
		  * Values
		  */
		 for(int i = 1; i <= 12; i++) {
			 
			 Map<ReportCategory, DailyReportRow> monthData = reportContent.get(i);
			 long totalDebit = 0L;
			 long totalCredit = 0L;
			 
			 for (int j = 0; j< reportCategories.length; j++) {
					ReportCategory reportCategory = reportCategories[j]; 
					DailyReportRow categoryData = monthData.get(reportCategory);
					
					if(null == categoryData) {
						categoryData = new DailyReportRow();
					}
					createRow(xsheet, offsetRow + j, i * 2, 
							curr(categoryData.getCreditAmount()), 
							curr(categoryData.getDebitAmount()));
					
					totalDebit+= categoryData.getDebitAmount();
					totalCredit+= categoryData.getCreditAmount();
				}
			 createRow(xsheet,  totalRowNum, i * 2, curr(totalCredit), curr(totalDebit));
			 
		 }
		 
		 
		 int rows = offsetRow + reportCategories.length;
		 for (int i = 0; i <= rows; i++) {
			 XSSFRow xssfRow = xsheet.getRow(i);
			 autosizeColumn(xssfRow, 2 + 12*2, BorderStyle.THIN, HorizontalAlignment.CENTER);
		}
		
	}
	
	/**
	 * get file from XSSFWorkbook
	 * @param xssfWorkbook
	 * @param fileName
	 * @return
	 */
	public static File getFile(XSSFWorkbook xssfWorkbook, String fileName) {
		/**
		 * Write file to disk
		 */
		File f = new File(fileName);
		try {
			xssfWorkbook.write(new FileOutputStream(f));
			if (f.canRead()) {
				log.info("DONE Writing Report: "+f.getAbsolutePath());
//				return f.getName();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				xssfWorkbook.write(bos);
			} finally {
			    bos.close();
			}
			byte[] bytes = bos.toByteArray();
			return f;
		} catch ( Exception e) { 
			e.printStackTrace();
			return null;
		} 
	}
	 
	
	

}
