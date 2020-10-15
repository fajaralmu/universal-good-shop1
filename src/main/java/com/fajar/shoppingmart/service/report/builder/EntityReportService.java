package com.fajar.shoppingmart.service.report.builder;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.service.ProgressService;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.util.EntityUtil;
import com.fajar.shoppingmart.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {
 
	@Autowired
	private ProgressService progressService;
	

	public CustomWorkbook getEntityReport(List<BaseEntity> entities, Class<? extends BaseEntity> entityClass,
			HttpServletRequest httpRequest) throws Exception {
		log.info("Generate entity report: {}", entityClass); 
		User currentUser = SessionUtil.getUserFromRequest(httpRequest); 
		String requestId = currentUser.getRequestId();
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityClass, null);
		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty).requestId(requestId).build(); 
	
		EntityReportBuilder reportBuilder = new EntityReportBuilder( reportData);
		reportBuilder.setProgressService(progressService);
		
		progressService.sendProgress(1, 1, 10, false, httpRequest);

		CustomWorkbook file = reportBuilder.buildReport(); 
		
		log.info("Entity Report generated");

		return file;
	}

}
