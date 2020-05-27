package com.fajar.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CashType {
	
	GENERAL_COST, CAPITAL, PURCHASING, SELLING, UNDEFINED;

	@JsonCreator
	public static CashType forValue(String value) {
		CashType[] enumKeys = CashType.values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].toString().equals(value)) {
				return enumKeys[i];
			}
		}

		return null;
	}

	@JsonValue
	public String toValue() {
		CashType[] enumKeys = CashType.values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].equals(this)) {
				return enumKeys[i].toString();
			}
		}

		return null; // or fail
	}
}
