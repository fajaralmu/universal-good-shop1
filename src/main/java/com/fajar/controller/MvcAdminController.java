package com.fajar.controller;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.service.ComponentService;
import com.fajar.service.TransactionService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;
import com.fajar.util.MVCUtil;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("admin")
public class MvcAdminController extends BaseController {

	Logger log = LoggerFactory.getLogger(MvcAdminController.class);
	@Autowired
	private UserSessionService userService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private WebConfigService webAppConfiguration;

	private String basePage;

	public MvcAdminController() {
		log.info("-----------------MvcAdminController------------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/home" })
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		Calendar cal = Calendar.getInstance();
		model.addAttribute("menus", componentService.getDashboardMenus(request)); 
		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
		model.addAttribute("title", "Shop::Dashboard");
		model.addAttribute("pageUrl", "shop/home-page");
		model.addAttribute("page", "dashboard");
		model.addAttribute("currentMonth", cal.get(Calendar.MONTH) + 1);
		model.addAttribute("currentYear", cal.get(Calendar.YEAR));
		Integer[] transactionYears = transactionService.getMinAndMaxTransactionYear();
		System.out.println("========TRX YEARS: " + transactionYears);
		model.addAttribute("minYear", transactionYears[0]);
		model.addAttribute("maxYear", transactionYears[1]);
		return basePage;
	}

	@RequestMapping(value = { "/management" })
	public String menuManagement(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		model.addAttribute("menus", componentService.getManagementMenus(request)); 
		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("title", "Shop::Management");
		model.addAttribute("pageUrl", "shop/management-page");
		model.addAttribute("page", "management");
		return basePage;
	}

	@RequestMapping(value = { "/transaction" })
	public String transaction(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		model.addAttribute("menus", componentService.getTransactionMenus(request)); 
		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
		model.addAttribute("title", "Shop::Transaction");
		model.addAttribute("pageUrl", "shop/transaction-page");
		model.addAttribute("page", "transaction");
		return basePage;
	}

	@RequestMapping(value = { "/transaction/in","/transaction/in/","/transaction/in/{transactionCode}" })
	public String incomingTransaction(@PathVariable(required = false) String transactionCode, Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		if(null!=transactionCode) {
			model.addAttribute("requestCode", transactionCode);
		}
		model.addAttribute("title", "Shop::Supply");
		model.addAttribute("pageUrl", "shop/transaction-in-page");
		model.addAttribute("page", "transaction");
		return basePage;
	}

	@RequestMapping(value = { "/transaction/out","/transaction/out/","/transaction/out/{transactionCode}"  })
	public String outTransaction(@PathVariable(required = false) String transactionCode, Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		if(null!=transactionCode) {
			model.addAttribute("requestCode", transactionCode);
		}
		model.addAttribute("title", "Shop::Purchase");
		model.addAttribute("pageUrl", "shop/transaction-out-page");
		model.addAttribute("page", "transaction");
		return basePage;
	}

}
