package com.fajar.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MvcCommonController {
	Logger log = LoggerFactory.getLogger(MvcCommonController.class);

	
	public MvcCommonController() {
		log.info("---------------------------MvcCommonController------------------------------");
	}

	@RequestMapping(value = { "/", "index" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Shop Application");
		model.addAttribute("pageUrl", "index");
		model.addAttribute("page", "main");
		return "BASE_PAGE";

	}

}
