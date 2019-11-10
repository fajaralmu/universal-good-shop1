package com.fajar.controller;

import java.io.IOException;

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
import com.fajar.service.AccountService;
import com.fajar.service.UserSessionService;

@CrossOrigin
@RestController
@RequestMapping("/api/account")
public class RestAccountController {
	Logger log = LoggerFactory.getLogger(RestAccountController.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserSessionService userSessionService;
 
	
	public RestAccountController() {
		log.info("------------------RestAccountController-----------------");
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
		ShopApiResponse response = accountService.login(request, httpRequest);
		return response;
	}
	@PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse logout(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("logout {}", request);
		if (userSessionService.hasSession(httpRequest, false)) {
			accountService.logout(httpRequest);
		}
		 
		return new ShopApiResponse();
	}

}
