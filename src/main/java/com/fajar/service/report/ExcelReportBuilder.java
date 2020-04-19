package com.fajar.service.report;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ReportCategory;
import com.fajar.entity.CashBalance;
import com.fajar.service.WebConfigService;
import com.fajar.util.DateUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelReportBuilder {
	
	@Autowired
	private WebConfigService webConfigService;
	
	public static void removeBorder(XSSFCellStyle cellStyle) {
		cellStyle.setBorderBottom(BorderStyle.NONE);
		cellStyle.setBorderTop(BorderStyle.NONE);
		cellStyle.setBorderLeft(BorderStyle.NONE);
		cellStyle.setBorderRight(BorderStyle.NONE);
		
	}

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
		CellRangeAddress region1 = new CellRangeAddress(row, row, 0, 6);
		CellRangeAddress region2 = new CellRangeAddress(row, row, 7, 11);
		xsheet.addMergedRegion(region1 );
		xsheet.addMergedRegion(region2 );
		
		XSSFRow reportTitleRow = createRow(xsheet, row, columnOffset,
				 "Buku Harian Bulan "+ period , null, null, null, null, null, null,
				 "Rekapitulasi Harian Bulan "+ period  
				);
		
		int actualCells = reportTitleRow.getPhysicalNumberOfCells();
		for (int i = 0; i < actualCells; i++) {
			reportTitleRow.getCell(i).getCellStyle().setAlignment(HorizontalAlignment.CENTER);
			removeBorder(reportTitleRow.getCell(i).getCellStyle());
		}
		
		row++;
		row++;
		
		Object[] columnNames = new Object[] {
				 "No", "Tgl", "Uraian", "Kode", "Debet", "Kredit","Saldo" ,
				 "No", "Perkiraan", "Kode", "Debet", "Kredit"
		};
		/**
		 * Report Table Columns
		 */
		XSSFRow headerRow = createRow(xsheet, row, columnOffset,
				columnNames 
				); 
		
		row++;
		
		/**
		 * =========================================
		 * 				Daily Content
		 * =========================================
		 
		 * First Row (Previous Balance)
		 */ 
		int dailyRow = row;
		createRow(xsheet, dailyRow, columnOffset,
				 	"", firstDate,"Saldo Awal", ReportCategory.CASH_BALANCE.code, 
					 currency(initialBalane.getActualBalance()),
					 0,
					 currency(initialBalane.getActualBalance()) 
				);
		dailyRow++;
		
		int currentDay = firstDate; 
		
		for (int i = 0; i < dailyReportRows.size(); i++) {
			DailyReportRow dailyReportRow = dailyReportRows.get(i);
			
			boolean sameDay = false;
			if(dailyReportRow.getDay() == currentDay) {
				sameDay = true;
			}
			currentDay = dailyReportRow.getDay(); 
			
			createRow(xsheet, dailyRow, columnOffset,
					 		"", sameDay ? "" : currentDay, dailyReportRow.getName(), dailyReportRow.getCategory().code,
							 currency(dailyReportRow.getDebitAmount()),
							 currency(dailyReportRow.getCreditAmount()),
							 "-"
					);

			log.info("writing row: {} of {}", i, dailyReportRows.size());
			dailyRow++;
		}
		
		/**
		 * Daily Summary
		 */
		createRow(xsheet, dailyRow, columnOffset,
						"","","Jumlah","",
						currency(totalDailyReportRow.getDebitAmount()),
						currency(totalDailyReportRow.getCreditAmount()),
						currency(totalDailyReportRow.getDebitAmount() - totalDailyReportRow.getCreditAmount()));
		
		
		/**
		 * ======================================================
		 * 					RECAPITULATION CONTENT
		 * ======================================================
		 */
		 
		int summaryRow = row;
		int number = 1;
		
		for (ReportCategory reportCategory : ReportCategory.values()) {
			DailyReportRow summary = dailyReportSummary.get(reportCategory);
			
			if(summary == null) {
				summary = new DailyReportRow();
			}
			
			createRow(xsheet, summaryRow, 
					columnOffset + 7,
					number,
					reportCategory.name,
					reportCategory.code,
					currency(summary.getDebitAmount()),
					currency(summary.getCreditAmount()) 
					); 
			
			summaryRow++;
			number++;
		}
		
		createRow(xsheet, summaryRow, columnOffset + 7, "", "Jumlah", "", 
				currency(totalDailyReportRow.getDebitAmount()),
				currency(totalDailyReportRow.getCreditAmount())); 
				summaryRow++;
		createRow(xsheet, summaryRow, columnOffset + 7, "", " Saldo Kas Bulan ini", "", "",
				currency(totalDailyReportRow.getDebitAmount() -  totalDailyReportRow.getCreditAmount()));
		
		/**
		 * auto size columns
		 */
		
		for (int i = 0; i < columnNames.length; i++) {
			XSSFCell cell = headerRow.getCell(i);
			cell.getCellStyle().setBorderTop(BorderStyle.DOUBLE);
			cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
			xsheet.autoSizeColumn(i);
		}
		
		/**
		 * Write file to disk
		 */
		
		File f = new File(reportName);
		try {
			xwb.write(new FileOutputStream(f));
			if (f.canRead()) {
				System.out.println("DONE Writing Report: "+f.getAbsolutePath());
//				return f.getName();
			}
		} catch ( Exception e) { 
			e.printStackTrace();
		}
//		System.out.println("FORMAT=============");
//		for(String s : BuiltinFormats.getAll()) {System.out.println(s);}
		
	}
	
	public static synchronized XSSFRow createRow(XSSFSheet sheet, int rownum, int offsetIndex, Object ...values) {
		
		final XSSFRow existingRow = sheet.getRow(rownum);
		XSSFRow row = existingRow  == null ? sheet.createRow(rownum) : existingRow;
		
		XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
		setAllBorder(style, BorderStyle.THIN); 
		fillRows(row, offsetIndex, style, values); 
		 
		for (int i = 0; i < values.length; i++) {
			sheet.autoSizeColumn(i);
		}
		
		return row ;
	}
	
	public static void setAllBorder(XSSFCellStyle cellStyle, BorderStyle borderStyle) {
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setBorderLeft(borderStyle); 
	}
	
	public static CellStyle createCellStyle(XSSFWorkbook workbook) {
		return workbook.createCellStyle();
	}
	
	public static synchronized void fillRows(XSSFRow parentRow, int offsetIndex, CellStyle sourceStyle, Object ...values) {
		DataFormat fmt = parentRow.getSheet().getWorkbook().createDataFormat();
		XSSFCell[] columns = new XSSFCell[values.length];
		for (int i = 0; i < values.length; i++) {
			Object cellValue = values[i];
			if(cellValue == null) {
				cellValue = "";
			}
			columns[i] = parentRow.createCell(offsetIndex+i);
			
			CellStyle cellStyle  =  createCellStyle(parentRow.getSheet().getWorkbook());
			
			if(sourceStyle != null) {
				cellStyle.cloneStyleFrom(sourceStyle);
				columns[i].setCellStyle(cellStyle);
			}
			
			if(cellValue instanceof CurrencyCell) {
				columns[i].setCellValue(Double.parseDouble(((CurrencyCell)cellValue).getValue().toString())); 
				columns[i].getCellStyle().setDataFormat( fmt.getFormat("#,##0") );
			}else {
				try {
					columns[i].setCellValue(Double.parseDouble(cellValue .toString()));  
					 
				}catch (Exception e) { 
					columns[i].setCellValue(cellValue.toString()); 
				}
			}
			
		}
	}
	
	public static CurrencyCell currency(long value) {
		return new CurrencyCell(value);
	}
	
	@Data 
	static class CurrencyCell{
		
		private final Long value;
		public CurrencyCell(long value) {
			this.value = value;
		}
		
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
