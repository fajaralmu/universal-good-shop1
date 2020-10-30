package com.fajar.shoppingmart.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.MessagingService;
import com.fajar.shoppingmart.service.transaction.TransactionHistoryService;
import com.fajar.shoppingmart.util.ApplicationUtil;

@CrossOrigin
@RestController 
@RequestMapping("/api/admin")
public class RestAdminController extends BaseController {
	Logger log = LoggerFactory.getLogger(RestAdminController.class);

	@Autowired
	private MessagingService messagingService;
	@Autowired
	private RestPublicController restPublicController;
	@Autowired
	private TransactionHistoryService transactionService;

	public RestAdminController() {
		log.info("------------------RestAdminController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	@Authenticated
	@PostMapping(value = "/appsessions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse appsessions(@RequestBody WebRequest request)  {

		WebResponse response = registeredRequestService.getAvailableSessions();
		return response;
	}
	@Authenticated
	@PostMapping(value = "/deletesession", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse deletesession(@RequestBody WebRequest request) {

		WebResponse response = registeredRequestService.deleteSession(request);
		return response;
	}
	@Authenticated(loginRequired = false)
	@PostMapping(value = "/sendmessage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse sendMessage(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		restPublicController.validatePageRequest(httpRequest);
		WebResponse response = messagingService.sendMessageToAdmin(request, httpRequest);
		return response;
	}
	@Authenticated(loginRequired = false)
	@PostMapping(value = "/getmessages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getMessages(@RequestBody WebRequest request, HttpServletRequest httpRequest){
		restPublicController.validatePageRequest(httpRequest);
		 
		WebResponse response = messagingService.getMessages(httpRequest);
		return response;
	}
	@Authenticated(loginRequired = false)
	@PostMapping(value = "/replymessage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse replyMessage(@RequestBody WebRequest request, HttpServletRequest httpRequest){

		WebResponse response = messagingService.replyMessageToClient(request, httpRequest);
		return response;
	}
	@Authenticated
	@PostMapping(value = "/clearsession", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearsessions(@RequestBody WebRequest request)   {

		WebResponse response = registeredRequestService.clearSessions();
		return response;
	}
	@Authenticated
	@PostMapping(value = "/saveentityorder/{entityName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse savePageSequence(@PathVariable("entityName") String entityName, @RequestBody WebRequest request) {

		WebResponse response = componentService.saveEntitySequence(request, entityName);
		return response;
	}
	@Authenticated
	@PostMapping(value = "/balance", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getBalance(@RequestBody WebRequest request) {
		
		WebResponse response = transactionService.getBalance(request);
		return response;
	}
	@Authenticated
	@PostMapping(value = "/resourceusage",  produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> resourceusage() {
		 
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("memoryInfo", ApplicationUtil.getMemoryInfo());
		return response ;
	}

}
