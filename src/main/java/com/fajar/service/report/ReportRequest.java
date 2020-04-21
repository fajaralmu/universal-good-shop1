package com.fajar.service.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.dto.Filter;
import com.fajar.dto.ReportCategory;
import com.fajar.entity.CashBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2737716435778655098L;
	private Filter filter;
	
	/**
	 * daily
	 */
	private CashBalance initialBalance; 
	private List<DailyReportRow> dailyReportRows;
	private Map<ReportCategory, DailyReportRow> dailyReportSummary; 
	private DailyReportRow totalDailyReportRow;
	
	/**
	 * monthly
	 */
	private Map<Integer, Map<ReportCategory, DailyReportRow>> monthyReportContent;
	
}
