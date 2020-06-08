package com.fajar.shoppingmart.service.report.data;

import java.io.Serializable;

import com.fajar.shoppingmart.dto.ReportCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRowData implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -6472123129880639922L;
	private String name;
	private long debitAmount;
	private long creditAmount;
//	private long balance;
	private ReportCategory category;
	private int day;
	private int month;
	
	public void addCreditAmount(long amt) {
		this.creditAmount += amt;
	}
	public void addDebitAmount(long amt) {
		this.debitAmount += amt;
	}
	public ReportRowData(ReportCategory reportCategory) {
		// TODO Auto-generated constructor stub
		this.category  = reportCategory;
	}
	
}