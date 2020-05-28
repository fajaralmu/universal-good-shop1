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
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Message;
import com.fajar.shoppingmart.entity.RegisteredRequest;
import com.fajar.shoppingmart.repository.MessageRepository;
import com.fajar.shoppingmart.util.StringUtil; 

@Service
public class MessagingService {

	@Autowired
	private RealtimeService2 realtimeService;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private UserSessionService userSessionService;
	
	private HashMap<String, List<BaseEntity>> messages = new HashMap<>();
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public List<BaseEntity> getMessages(String requestId){
		
		return messages.get(requestId);
	}
	
	public WebResponse getMessages( HttpServletRequest httpRequest) { 
		 
		String reqId = httpRequest.getHeader("requestId"); 
		WebResponse response = WebResponse.builder().code(reqId).entities(messages.get(reqId)).build();
		realtimeService.sendMessageChat(response);
		return response;
	}
	 
	public WebResponse sendMessage(WebRequest request, HttpServletRequest httpRequest) { 
		String content= request.getValue();
		String reqId = httpRequest.getHeader("requestId");
		
		RegisteredRequest registeredRequest = userSessionService.getRegisteredRequest(reqId);
		
		Message message = new Message(reqId, content, new Date(), Long.valueOf(StringUtil.generateRandomNumber(3)), reqId);
		message.setAlias(request.getUsername() == null? "":request.getUsername());
		message.setUserAgent(registeredRequest.getUserAgent());
		message.setIpAddress(registeredRequest.getIpAddress());
		putMessage(reqId, message);
		
		WebResponse response = WebResponse.builder().code(reqId).entities(messages.get(reqId)).build();
		realtimeService.sendMessageChat(response);
		return response;
	}
	
	public WebResponse replyMessage(WebRequest request, HttpServletRequest httpRequest) { 
		String content= request.getValue(); 
		
		RegisteredRequest registeredRequest = userSessionService.getRegisteredRequest(request.getDestination());
		
		Message message = new Message("ADMIN", content, new Date(), Long.valueOf(StringUtil.generateRandomNumber(3)), request.getDestination());
		message.setAdmin(1);
		putMessage(request.getDestination(), message);
		message.setUserAgent(registeredRequest.getUserAgent());
		message.setIpAddress(registeredRequest.getIpAddress());
		
		WebResponse response = WebResponse.builder().code(request.getDestination()).entities(messages.get(request.getDestination())).build();
		realtimeService.sendMessageChat(response);
		WebResponse responseAPI = new WebResponse();
		BeanUtils.copyProperties(response, responseAPI);
		responseAPI.setCode("00");
		return responseAPI;
	}
	
	void putMessage(String requestId, Message message) {
		String messageUsername = message.getAlias();
		if(messages.get(requestId) == null)
			messages.put(requestId, new ArrayList<>());
		
		List<BaseEntity> currentMessages = messages.get(requestId);
		for (BaseEntity baseEntity : currentMessages) {
			if(((Message) baseEntity).getAdmin() == 0 && message.getAdmin() == 0)
				((Message) baseEntity).setAlias(messageUsername);
		}
		currentMessages.add(message);
		messages.put(requestId,currentMessages);
		messageRepository.save(message);
	}

}
