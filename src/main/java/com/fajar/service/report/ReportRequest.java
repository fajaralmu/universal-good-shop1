package com.fajar.service.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fajar.dto.Filter;
import com.fajar.dto.ReportCategory;
import com.fajar.entity.CashBalance;

import lombok.Data;

@Data
public class ReportRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2737716435778655098L;
	private CashBalance initialBalance;
	private Filter filter;
	private List<DailyReportRow> dailyReportRows;
	private Map<ReportCategory, DailyReportRow> dailyReportSummary; 
	private DailyReportRow totalDailyReportRow;
}
