package com.fajar.util;

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

import com.fajar.service.report.CurrencyCell;

public class ExcelReportUtil {
	/**
	 * 
	 * @param row
	 * @param length
	 * @param borderStyle Nullable
	 * @param horizontalAlignment Nullable
	 */
	public static void autosizeColumn(XSSFRow row, int length, BorderStyle borderStyle, HorizontalAlignment horizontalAlignment) {
		for(int i = 0; i < length; i++) {
			XSSFCell cell = row.getCell(i);
			if(borderStyle != null)
				cell.getCellStyle().setBorderTop(borderStyle);
			if(horizontalAlignment!=null)
				cell.getCellStyle().setAlignment(horizontalAlignment);
			row.getSheet().autoSizeColumn(i);
		}
	}
	
	public static void addMergedRegion(XSSFSheet sheet, CellRangeAddress...cellRangeAddresses) {
		for (int i = 0; i < cellRangeAddresses.length; i++) {
			sheet.addMergedRegion(cellRangeAddresses[i]);
		}
	}
	
	public static XSSFRow createRow(XSSFSheet sheet, int rownum, int offsetIndex, Object ...values) {
		
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
	
	public static void removeBorder(XSSFCellStyle cellStyle) {
		setAllBorder(cellStyle, BorderStyle.NONE);
	}
	
	public static CellStyle createCellStyle(XSSFWorkbook workbook) {
		return workbook.createCellStyle();
	}
	
	public static void fillRows(XSSFRow parentRow, int offsetIndex, CellStyle sourceStyle, Object ...values) {
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

}
