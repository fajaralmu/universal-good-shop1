package com.fajar.dto;

public enum ReportCategory{
	CASH_BALANCE("Saldo"),
	SHOP_ITEM("Barang Toko"),
	OPERATIONAL_COST("Biaya Operasional"),
	CAPITAL("Dana");
	
	public final String name;
	
	private ReportCategory(String name) {
		this.name= name;
	}
	
}
