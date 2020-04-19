package com.fajar.service.report;

import lombok.Data;

@Data
public class CurrencyCell extends CustomCell { 
	public CurrencyCell(long value) {
		super();
		this.value = value;
	}
}
