package com.fajar.controller;

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

import com.fajar.dto.WebRequest;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.report.PrintedReportService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Controller
@RequestMapping("/api/report")
@Slf4j
public class RestReportController {
	
	@Autowired
	private PrintedReportService excelReportService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/daily", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void daily(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("daily report {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		  
		File result = excelReportService.buildDailyReport(request) ;

		writeFileReponse(httpResponse, result);
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
	
	@PostMapping(value = "/monthly", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void monthly(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("monthly report {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		
		File result = excelReportService.buildMonthlyReport(request);
		
		writeFileReponse(httpResponse, result);
	}
	@PostMapping(value = "/entity", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void entityreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("entityreport {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		
		File result = excelReportService.buildEntityReport(request);
		
		writeFileReponse(httpResponse, result);
	}
	
	 

}
