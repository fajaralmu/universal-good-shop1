package com.fajar.service.report;

public class NumericCell extends CustomCell{

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
}
