package com.fajar.shoppingmart.dto;

public enum VoucherType  {
	
	PRIMARY("Primary"), SERVICE("Service");
	
	public final String value;
	private VoucherType(String value) {
		this.value = value;
	}

}
