package com.fajar.shoppingmart.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MvcPublicController extends BaseController{ 
	 
	@Autowired
	private ProductService productService;
	
	public MvcPublicController() {
		log.info("---------------------------Mvc Public Controller------------------------------");
	}
	
	@PostConstruct
	public void init() {
		basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/", "index" })
	@CustomRequestInfo(title="Shopping Mart Application", pageUrl = "index")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String imagebasePath = getFullImagePath(request);

		List<String> randomImages = productService.getRandomProductImages(imagebasePath); 
		model.addAttribute("imageUrlList", randomImages);
		model.addAttribute("page", "main"); 
		
		return basePage;

	}
	
	
	@RequestMapping(value = { "/public/catalog","/public/catalog/", "/public/catalog/{option}" })
	@CustomRequestInfo(title = "Product Catalog", pageUrl = "shop/catalog-page")
	public String catalog(@PathVariable(required = false)String option, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("page", "main");
		model.addAttribute("categories", componentService.getAllCategories());
		model.addAttribute("defaultOption", option == null || option.equals("")? null:option); 
		return basePage;

	}
	
	@RequestMapping(value = { "/public/about" })
	@CustomRequestInfo(title="About Us", pageUrl = "shop/about-page")
	public String about(  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		setActivePage(request );

		model.addAttribute("page", "about"); 
		return basePage;

	}
	
	@RequestMapping(value = { "/public/suppliers" })
	@CustomRequestInfo(title="Our Suppliers", pageUrl = "shop/supplier-page")
	public String suppliers(  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("page", "main"); 
		return basePage;

	}
	

}
