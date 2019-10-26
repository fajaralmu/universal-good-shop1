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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.service.ComponentService;
import com.fajar.service.WebAppConfiguration;
import com.fajar.util.MVCUtil;

@Controller
public class MvcPublicController {
	Logger log = LoggerFactory.getLogger(MvcPublicController.class);
	@Autowired
	private WebAppConfiguration webAppConfiguration;
	private String basePage;
	@Autowired
	private ComponentService componentService;
	
	public MvcPublicController() {
		log.info("---------------------------MvcCommonController------------------------------");
	}
	
	@PostConstruct
	public void init() {
		basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/", "index" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("menus", componentService.getPublicMenus(request));
		model.addAttribute("host", MVCUtil.getHost(request));
		model.addAttribute("imagePath",webAppConfiguration.getUploadedImagePath());
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Shop Application");
		model.addAttribute("pageUrl", "index");
		model.addAttribute("page", "main");
		return basePage;

	}
	
	
	@RequestMapping(value = { "/public/catalog","/public/catalog/", "/public/catalog/{option}" })
	public String catalog(@PathVariable(required = false)String option, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("host", MVCUtil.getHost(request));
		model.addAttribute("imagePath",webAppConfiguration.getUploadedImagePath());
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("contextPath",request.getContextPath());
		model.addAttribute("title", "Product Catalog");
		model.addAttribute("pageUrl", "shop/catalog-page");
		model.addAttribute("page", "main");
		model.addAttribute("defaultOption", option == null || option.equals("")? "null":option);
		return basePage;

	}
	

}
