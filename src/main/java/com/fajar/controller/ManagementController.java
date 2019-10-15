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
public class ManagementController {

	Logger log = LoggerFactory.getLogger(ManagementController.class);
	@Autowired
	private UserSessionService userService;
	@Autowired
	private EntityService entityService;

	public ManagementController() {
		log.info("-----------------AdminController------------------");
	}

	@RequestMapping(value = { "/unit" })
	public String unit(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(Routing.ROOT_ROUTE + "account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Unit", null);
		model.addAttribute("entityProperty", entityProperty);
		return "shop/entity-management-page";
	}

	@RequestMapping(value = { "/supplier" })
	public String supplier(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(Routing.ROOT_ROUTE + "account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Supplier", null);
		model.addAttribute("entityProperty", entityProperty);
		return "shop/entity-management-page";
	}
	
	@RequestMapping(value = { "/customer" })
	public String customer(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(Routing.ROOT_ROUTE + "account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Customer", null);
		model.addAttribute("entityProperty", entityProperty);
		return "shop/entity-management-page";
	}
	
	@RequestMapping(value = { "/product" })
	public String product(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(Routing.ROOT_ROUTE + "account/login");
		}
		HashMap<String, Object> listObject= new HashMap<>();
		List<Unit> units =entityService.getAllUnit();
		listObject.put("unit", units);
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Product", listObject);
		model.addAttribute("entityProperty", entityProperty);
		return "shop/entity-management-page";
	}


}
