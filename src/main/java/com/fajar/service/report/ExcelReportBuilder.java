package com.fajar.service.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ReportCategory;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CashBalance;
import com.fajar.service.WebConfigService;

@Service
public class ExcelReportBuilder {
	
	@Autowired
	private WebConfigService webConfigService;

	public void writeDailyReport(int month, int year, CashBalance initialBalane, List<DailyReportRow> dailyReportRows,
			Map<ReportCategory, DailyReportRow> dailyReportSummary, DailyReportRow totalDailyReportRow) {
		
		String reportName = webConfigService.getReportPath() + "/Daily-" + month + "-" + year + ".xlsx";
		XSSFWorkbook xwb;
		XSSFCellStyle styleUmum;
		XSSFCellStyle styleNamaobat;
		
		xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet("Daily "+month+"-"+year); 
		
		int row = 0;
		
		XSSFRow headerRow = createRow(xsheet, row, 0,
				 "No","Tgl","Uraian","Kode","Debet","Kredit","Saldo"  
				);
		row++;
		XSSFRow initialBalanceRow = createRow(xsheet, row, 0,
				 "",1,"Saldo Awal",ReportCategory.CASH_BALANCE,initialBalane.getActualBalance(),0,initialBalane.getActualBalance() 
				);
		row++;
		
		for (int i = 0; i < dailyReportRows.size(); i++) {
			DailyReportRow dailyReportRow = dailyReportRows.get(i);
			XSSFRow dailyRow = createRow(xsheet, row, 0,
					 "",dailyReportRow.getDay() ,dailyReportRow.getName(),dailyReportRow.getCode(),
					 dailyReportRow.getDebitAmount(),
					 dailyReportRow.getCreditAmount(),
					 0
					);
			
			row++;
		}
		
		File f = new File(reportName);
		try {
			xwb.write(new FileOutputStream(f));
			if (f.canRead()) {
				System.out.println("DONE");
//				return f.getName();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static synchronized XSSFRow createRow(XSSFSheet sheet, int rownum, int offsetIndex, Object ...values) {
		
		XSSFRow row = sheet.createRow(rownum);
		fillRows(row, offsetIndex, values);
		return row ;
	}
	
	public static synchronized void fillRows(XSSFRow parentRow, int offsetIndex, Object ...values) {
		
		XSSFCell[] columns = new XSSFCell[values.length];
		for (int i = 0; i < values.length; i++) {
			Object cellValue = values[i];
			if(cellValue == null) {
				cellValue = "";
			}
			columns[i] = parentRow.createCell(offsetIndex+i);
			columns[i].setCellValue(cellValue.toString());
		}
	}

}
