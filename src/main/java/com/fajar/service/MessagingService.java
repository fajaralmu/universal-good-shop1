package com.fajar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import com.fajar.dto.Message;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.util.StringUtil;
import com.sun.xml.internal.ws.developer.Serialization;

@Service
public class MessagingService {

	@Autowired
	private RealtimeService2 realtimeService;
	
	private HashMap<String, List<BaseEntity>> messages = new HashMap<>();
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public List<BaseEntity> getMessages(String requestId){
		
		return messages.get(requestId);
	}
	
	public ShopApiResponse sendMessage(ShopApiRequest request, HttpServletRequest httpRequest) { 
		String content= request.getValue();
		String reqId = httpRequest.getHeader("requestId");
		
		Message message = new Message(reqId, content, new Date(), Long.valueOf(StringUtil.generateRandomNumber(3)), reqId);
		putMessage(reqId, message);
		
		ShopApiResponse response = ShopApiResponse.builder().code(reqId).entities(messages.get(reqId)).build();
		realtimeService.sendMessageChat(response);
		return response;
	}
	
	public ShopApiResponse replyMessage(ShopApiRequest request, HttpServletRequest httpRequest) { 
		String content= request.getValue(); 
		
		Message message = new Message("ADMIN", content, new Date(), Long.valueOf(StringUtil.generateRandomNumber(3)), request.getDestination());
		message.setAdmin(1);
		putMessage(request.getDestination(), message);
		
		ShopApiResponse response = ShopApiResponse.builder().code(request.getDestination()).entities(messages.get(request.getDestination())).build();
		realtimeService.sendMessageChat(response);
		ShopApiResponse responseAPI = new ShopApiResponse();
		BeanUtils.copyProperties(response, responseAPI);
		responseAPI.setCode("00");
		return responseAPI;
	}
	
	void putMessage(String requestId, Message message) {
		if(messages.get(requestId) == null)
			messages.put(requestId, new ArrayList<>());
		messages.get(requestId).add(message);
	}

}
