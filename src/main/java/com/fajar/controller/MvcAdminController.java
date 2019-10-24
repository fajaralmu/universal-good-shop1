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
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.service.ComponentService;
import com.fajar.service.TransactionService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebAppConfiguration;
import com.fajar.util.MVCUtil;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("admin")
public class MvcAdminController {

	Logger log = LoggerFactory.getLogger(MvcAdminController.class);
	@Autowired
	private UserSessionService userService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private WebAppConfiguration webAppConfiguration;
	
	private String basePage;
	
	public MvcAdminController() {
		log.info("-----------------MvcAdminController------------------");
	}
	
	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/home" })
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
		model.addAttribute("menus", componentService.getDashboardMenus(request));
		model.addAttribute("host", MVCUtil.getHost(request));
		model.addAttribute("imagePath",webAppConfiguration.getUploadedImagePath());
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Shop::Dashboard");
		model.addAttribute("pageUrl", "shop/home-page");
		return basePage;
	}
	
	@RequestMapping(value = { "/management" })
	public String menuManagement(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
		model.addAttribute("menus", componentService.getManagementMenus(request));
		model.addAttribute("host", MVCUtil.getHost(request));
		model.addAttribute("imagePath",webAppConfiguration.getUploadedImagePath());
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Shop::Management");
		model.addAttribute("pageUrl", "shop/management-page");
		return basePage;
	}
	
	@RequestMapping(value = { "/transaction" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
		model.addAttribute("menus", componentService.getTransactionMenus(request));
		model.addAttribute("host", MVCUtil.getHost(request));
		model.addAttribute("imagePath",webAppConfiguration.getUploadedImagePath());
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Shop::Transaction");
		model.addAttribute("pageUrl", "shop/transaction-page");
		return basePage;
	}
	
	@RequestMapping(value = { "/transaction/in" })
	public String incomingTransaction(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
		model.addAttribute("contextPath",request.getContextPath()); 
		model.addAttribute("title", "Shop::Supply");
		model.addAttribute("pageUrl", "shop/transaction-in-page");
		return basePage;
	}
	
	@RequestMapping(value = { "/transaction/out" })
	public String outTransaction(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath()+"/account/login");
		}
	
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Shop::Purchase");
		model.addAttribute("pageUrl", "shop/transaction-out-page");
		return basePage;
	}
	
	 
	
 
}
