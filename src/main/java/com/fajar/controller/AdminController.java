package com.fajar.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.parameter.EntityParameter;
import com.fajar.parameter.Routing;
import com.fajar.service.RealtimeService;
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
	public AdminController() {
		log.info("-----------------AdminController------------------");
	}

	@RequestMapping(value = { "/home" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(Routing.ROOT_ROUTE+"account/login");
		}
		return "shop/home-page";
	}
	
	
 
}
