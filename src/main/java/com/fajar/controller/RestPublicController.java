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
import com.fajar.exception.InvalidRequestException;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.ProductService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/public")
public class RestPublicController {
	
	@Autowired
	private ProductService productService;
	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private UserSessionService userSessionService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse get(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		validatePageRequest(httpRequest);
		log.info("register {}", request);
		ShopApiResponse response = productService.getPublicEntities(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/moresupplier", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse moresupplier(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		validatePageRequest(httpRequest);
		log.info("more supplier {}", request);
		ShopApiResponse response = productService.getMoreProductSupplier(request);
		return response;
	}
	
	@PostMapping(value = "/requestid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse getRequestId(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException { 
		log.info("register {}", request);
		ShopApiResponse response = userSessionService.requestId(httpRequest, httpResponse);
		return response;
	}
	
	public void validatePageRequest(HttpServletRequest req) { 
		boolean validated = userSessionService.validatePageRequest(req );
        if(!validated)  {
        	throw new InvalidRequestException("Invalid page request");
        }
	}
	
	
	 
}
