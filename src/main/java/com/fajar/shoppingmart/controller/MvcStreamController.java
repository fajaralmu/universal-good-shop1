package com.fajar.shoppingmart.controller;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.DateUtil;

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
		
		setActivePage(request );
		model.addAttribute("partnerId", partnerId);
		model.addAttribute("title", "Video Call");
		model.addAttribute("pageUrl", "shop/video-call");
		
		return basePage;
	} 

}
