package com.fajar.shoppingmart.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Message;
import com.fajar.shoppingmart.entity.RegisteredRequest;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.repository.MessageRepository;
import com.fajar.shoppingmart.service.runtime.MessagesRuntimeRepository;
import com.fajar.shoppingmart.service.sessions.RegisteredRequestService;
import com.fajar.shoppingmart.util.StringUtil;

@Service
public class MessagingService {

	@Autowired
	private RealtimeService2 realtimeService;

//	@Autowired
//	private MessageRepository messageRepository;

	@Autowired
	private RegisteredRequestService registeredRequestService;

	@Autowired
	private EntityRepository entityRepository;
 
	@Autowired
	private MessageRepository messageRepository;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public List<Message> getMessages(String requestId) {

		return messageRepository.findByRequestId(requestId);
	}

	public WebResponse getMessages(HttpServletRequest httpRequest) {

		String requestId = httpRequest.getHeader("requestId");
		WebResponse response = WebResponse.builder().code(requestId).build();
		response.setEntities(getMessages(requestId));
		realtimeService.sendMessageChat(response);
		return response;
	}

	public WebResponse sendMessage(WebRequest request, HttpServletRequest httpRequest) {
		String content = request.getValue();
		String requestId = httpRequest.getHeader("requestId");

		RegisteredRequest registeredRequest = registeredRequestService.getRegisteredRequest(requestId);

		Message message = new Message(requestId, content, new Date(), requestId);
		message.setAlias(request.getUsername() == null ? "" : request.getUsername());
		message.setUserAgent(registeredRequest.getUserAgent());
		message.setIpAddress(registeredRequest.getIpAddress());
		putMessage(requestId, message);

		WebResponse response = WebResponse.builder().code(requestId).build();
		response.setEntities(getMessages(requestId));
		realtimeService.sendMessageChat(response);
		return response;
	}

	public WebResponse replyMessage(WebRequest request, HttpServletRequest httpRequest) {
		String content = request.getValue();

		RegisteredRequest registeredRequest = registeredRequestService.getRegisteredRequest(request.getDestination());

		Message message = new Message("ADMIN", content, new Date() , request.getDestination());
		message.setAdmin(1);
		putMessage(request.getDestination(), message);
		message.setUserAgent(registeredRequest.getUserAgent());
		message.setIpAddress(registeredRequest.getIpAddress());

		WebResponse response = WebResponse.builder().code(request.getDestination()).build();
		response.setEntities(getMessages(request.getDestination()));
		realtimeService.sendMessageChat(response);
		WebResponse responseAPI = new WebResponse();
		BeanUtils.copyProperties(response, responseAPI);
		responseAPI.setCode("00");
		return responseAPI;
	}

	void putMessage(String requestId, Message message) {
		entityRepository.save(message);
	}

}
