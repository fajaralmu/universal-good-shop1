package com.fajar.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("account")
public class MvcAccountController extends BaseController { 
	
	private final UserSessionService userSessionService;
	private final WebConfigService webAppConfiguration;

	private String basePage;

	@Autowired
	public MvcAccountController(UserSessionService userSessionService, WebConfigService webAppConfiguration) {
		super();
		this.userSessionService = userSessionService;
		this.webAppConfiguration = webAppConfiguration;
		log.info("----------------Mvc Account Controller---------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/login" })
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request, false)) {
			response.sendRedirect(request.getContextPath() + "/admin/home");
		}
		model.addAttribute("pageUrl", "shop/login-page");
		model.addAttribute("title", "Login");
		model.addAttribute("page", "login");
		return basePage;
	}

	@RequestMapping(value = { "/logout" })
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

}
