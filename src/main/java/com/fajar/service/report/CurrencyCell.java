package com.fajar.service.report;

import lombok.Data;

@Data
public class CurrencyCell extends CustomCell { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1287888395717453220L;

	public CurrencyCell(long value) {
		super();
		this.value = value;
	}
}
