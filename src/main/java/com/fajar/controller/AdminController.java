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
import com.fajar.service.TransactionService;
import com.fajar.service.UserSessionService;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("admin")
public class AdminController {

	Logger log = LoggerFactory.getLogger(AdminController.class);
	@Autowired
	private UserSessionService userService;
	@Autowired
	private TransactionService transactionService;
	
	public AdminController() {
		log.info("-----------------AdminController------------------");
	}

	@RequestMapping(value = { "/home" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
		return "shop/home-page";
	}
	
	@RequestMapping(value = { "/transaction/in" })
	public String incomingTransaction(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
		return "shop/transaction-in-page";
	}
	
	@RequestMapping(value = { "/transaction/out" })
	public String outTransaction(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
	
		return "shop/transaction-out-page";
	}
	
	
 
}
