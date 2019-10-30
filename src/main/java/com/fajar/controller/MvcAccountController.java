package com.fajar.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entity.ShopProfile;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebAppConfiguration;

@Controller
@RequestMapping("account")
public class MvcAccountController extends BaseController{
	Logger log = LoggerFactory.getLogger(MvcAccountController.class);
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private WebAppConfiguration webAppConfiguration;

	private String basePage;

	public MvcAccountController() {

		log.info("----------------MvcAccountController---------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/login" })
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/admin/home");
		}
		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("pageUrl", "shop/login-page");
		model.addAttribute("page", "login"); 
		return basePage;
	}

	@RequestMapping(value = { "/logout" })
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request)) {
			userSessionService.logout(request);
		}

		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("pageUrl", "shop/login-page");
		model.addAttribute("page", "login"); 
		return basePage;
	}

	@RequestMapping(value = { "/register" })
	public String register(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/admin/home");
		}

		model.addAttribute("contextPath", request.getContextPath());
		return "shop/register-page";
	}

	
}
