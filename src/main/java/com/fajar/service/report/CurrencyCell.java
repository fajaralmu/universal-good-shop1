package com.fajar.service.report;

import lombok.Data;

@Data
public class CurrencyCell extends CustomCell {
	private final Long value;
	public CurrencyCell(long value) {
		super();
		this.value = value;
	}
}
