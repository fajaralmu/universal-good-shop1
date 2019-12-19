package com.fajar.controller;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fajar.entity.ShopProfile;
import com.fajar.entity.User;
import com.fajar.service.AccountService;
import com.fajar.service.RegistryService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;
import com.fajar.util.DateUtil;
import com.fajar.util.MvcUtil;
@Controller
public class BaseController {
	
	@Autowired
	private WebConfigService webAppConfiguration;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private RegistryService registryService;

	@ModelAttribute("shopProfile")
	public ShopProfile getProfile(HttpServletRequest request) {
//		System.out.println("Has Session: "+userSessionService.hasSession(request, false));
		return webAppConfiguration.getProfile();
	}
	
	@ModelAttribute("timeGreeting")
	public String timeGreeting(HttpServletRequest request) {
		return DateUtil.getTimeGreeting();
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
		return MvcUtil.getHost(request);
	}
	
	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
	
	@ModelAttribute("fullImagePath")
	public String getFullImagePath(HttpServletRequest request) {
		return getHost(request)+ getContextPath(request)+"/"+getUploadedImagePath(request)+"/";
	}
	
	@ModelAttribute("imagePath")
	public String getUploadedImagePath(HttpServletRequest request) {
		return webAppConfiguration.getUploadedImagePath();
	}
	
	@ModelAttribute("pageToken")
	public String pageToken(HttpServletRequest request) {
		  return accountService.getToken(request);
	}
	
	@ModelAttribute("requestId")
	public String requestId(HttpServletRequest request) {
		
		return	registryService.addPageRequest(  getCookie(RegistryService.JSESSSIONID, request.getCookies()).getValue());
		 
	}
	
	public static Cookie getCookie(String name, Cookie[] cookies) {
		try {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals(name)) { return cookie; }
			}
		}catch(Exception ex) { ex.printStackTrace(); }
		return null;
	}
}
