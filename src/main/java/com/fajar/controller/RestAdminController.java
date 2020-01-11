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
import com.fajar.service.MessagingService;
import com.fajar.service.UserAccountService;
import com.fajar.service.UserSessionService;

@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class RestAdminController {
	Logger log = LoggerFactory.getLogger(RestAdminController.class);
	@Autowired
	private UserAccountService accountService;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private MessagingService messagingService;
	@Autowired
	private RestPublicController restPublicController;

	public RestAdminController() {
		log.info("------------------RestAdminController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/appsessions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse appsessions(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = userSessionService.getAppRequest();
		return response;
	}

	@PostMapping(value = "/deletesession", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse deletesession(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = userSessionService.deleteSession(request);
		return response;
	}

	@PostMapping(value =  "/sendmessage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse sendMessage(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		 restPublicController.validatePageRequest(httpRequest);
		ShopApiResponse response = messagingService.sendMessage(request,httpRequest);
		return response;
	}
	
	@PostMapping(value =  "/replymessage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ShopApiResponse replyMessage(@RequestBody ShopApiRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return ShopApiResponse.failedResponse();
		}
		ShopApiResponse response = messagingService.replyMessage(request,httpRequest);
		return response;
	}


}
