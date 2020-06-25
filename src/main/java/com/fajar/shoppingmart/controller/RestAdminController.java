package com.fajar.shoppingmart.controller;

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

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.MessagingService;

@CrossOrigin
@RestController
@Authenticated
@RequestMapping("/api/admin")
public class RestAdminController extends BaseController {
	Logger log = LoggerFactory.getLogger(RestAdminController.class);
	 
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
	public WebResponse appsessions(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return WebResponse.failedResponse();
		}
		WebResponse response = userSessionService.getAvailableSessions();
		return response;
	}

	@PostMapping(value = "/deletesession", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse deletesession(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return WebResponse.failedResponse();
		}
		WebResponse response = userSessionService.deleteSession(request);
		return response;
	}

	@PostMapping(value =  "/sendmessage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse sendMessage(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		 restPublicController.validatePageRequest(httpRequest);
		WebResponse response = messagingService.sendMessage(request,httpRequest);
		return response;
	}
	
	@PostMapping(value =  "/getmessages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getmessages(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		 restPublicController.validatePageRequest(httpRequest);
		WebResponse response = messagingService.getMessages(httpRequest);
		return response;
	}
	
	@PostMapping(value =  "/replymessage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse replyMessage(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return WebResponse.failedResponse();
		}
		WebResponse response = messagingService.replyMessage(request,httpRequest);
		return response;
	}
	
	@PostMapping(value =  "/clearsession", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearsessions(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return WebResponse.failedResponse();
		}
		WebResponse response = userSessionService.clearSessions();
		return response;
	}
	
	@PostMapping(value =  "/savepagesequence", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse savePageSequence(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return WebResponse.failedResponse();
		}
		WebResponse response = componentService.savePageSequence(request);
		return response;
	}


}
