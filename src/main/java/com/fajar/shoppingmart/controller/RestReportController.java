package com.fajar.shoppingmart.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.report.builder.CustomWorkbook;
import com.fajar.shoppingmart.service.report.data.ExcelReportService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Controller
@RequestMapping("/api/report")
@Slf4j 
@Authenticated
public class RestReportController {
	
	@Autowired
	private ExcelReportService reportService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public RestReportController() {
		log.info("------------------RestReportController----------------------");
	}
	
	@PostMapping(value = "/daily", consumes = MediaType.APPLICATION_JSON_VALUE )
	@CustomRequestInfo(withRealtimeProgress = true)
	public void daily(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("daily report {}", request); 
		  
		CustomWorkbook result = reportService.buildDailyReport(request, httpRequest) ;

		writeXSSFWorkbook(httpResponse, result);
	}
	 
	
	@PostMapping(value = "/monthly", consumes = MediaType.APPLICATION_JSON_VALUE )
	@CustomRequestInfo(withRealtimeProgress = true)
	public void monthly(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("monthly report {}", request); 
		
		CustomWorkbook result = reportService.buildMonthlyReport(request, httpRequest);
		
		writeXSSFWorkbook(httpResponse, result);
 
	}
	@PostMapping(value = "/entity", consumes = MediaType.APPLICATION_JSON_VALUE )
	@CustomRequestInfo(withRealtimeProgress = true)
	public void entityreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("entityreport {}", request); 
		
		CustomWorkbook result = reportService.generateEntityReport(request, httpRequest);
		
		writeXSSFWorkbook(httpResponse, result);
	}
	@PostMapping(value = "/balance1", consumes = MediaType.APPLICATION_JSON_VALUE )
	@CustomRequestInfo(withRealtimeProgress = true)
	public void balanceWorkSheet(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("balance work sheet {}", request); 
		
		CustomWorkbook result = reportService.buildBalanceReport(request, httpRequest);
		
		writeXSSFWorkbook(httpResponse, result );
	}
	 
	///////////////////Writer//////////////////
	
	
	public static void writeXSSFWorkbook(HttpServletResponse httpResponse, CustomWorkbook xwb ) throws Exception{
		httpResponse.setContentType("text/xls");
		httpResponse.setHeader("Content-disposition", "attachment;filename=" + xwb.getFileName());

		try (OutputStream outputStream = httpResponse.getOutputStream()) {
			xwb.write(outputStream);
		}
	}
	public static void writeFileReponse(HttpServletResponse httpResponse, File file) throws  Exception {
		httpResponse.setHeader("Content-disposition","attachment; filename="+file.getName());
		FileInputStream in = new FileInputStream(file);
		OutputStream out = httpResponse.getOutputStream();

		byte[] buffer= new byte[8192]; // use bigger if you want
		int length = 0;

		while ((length = in.read(buffer)) > 0){
		     out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}

}
