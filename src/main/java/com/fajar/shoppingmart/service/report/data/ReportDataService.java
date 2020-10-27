package com.fajar.shoppingmart.service.report.data;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.entity.EntityService;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportDataService {
	 
	@Autowired
	private EntityService entityService;  
	@Autowired
	private DataResources dataResources; 
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public ReportData getDailyReportData(WebRequest request, HttpServletRequest httpRequest) { 
		 
			
		DailyAndMonthlyReportData dailyReportData = new DailyAndMonthlyReportData(dataResources);
		ReportData reportData = dailyReportData.getDailyReportData(request, httpRequest);
		
		return reportData;
		 
	}  
	
	public ReportData getEntityReportData(WebRequest request) throws Exception { 
//		request.getFilter().setLimit(0);
		WebResponse response = entityService.filter(request, null);
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(response.getEntityClass(), null);
		
		return ReportData.builder().entities(response.getEntities()).entityProperty(entityProperty).build();
	}

	public ReportData getMonthlyReportData(WebRequest request) {
		DailyAndMonthlyReportData dailyReportData = new DailyAndMonthlyReportData(dataResources);
		ReportData reportData = dailyReportData.getMonthlyReportData(request);
		
		return reportData;
	}
 
	
}
