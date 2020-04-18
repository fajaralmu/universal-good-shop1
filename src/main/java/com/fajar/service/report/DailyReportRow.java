package com.fajar.service.report;

import java.io.Serializable;

import com.fajar.dto.ReportCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportRow implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -6472123129880639922L;
	private String name;
	private long debitAmount;
	private long creditAmount;
//	private long balance;
	private ReportCategory code;
	private int day;
	private int month;
	
	public void addCreditAmount(long amt) {
		this.creditAmount += amt;
	}
	public void addDebitAmount(long amt) {
		this.debitAmount += amt;
	}
	
}