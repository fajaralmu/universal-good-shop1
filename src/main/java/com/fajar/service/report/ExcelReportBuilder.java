package com.fajar.service.report;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelReportBuilder {
	
	@Autowired
	private WebConfigService webConfigService;

	public void writeDailyReport(int month, int year, CashBalance initialBalane, List<DailyReportRow> dailyReportRows,
			Map<ReportCategory, DailyReportRow> dailyReportSummary, DailyReportRow totalDailyReportRow) {
		
		String reportName = webConfigService.getReportPath() + "/Daily-" + month + "-" + year + ".xlsx";
		XSSFWorkbook xwb  = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet("Daily "+month+"-"+year); 
		
		int row = 0;
		int columnOffset = 0;
		final int firstDate = 1;
		
		XSSFRow headerRow = createRow(xsheet, row, columnOffset,
				 "No","Tgl","Uraian","Kode","Debet","Kredit","Saldo"  
				);
		
		for (int i = 0; i < 7; i++) {
			XSSFCell cell = headerRow.getCell(i);
			cell.getCellStyle().setBorderBottom(BorderStyle.DOUBLE);
		}
		
		row++;
		createRow(xsheet, row, columnOffset,
				 "", firstDate,"Saldo Awal", ReportCategory.CASH_BALANCE,initialBalane.getActualBalance(),0,initialBalane.getActualBalance() 
				);
		row++;
		
		int currentDay = firstDate;
		
		for (int i = 0; i < dailyReportRows.size(); i++) {
			DailyReportRow dailyReportRow = dailyReportRows.get(i);
			
			boolean sameDay = false;
			if(dailyReportRow.getDay() == currentDay) {
				sameDay = true;
			}
			currentDay = dailyReportRow.getDay(); 
			
			createRow(xsheet, row, columnOffset,
					 "", sameDay ? "" : currentDay, dailyReportRow.getName(), dailyReportRow.getCode(),
					 dailyReportRow.getDebitAmount(),
					 dailyReportRow.getCreditAmount(),
					 0
					);

			log.info("writing row: {} of {}", row, dailyReportRows.size());
			row++;
		}
		
		createRow(xsheet, row, columnOffset,
				"","","Jumlah","",
				totalDailyReportRow.getDebitAmount(),
				totalDailyReportRow.getCreditAmount(),
				(totalDailyReportRow.getDebitAmount() - totalDailyReportRow.getCreditAmount()));
		
		File f = new File(reportName);
		try {
			xwb.write(new FileOutputStream(f));
			if (f.canRead()) {
				System.out.println("DONE Writing Report: "+f.getAbsolutePath());
//				return f.getName();
			}
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static synchronized XSSFRow createRow(XSSFSheet sheet, int rownum, int offsetIndex, Object ...values) {
		
		XSSFRow row = sheet.createRow(rownum);
		
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
	
	public static synchronized void fillRows(XSSFRow parentRow, int offsetIndex, CellStyle cellStyle, Object ...values) {
		 
		XSSFCell[] columns = new XSSFCell[values.length];
		for (int i = 0; i < values.length; i++) {
			Object cellValue = values[i];
			if(cellValue == null) {
				cellValue = "";
			}
			columns[i] = parentRow.createCell(offsetIndex+i);
			try {
				columns[i].setCellValue(Double.parseDouble(cellValue .toString()));
			}catch (Exception e) { 
				columns[i].setCellValue(cellValue.toString()); 
			}
			if(cellStyle != null)
				columns[i].setCellStyle(cellStyle);
		}
	}
	
	

}
