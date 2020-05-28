package com.fajar.shoppingmart.service.report.builder;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.shoppingmart.fajar.util.DateUtil;
import com.shoppingmart.fajar.util.ExcelReportUtil;
import com.shoppingmart.fajar.util.MyFileUtil;

import lombok.extern.slf4j.Slf4j;

 @Slf4j
public class EntityReportBuilder { 
	 
	private final WebConfigService webConfigService;
	
	public EntityReportBuilder(WebConfigService webConfigService) {
		this.webConfigService = webConfigService;
	}
	
	public File getEntityReport(ReportData reportData) { 
		List<BaseEntity> entities = reportData.getEntities();
		EntityProperty entityProperty = reportData.getEntityProperty();
		
		log.info("Writing entity report of: {}", entityProperty.getEntityName());
		
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = entityProperty.getEntityName();
		
		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_"+ time+ ".xlsx";
		XSSFWorkbook xwb  = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName ); 
		
		Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
		ExcelReportUtil.createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2, entityValues);
		
		File file = MyFileUtil.getFile(xwb, reportName);
		return file;
	}
	 
}
