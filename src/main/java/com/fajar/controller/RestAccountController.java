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
import com.fajar.service.LogProxyFactory;
import com.fajar.service.UserAccountService;
import com.fajar.service.UserSessionService;

@CrossOrigin
@RestController
@RequestMapping("/api/account")
public class RestAccountController {
	Logger log = LoggerFactory.getLogger(RestAccountController.class);
	@Autowired
	private UserAccountService accountService;
	@Autowired
	private UserSessionService userSessionService;
 
	
	public RestAccountController() {
		log.info("------------------RestAccountController-----------------");
	}
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value =  "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse register(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("register {}", request);
		ShopApiResponse response = accountService.registerUser(request);
		return response;
	}
	
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse login(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("login {}", request);
		ShopApiResponse response = accountService.login(request, httpRequest,httpResponse);
		return response;
	}
	@PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse logout(  HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		 
		boolean success = false;
		if (userSessionService.hasSession(httpRequest, false)) {
			success = accountService.logout(httpRequest);
		}
		 
		return ShopApiResponse.builder().code(success?"00":"01").message("SUCCESS LOGOUT: "+success).build();
	}
	@PostMapping(value = "/getprofile", produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse getprpfile(  HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		 
		if (!userSessionService.hasSession(httpRequest, false)) {
			return ShopApiResponse.failedResponse();
		}
		 
		return userSessionService.getProfile(httpRequest);
	}

}
