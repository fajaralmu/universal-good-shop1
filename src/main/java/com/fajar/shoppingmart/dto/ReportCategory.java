package com.fajar.shoppingmart.dto;

public enum ReportCategory{
	
	//Changing the order of the declaration will affect report label order
	CASH_BALANCE("Saldo", "20001"),
	SHOP_ITEM("Barang Toko", "40002"),
	OPERATIONAL_COST("Biaya Operasional", "50003"),
	CAPITAL("Dana", "10004");
	
	public final String name;
	public final String code; 
	//code starts with 
	//equals "5" Rugi
	//equals "4" Laba
	//<2 Neraca (Debet)
	//<4 Neraca (Kredit)
	
	private ReportCategory(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	public boolean codeStartsWith(String val) {
		return code.startsWith(val);
	}
	
	public boolean codeLeftCharLessThan(String val) {
		int i = Integer.valueOf(val);
		char codeChar = code.charAt(0);
		int intChar = Integer.valueOf(String.valueOf(codeChar));
		
		return intChar < i;
	}
	
}
