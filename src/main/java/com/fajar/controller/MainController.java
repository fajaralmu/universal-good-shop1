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
import org.springframework.web.bind.annotation.RequestMethod;

import com.fajar.service.UserSessionService;

@Controller
public class MainController {
	Logger log = LoggerFactory.getLogger(MainController.class);

	
	public MainController() {
		log.info("---------------------------main shop controller------------------------------");
	}

	@RequestMapping(value = { "/", "index" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		return "shop/index";

	}

	@RequestMapping(value = { "time" }, method = RequestMethod.POST)
	public void time(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
//		if (userService.sessionUser(request)) {
//			response.getWriter().write(new Date().toString());
//		}else {
//			response.getWriter().write("Invalid Request");
//		}

	}
}
