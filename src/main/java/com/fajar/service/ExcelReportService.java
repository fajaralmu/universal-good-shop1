package com.fajar.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.dto.TransactionType;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.CashBalance;
import com.fajar.entity.CostFlow;
import com.fajar.entity.ProductFlow;
import com.fajar.repository.CapitalFlowRepository;
import com.fajar.repository.CostFlowRepository;
import com.fajar.repository.ProductFlowRepository;
import com.fajar.util.DateUtil;

@Service
public class ExcelReportService {
	
	@Autowired
	private CostFlowRepository costFlowRepository;
	@Autowired
	private CapitalFlowRepository capitalFlowRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private ProductFlowRepository productFlowRepository;
	
	private Map<Integer, List<BaseEntity>> dailyTransactions = new HashMap<>();
	

	public ShopApiResponse buildDailyReport(ShopApiRequest request) { 
		
		Filter filter = request.getFilter();
		CashBalance cashBalance = getBalance(filter);
		int month = filter.getMonth();
		int year = filter.getMonth() ;
		
		Integer[] months = DateUtil.getMonths(year);
		Integer dayCount = months[month - 1]; 
		List<BaseEntity> transactions = getTransactions(month, year);
		
		for (int i = 1; i <= dayCount; i++) {
			
		}
		
		return ShopApiResponse.success();
	} 
	 
	private void putTransaction(int day, BaseEntity baseEntity) {
		if(dailyTransactions.get(day) == null) {
			dailyTransactions.put(day, new ArrayList<>());
		}
		dailyTransactions.get(day).add(baseEntity);
	}

	private List<BaseEntity> getTransactions(int month, int year) {
		dailyTransactions.clear();
		
		List<ProductFlow> transactionItems = productFlowRepository.findByTransactionPeriod(  month, year); 
		List<CapitalFlow> capitalFlows = capitalFlowRepository.findByPeriod(month, year);
		List<CostFlow> costFlows = costFlowRepository.findByPeriod(month, year);
		 
		
		List<BaseEntity> results = new ArrayList<BaseEntity>();
		
		results.addAll(costFlows);
		results.addAll(transactionItems);
		results.addAll(capitalFlows);
		
		for (BaseEntity baseEntity : results) {
			
			Date createdDate = baseEntity.getCreatedDate();
			Calendar calendar = DateUtil.cal(createdDate);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			putTransaction(day, baseEntity);
		}
		
		return results ;
	}




	private CashBalance getBalance(Filter filter) {
		int month = filter.getMonth() - 1 < 0 ? 12 : filter.getMonth() - 1;
		int year = filter.getMonth() - 1 < 0 ? filter.getYear() - 1 : filter.getYear();
		CashBalance cashBalance = cashBalanceService.getBalanceAtTheEndOf(month, year);		
		return cashBalance;
	}
	
	static enum ReportCategory{
		CASH_BALANCE,
		SHOP_ITEM,
		OPERATIONAL_COST,
		CAPITAL
		
		
	}

}
