package com.fajar.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.service.report.CurrencyCell;
import com.fajar.service.report.CustomCell;
import com.fajar.service.report.NumericCell;

public class ExcelReportUtil {
	/**
	 * 
	 * @param row
	 * @param countOfColumns
	 * @param borderStyle @Nullable
	 * @param horizontalAlignment @Nullable
	 */
	public static void autosizeColumn(XSSFRow row, int countOfColumns, BorderStyle borderStyle, HorizontalAlignment horizontalAlignment) {
		if(row == null) {
			return;
		}
		for(int i = 0; i < countOfColumns; i++) {
			XSSFCell cell = row.getCell(i);
			if(cell == null) {
				continue;
			}
			if(borderStyle != null)
				cell.getCellStyle().setBorderTop(borderStyle);
			if(horizontalAlignment!=null)
				cell.getCellStyle().setAlignment(horizontalAlignment);
			
			cell.getCellStyle().setVerticalAlignment(VerticalAlignment.CENTER);
			row.getSheet().autoSizeColumn(i);
		}
	}
	
	public static void addMergedRegion(XSSFSheet sheet, CellRangeAddress...cellRangeAddresses) {
		for (int i = 0; i < cellRangeAddresses.length; i++) {
			sheet.addMergedRegion(cellRangeAddresses[i]);
		}
	}
	
	public static XSSFRow createRow(final XSSFSheet sheet, final int rownum, final int offsetIndex, final Object ...values) {
		
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
	
	public static void setBorder(XSSFCellStyle cellStyle, BorderStyle top, BorderStyle bottom, BorderStyle right,
			BorderStyle left) { 
			cellStyle.setBorderBottom(bottom);
			cellStyle.setBorderTop(top);
			cellStyle.setBorderRight(right);
			cellStyle.setBorderLeft(left); 
	 
	}
	
	public static void setAllBorder(XSSFCellStyle cellStyle, BorderStyle borderStyle) {
		setBorder(cellStyle, borderStyle, borderStyle, borderStyle, borderStyle); 
	}
	
	public static void removeBorder(XSSFCellStyle cellStyle) {
		setAllBorder(cellStyle, BorderStyle.NONE);
	}
	
	public static CellStyle createCellStyle(XSSFWorkbook workbook) {
		return workbook.createCellStyle();
	}
	
	/**
	 * fill row with values
	 * @param parentRow
	 * @param offsetIndex
	 * @param sourceStyle
	 * @param values
	 */
	public static void fillRows(XSSFRow parentRow, int offsetIndex, CellStyle sourceStyle, Object ...values) {
		DataFormat fmt = parentRow.getSheet().getWorkbook().createDataFormat();
		XSSFCell[] cells = new XSSFCell[values.length];
		for (int i = 0; i < values.length; i++) {
			Object cellValue = values[i];
			if(cellValue == null) {
				cellValue = "";
			}
			XSSFCell cell = parentRow.createCell(offsetIndex+i);
			
			CellStyle cellStyle  =  createCellStyle(parentRow.getSheet().getWorkbook());
			
			if(sourceStyle != null) {
				cellStyle.cloneStyleFrom(sourceStyle);
				cell.setCellStyle(cellStyle);
			} 
			
			setCellValue(cell, cellValue, fmt);
			
			cells[i] = cell;
		}
	}
	
	/**
	 * set value for cell
	 * @param cell
	 * @param value
	 * @param fmt
	 */
	public static void setCellValue(XSSFCell cell, Object value, DataFormat fmt) {
		
		if(null == value) {
			return;
		}
		if(value.getClass().getSuperclass().equals(CustomCell.class) && ((CustomCell)value).getValue() != null) { 
		 
			if(value instanceof CurrencyCell) {
			 	String stringValue = ((CurrencyCell)value).getValue().toString();
				value = (Double.parseDouble(stringValue)); 
				cell.setCellValue((Double) value);
				
				if(null != fmt)
					cell.getCellStyle().setDataFormat( fmt.getFormat("#,##0") );
			}else if(value instanceof NumericCell) {
				value = (Double.parseDouble(((CustomCell)value).getValue().toString() )); 
				cell.setCellValue((Double) value);
			}
			
		}else if(value instanceof Double  ) {
			cell.setCellValue((Double) value);
			
		}else if(value instanceof Date) {
			cell.setCellValue((Date) value);
			
		}else if(value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
			
		}else if(value instanceof Calendar) {
			cell.setCellValue((Calendar) value);
			
		}else {
			try {
				cell.setCellValue(Double.parseDouble(value .toString()));  
				 
			}catch (Exception e) { 
				cell.setCellValue(value.toString()); 
			} 
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
