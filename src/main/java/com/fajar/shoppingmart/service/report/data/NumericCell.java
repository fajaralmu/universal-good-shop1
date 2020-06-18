package com.fajar.shoppingmart.service.report.data;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class NumericCell extends CustomCell<Number>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3347683601680795033L;
	public NumericCell(int value) {
		super();
		this.value = value;
	}
	public NumericCell(long value) {
		super();
		this.value = value;
	}
	public NumericCell(double value) {
		super();
		this.value = value;
	}
	@Override
	public void setValue(XSSFCell cell) {
		try {
			cell.setCellValue(Double.parseDouble(value.toString()));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
