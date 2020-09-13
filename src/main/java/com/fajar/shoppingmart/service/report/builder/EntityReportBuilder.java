package com.fajar.shoppingmart.service.report.builder;

import java.util.List;

import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.util.ExcelReportUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EntityReportBuilder extends ReportBuilder {
	private List<BaseEntity> entities;
	private EntityProperty entityProperty;
	private final String requestId;

	public EntityReportBuilder(ReportData reportData) {
		super(reportData);
		this.requestId = reportData.getRequestId();
	}

	@Override
	protected void init() {
		entities = reportData.getEntities();
		entityProperty = reportData.getEntityProperty();

	}

	@Override
	public CustomWorkbook buildReport() {

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();
//		 webConfigService.getReportPath() + "/" 
		String reportName = sheetName + "_" + time + "_" + requestId + ".xlsx";
		CustomWorkbook xwb = new CustomWorkbook();
		xsheet = xwb.createSheet(sheetName);
		xwb.setFileName(reportName);
		createEntityTable();

		sendProgress(1, 1, 10);

//		byte[] file = MyFileUtil.getFile(xwb, reportName);
		sendProgress(1, 1, 10);
		return xwb;
	}

	private void createEntityTable() {
		try {
			Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
			ExcelReportUtil.RowCreatedCallback rowCallback = new ExcelReportUtil.RowCreatedCallback() {
				
				@Override
				public void callback(int i, int totalRow) { 
					progressService.sendProgress(1,  totalRow, 60, requestId);
				}
			};
			ExcelReportUtil.createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2,rowCallback, entityValues);

		} catch (Exception e) {
			log.error("Error creating entity excel table");
			e.printStackTrace();
		}
	}

}
