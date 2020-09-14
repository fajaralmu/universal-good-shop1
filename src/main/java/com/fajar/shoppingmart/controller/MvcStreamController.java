package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.dto.PartnerInfo;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.StreamingService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("stream")
public class MvcStreamController extends BaseController { 
	
	@Autowired
	private StreamingService streamingService;

	public MvcStreamController() {
		log.info("-----------------Mvc Stram Controller------------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/videocall/{partnerId}" })
	public String menuDashboard(Model model, @PathVariable String partnerId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException { 
		
		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		PartnerInfo partnerInfo;
		try {
			partnerInfo = streamingService.getPartnerInfo(partnerId); 
			
			streamingService.setActive(request); 
			model.addAttribute("partnerId", partnerId);
			model.addAttribute("title", "Video Call");
			model.addAttribute("partnerInfo", partnerInfo);
			model.addAttribute("pageUrl", "webpage/video-call");
		} catch (Exception e) {
			sendRedirectLogin(request, response);
			 
		}
		return basePage;
	} 

}
