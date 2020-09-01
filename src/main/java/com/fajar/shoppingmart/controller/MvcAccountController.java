package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.MenuInitiationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("account")
public class MvcAccountController extends BaseController { 
	 
	@Autowired
	private MenuInitiationService menuInitiationService;

	@Autowired
	public MvcAccountController() { 
		log.info("----------------Mvc Account Controller---------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/login" })
	@CustomRequestInfo(title="Login", pageUrl = "shop/login-page", stylePaths = "loginpage")
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request, false)) {
			response.sendRedirect(request.getContextPath() + "/admin/home");
		}
		
		setActivePage(request );

		model.addAttribute("page", "login");
		return basePage;
	}

	@RequestMapping(value = { "/logout" })
	@Authenticated
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request, false)) {
			userSessionService.logout(request);
		}

		model.addAttribute("pageUrl", "shop/login-page");
		model.addAttribute("page", "login");
		return basePage;
	}

	@RequestMapping(value = { "/register" })
	public String register(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/admin/home");
		}
		return "shop/register-page";
	}
	
	@RequestMapping(value = { "/websetting" })
	@Authenticated
	public void webSetting(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		String parameter = request.getParameter("action");
		log.info("parameter: {}", parameter);
		if (null != parameter && parameter.equals("resetmenu")) {
			menuInitiationService.resetMenus();
		}
		response.setStatus(302);
		response.setHeader("location", request.getContextPath() + "/admin/home");

	}

}
