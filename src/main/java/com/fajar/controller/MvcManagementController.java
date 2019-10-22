package com.fajar.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.config.EntityProperty;
import com.fajar.entity.Unit;
import com.fajar.entity.UserRole;
import com.fajar.parameter.Routing;
import com.fajar.service.EntityService;
import com.fajar.service.UserSessionService;
import com.fajar.util.EntityUtil;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("management")
public class MvcManagementController {

	Logger log = LoggerFactory.getLogger(MvcManagementController.class);
	@Autowired
	private UserSessionService userService;
	@Autowired
	private EntityService entityService;

	public MvcManagementController() {
		log.info("-----------------MvcManagementController------------------");
	}

	@RequestMapping(value = { "/unit" })
	public String unit(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Unit", null);
		model.addAttribute("entityProperty", entityProperty);
		model  =constructCommonModel(request, model, "Unit");
		return "BASE_PAGE";
	}

	@RequestMapping(value = { "/supplier" })
	public String supplier(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Supplier", null);
		model.addAttribute("entityProperty", entityProperty);
		model  =constructCommonModel(request, model, "Supplier");
		return "BASE_PAGE";
	}
	
	@RequestMapping(value = { "/customer" })
	public String customer(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Customer", null);
		model.addAttribute("entityProperty", entityProperty);
		model  =constructCommonModel(request, model, "Customer");
		return "BASE_PAGE";
	}
	
	@RequestMapping(value = { "/product" })
	public String product(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		 EntityProperty entityProperty = EntityUtil.createEntityProperty("Product", null);
		model.addAttribute("entityProperty", entityProperty);
		model  =constructCommonModel(request, model, "Product");
		return "BASE_PAGE";
	}
	
	@RequestMapping(value = { "/category" })
	public String category(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		 EntityProperty entityProperty = EntityUtil.createEntityProperty("Category", null);
		model.addAttribute("entityProperty", entityProperty);
		model  =constructCommonModel(request, model, "Category");
		return "BASE_PAGE";
	}
	
	@RequestMapping(value = { "/transaction" })
	public String transaction(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		 EntityProperty entityProperty = EntityUtil.createEntityProperty("Transaction", null);
		model.addAttribute("entityProperty", entityProperty);
		model  =constructCommonModel(request, model, "Transaction");
		model.addAttribute("editable",false);
		return "BASE_PAGE";
	}
	
	@RequestMapping(value = { "/user" })
	public String user(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		HashMap<String, Object> listObject= new HashMap<>();
		List<UserRole> roles =entityService.getAllUserRole();
		listObject.put("userRole", roles);
		EntityProperty entityProperty = EntityUtil.createEntityProperty("User", listObject);
		model.addAttribute("entityProperty", entityProperty);
		log.info("============ENTITY PROPERTY: "+entityProperty);
		model  =constructCommonModel(request, model, "User");
		return "BASE_PAGE";
	}
	
	@RequestMapping(value = { "/menu" })
	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Menu", null);
		model.addAttribute("entityProperty", entityProperty);
		log.info("============ENTITY PROPERTY: "+entityProperty);
		model  =constructCommonModel(request, model, "Menu");
		return "BASE_PAGE";
	}
	
	private Model constructCommonModel(HttpServletRequest request, Model model, String title) {
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Management::"+title);
		model.addAttribute("editable",true);
		model.addAttribute("pageUrl", "shop/entity-management-page");
		return model;
	}


}
