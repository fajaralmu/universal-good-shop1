package com.fajar.shoppingmart.dto;

public enum FormInputColumn {

	ONE_COLUMN(1), TWO_COLUMN(2);
	
	public final int value;
	
	private FormInputColumn(int value) {
		this.value = value;
	}
}
