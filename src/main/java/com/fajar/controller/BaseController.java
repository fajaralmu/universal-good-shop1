package com.fajar.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fajar.entity.ShopProfile;
import com.fajar.entity.User;
import com.fajar.service.AccountService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;
import com.fajar.util.MVCUtil;
@Controller
public class BaseController {
	
	@Autowired
	private WebConfigService webAppConfiguration;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private AccountService accountService;

	@ModelAttribute("shopProfile")
	public ShopProfile getProfile(HttpServletRequest request) {
//		System.out.println("Has Session: "+userSessionService.hasSession(request, false));
		return webAppConfiguration.getProfile();
	}
	
	@ModelAttribute("loggedUser")
	public User getLoggedUser(HttpServletRequest request) {
		if(userSessionService.hasSession(request, false)) {
			return userSessionService.getUser(request);
		}
		else return null;
	} 
	
	@ModelAttribute("host")
	public String getHost(HttpServletRequest request) {
		return MVCUtil.getHost(request);
	}
	
	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
	
	@ModelAttribute("imagePath")
	public String getUploadedImagePath(HttpServletRequest request) {
		return webAppConfiguration.getUploadedImagePath();
	}
	
	@ModelAttribute("pageToken")
	public String pageToken(HttpServletRequest request) {
		  return accountService.getToken(request);
	}
}
