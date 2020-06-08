package com.fajar.shoppingmart.dto;

public enum ReportCategory{
	
	//Changing the order of the declaration will affect report label order
	CASH_BALANCE("Saldo", "10001"),
	SHOP_ITEM("Barang Toko", "10002"),
	OPERATIONAL_COST("Biaya Operasional", "10003"),
	CAPITAL("Dana", "10004");
	
	public final String name;
	public final String code;
	
	private ReportCategory(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	public boolean codeStartsWith(String val) {
		return code.startsWith(val);
	}
	
}
