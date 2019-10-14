package com.fajar.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.parameter.Routing;
import com.fajar.service.UserSessionService;

@Controller 
@RequestMapping("account")
public class AccountController {
	Logger log = LoggerFactory.getLogger(AccountController.class);
	@Autowired
	private UserSessionService userSessionService;
	
	public AccountController() {
		log.info("----------------ACCOUNT CONTROLLER---------------");
	}

	@RequestMapping(value = { "/login" })
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request)) {
			response.sendRedirect(Routing.ROOT_ROUTE+"admin/home");
		}
		return "shop/login-page";
	}
	
	@RequestMapping(value = { "/logout" })
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request)) {
			userSessionService.logout(request);
		}
		return "shop/login-page";
	}
	
	@RequestMapping(value = { "/register" })
	public String register(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (userSessionService.hasSession(request)) {
			response.sendRedirect(Routing.ROOT_ROUTE+"admin/home");
		}
		return "shop/register-page";
	}
}
