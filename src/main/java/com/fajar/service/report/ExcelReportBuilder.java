package com.fajar.service.report;

import static com.fajar.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.util.ExcelReportUtil.autosizeColumn;
import static com.fajar.util.ExcelReportUtil.createRow;
import static com.fajar.util.ExcelReportUtil.removeBorder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ReportCategory;
import com.fajar.entity.CashBalance;
import com.fajar.service.WebConfigService;
import com.fajar.util.DateUtil;
import com.fajar.util.ThreadUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelReportBuilder {
	
	private static final String BLANK = "";
	@Autowired
	private WebConfigService webConfigService;
	
	
	/**
	 * write daily report for one month
	 * @param month
	 * @param year
	 * @param initialBalane
	 * @param dailyReportRows
	 * @param dailyReportSummary
	 * @param totalDailyReportRow
	 */
	public void writeDailyReport(int month, int year, CashBalance initialBalane, List<DailyReportRow> dailyReportRows,
			Map<ReportCategory, DailyReportRow> dailyReportSummary, DailyReportRow totalDailyReportRow) {
		
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hmmss");
		
		String reportName = webConfigService.getReportPath() + "/Daily-" + month + "-" + year + "_"+ time+ ".xlsx";
		XSSFWorkbook xwb  = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet("Daily "+month+"-"+year); 
		
		int row = 0;
		int columnOffset = 0;
		final int firstDate = 1;
		
		/**
		 * Report title
		 */
		String period = DateUtil.MONTH_NAMES[month-1]+" "+year;
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
					 curr(initialBalane.getActualBalance()),
					 0,
					 curr(initialBalane.getActualBalance()));
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
			 
			createRow(xsheet, rowNum, 
					columnOffset + 7, number, reportCategory.name, reportCategory.code,
					curr(summary.getDebitAmount()), curr(summary.getCreditAmount())  );  
			
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
		
		/**
		 * Write file to disk
		 */
		
		File f = new File(reportName);
		try {
			xwb.write(new FileOutputStream(f));
			if (f.canRead()) {
				log.info("DONE Writing Report: "+f.getAbsolutePath());
//				return f.getName();
			}
		} catch ( Exception e) { 
			e.printStackTrace();
		} 
		
	}
	
	
	
	public static CurrencyCell curr(long value) {
		return new CurrencyCell(value);
	}
	 
	
	/**
	 * 
	 * Cell Formats
	 * General
0
0.00
#,##0
#,##0.00
"$"#,##0_);("$"#,##0)
"$"#,##0_);[Red]("$"#,##0)
"$"#,##0.00_);("$"#,##0.00)
"$"#,##0.00_);[Red]("$"#,##0.00)
0%
0.00%
0.00E+00
# ?/?
# ??/??
m/d/yy
d-mmm-yy
d-mmm
mmm-yy
h:mm AM/PM
h:mm:ss AM/PM
h:mm
h:mm:ss
m/d/yy h:mm
reserved-0x17
reserved-0x18
reserved-0x19
reserved-0x1a
reserved-0x1b
reserved-0x1c
reserved-0x1d
reserved-0x1e
reserved-0x1f
reserved-0x20
reserved-0x21
reserved-0x22
reserved-0x23
reserved-0x24
#,##0_);(#,##0)
#,##0_);[Red](#,##0)
#,##0.00_);(#,##0.00)
#,##0.00_);[Red](#,##0.00)
_("$"* #,##0_);_("$"* (#,##0);_("$"* "-"_);_(@_)
_(* #,##0_);_(* (#,##0);_(* "-"_);_(@_)
_("$"* #,##0.00_);_("$"* (#,##0.00);_("$"* "-"??_);_(@_)
_(* #,##0.00_);_(* (#,##0.00);_(* "-"??_);_(@_)
mm:ss
[h]:mm:ss
mm:ss.0
##0.0E+0
@
	 */

}
