package com.fajar.service.report;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entity.BaseEntity;
import com.fajar.entity.setting.EntityProperty;
import com.fajar.service.WebConfigService;
import com.fajar.util.DateUtil;
import com.fajar.util.ExcelReportUtil;
import com.fajar.util.MyFileUtil;

@Service
public class EntityReportBuilder {

	@Autowired
	private WebConfigService webConfigService;
	
	public File getEntityReport(List<BaseEntity> entities, EntityProperty entityProperty) { 
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
