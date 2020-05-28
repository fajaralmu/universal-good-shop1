package com.fajar.shoppingmart.service.report.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.entity.setting.EntityProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportData implements Serializable {

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
	
	/**
	 * entities
	 */
	private Class entityClass;
	private List<BaseEntity> entities;
	private EntityProperty entityProperty;
	
}
