package com.fajar.shoppingmart.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.controller.BaseController;
import com.fajar.shoppingmart.dto.PartnerInfo;
import com.fajar.shoppingmart.entity.RegisteredRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreamingService {
	
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private BaseController baseController;
	
	public PartnerInfo getPartnerInfo(String partnerId) throws Exception {
		RegisteredRequest partnerSession = userSessionService.getAvailableSession(partnerId);
		
		if(null == partnerSession) {
			throw new Exception("Invalid request ID");
		}
		PartnerInfo partnerInfo = new PartnerInfo();
		partnerInfo.setActive(partnerSession.isActive());
		
		return partnerInfo;
	}

	public void setActive(HttpServletRequest request) {
		String currentRequestId = baseController.getRegisteredRequestId(request);
		log.info("req id active : {}", currentRequestId);
		userSessionService.setActiveSession(currentRequestId, true);
		
	}

}
