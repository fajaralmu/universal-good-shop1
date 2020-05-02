package com.fajar.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.service.EntityService;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.UserAccountService;

@CrossOrigin
@RestController
@RequestMapping("/api/entity")
public class RestEntityController {
	Logger log = LoggerFactory.getLogger(RestEntityController.class);
	@Autowired
	private UserAccountService accountService;
	@Autowired
	private EntityService entityService;
	 

	public RestEntityController() {
		log.info("------------------RestEntityController-----------------");
	}
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse add(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("add entity {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		return entityService.saveEntity(request,httpRequest, true); 
	}
	
	@PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse update(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("register update {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		return entityService.saveEntity(request,httpRequest, false);
		 
	}
	
	@PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse get(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("get entity {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		return entityService.filter(request );
		 
	}
	
	@PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse delete(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("delete entity {}", request);
		if(!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		return entityService.delete(request ); 
	}

}
