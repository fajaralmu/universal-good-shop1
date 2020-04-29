package com.fajar.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fajar.entity.Page;
import com.fajar.entity.ShopProfile;
import com.fajar.entity.User;
import com.fajar.service.ComponentService;
import com.fajar.service.RegistryService;
import com.fajar.service.UserAccountService;
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
	private UserAccountService accountService;
	@Autowired
	private RegistryService registryService;
	@Autowired
	private ComponentService componentService;

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
			return userSessionService.getUserFromSession(request);
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
		Cookie cookie = getCookie(RegistryService.JSESSSIONID, request.getCookies());
		String cookieValue = cookie == null ? UUID.randomUUID().toString():cookie.getValue();
		return	registryService.addPageRequest(  cookieValue);
		 
	}
	
	@ModelAttribute("pages")
	public List<Page> pages(HttpServletRequest request){
		
		return componentService.getPages();
	}
	
	@ModelAttribute("activePage")
	public String activePage(HttpServletRequest request) {
		return "management";
	}
	
	public static Cookie getCookie(String name, Cookie[] cookies) {
		try {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals(name)) { return cookie; }
			}
		}catch(Exception ex) { ex.printStackTrace(); }
		return null;
	}
	
	public static void sendRedirect(HttpServletResponse response ,String url) throws IOException {
		response.sendRedirect(url);
	}
}
