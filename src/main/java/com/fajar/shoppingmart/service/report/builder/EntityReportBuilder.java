package com.fajar.shoppingmart.service.report.builder;

import java.io.File;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.util.ExcelReportUtil;
import com.fajar.shoppingmart.util.MyFileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityReportBuilder extends ReportBuilder {
	private List<BaseEntity> entities;
	private EntityProperty entityProperty;

	public EntityReportBuilder(WebConfigService webConfigService) {
		super(webConfigService);
	}

	@Override
	public File buildReport(ReportData reportData) {
		entities = reportData.getEntities();
		entityProperty = reportData.getEntityProperty();

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName);

		createEntityTable();

		File file = MyFileUtil.getFile(xwb, reportName);
		return file;
	}

	private void createEntityTable() {
		try {
			Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
			ExcelReportUtil.createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2, entityValues);

		} catch (Exception e) {
			log.error("Error creating entity excel table");
			e.printStackTrace();
		}
	}

}
