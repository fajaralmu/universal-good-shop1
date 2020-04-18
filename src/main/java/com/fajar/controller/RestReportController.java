package com.fajar.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.service.ExcelReportService;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.UserSessionService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/report")
@Slf4j
public class RestReportController {
	
	
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private ExcelReportService excelReportService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/daily", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse daily(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("supply {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		  
		return excelReportService.buildDailyReport(request) ;
	}
	
	@PostMapping(value = "/monthly", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse monthly(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("supply {}", request);
		if(!userSessionService.hasSession(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		 
		ShopApiResponse response = new ShopApiResponse();
		return response ;
	}
	
	 

}
