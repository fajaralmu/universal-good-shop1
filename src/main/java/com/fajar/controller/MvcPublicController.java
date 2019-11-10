package com.fajar.controller;

import java.io.IOException;
import java.util.List;

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
import com.fajar.service.ProductService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;
import com.fajar.util.MVCUtil;

@Controller
public class MvcPublicController extends BaseController{
	Logger log = LoggerFactory.getLogger(MvcPublicController.class);
	@Autowired
	private WebConfigService webAppConfiguration;
	private String basePage;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private ProductService productService;
	
	public MvcPublicController() {
		log.info("---------------------------Mvc Public Controller------------------------------");
	}
	
	@PostConstruct
	public void init() {
		basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/", "index" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String imagebasePath = getFullImagePath(request);
		model.addAttribute("menus", componentService.getPublicMenus(request));
		model.addAttribute("title", "Shop Application");
		model.addAttribute("pageUrl", "index");
		List<String> randomImages = productService.getRandomProductImages(imagebasePath); 
		model.addAttribute("imageUrlList", randomImages);
		model.addAttribute("page", "main"); 
		
		return basePage;

	}
	
	
	@RequestMapping(value = { "/public/catalog","/public/catalog/", "/public/catalog/{option}" })
	public String catalog(@PathVariable(required = false)String option, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

	 
		model.addAttribute("title", "Product Catalog");
		model.addAttribute("pageUrl", "shop/catalog-page");
		model.addAttribute("page", "main");
		model.addAttribute("categories", componentService.getAllCategories());
		model.addAttribute("defaultOption", option == null || option.equals("")? null:option); 
		return basePage;

	}
	
	@RequestMapping(value = { "/public/about" })
	public String about(  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		 
		model.addAttribute("title", "About Us");
		model.addAttribute("pageUrl", "shop/about-page");
		model.addAttribute("page", "about"); 
		return basePage;

	}
	
	@RequestMapping(value = { "/public/suppliers" })
	public String suppliers(  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		 
		model.addAttribute("title", "Our Suppliers");
		model.addAttribute("pageUrl", "shop/supplier-page");
		model.addAttribute("page", "main"); 
		return basePage;

	}
	

}
