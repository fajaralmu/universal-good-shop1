package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.exception.InvalidRequestException;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/public")
@Authenticated(loginRequired = false)
public class RestPublicController extends BaseController{
	
	@Autowired
	private ProductService productService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public RestPublicController() {
		log.info("----------------------Rest Public Controller-------------------");
	}
	
	@PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse get(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		validatePageRequest(httpRequest);
		log.info("register {}", request);
		WebResponse response = productService.getPublicEntities(request, httpRequest.getHeader("requestId"));
		return response;
	}
	
	@PostMapping(value = "/moresupplier", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse moresupplier(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		validatePageRequest(httpRequest);
		log.info("more supplier {}", request);
		WebResponse response = productService.getMoreProductSupplier(request);
		return response;
	}
	
	@PostMapping(value = "/requestid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getRequestId(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException { 
		log.info("register {}", request);
		WebResponse response = userSessionService.requestId(httpRequest, httpResponse);
		return response;
	}
	
	@PostMapping(value = "/pagecode")
	public WebResponse getCurrentPageCode(HttpServletRequest request, HttpServletResponse response) {
		validatePageRequest(request);
		return WebResponse.builder().code(super.activePage(request)).build();
	}
	@PostMapping(value = "/menus/{pageCode}")
	public WebResponse getMenusByPage(@PathVariable(value = "pageCode") String pageCode, HttpServletRequest request, HttpServletResponse response) {
		validatePageRequest(request);
		return componentService.getMenuByPageCode(pageCode);
	}
	
	public void validatePageRequest(HttpServletRequest req) { 
		boolean validated = userSessionService.validatePageRequest(req );
        if(!validated)  {
        	throw new InvalidRequestException("Invalid page request");
        }
	}
	
	
	 
}
