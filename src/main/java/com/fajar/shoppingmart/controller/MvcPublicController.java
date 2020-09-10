package com.fajar.shoppingmart.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.runtime.FlatFileAccessor;
import com.fajar.shoppingmart.service.transaction.ProductService;
import com.fajar.shoppingmart.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MvcPublicController extends BaseController{ 
	 
	@Autowired
	private ProductService productService;
	@Autowired
	private FlatFileAccessor flatFileAccessor;
	
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
		model.addAttribute("imageUrlList", CollectionUtil.listToKeyVal(randomImages));
		model.addAttribute("page", "main"); 
		model.addAttribute("hideNavBar", true);
		return basePage;

	}
	
	
	@RequestMapping(value = { "/public/catalog","/public/catalog/", "/public/catalog/{option}" })
	@CustomRequestInfo(title = "Product Catalog", pageUrl = "webpage/catalog-page", scriptPaths = {"product-catalog", "product-catalog-display" }, stylePaths = {"filter-box"})
	public String catalog(@PathVariable(required = false)String option, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("page", "main");
		model.addAttribute("categories", componentService.getAllCategories());
		model.addAttribute("defaultOption", option == null || option.equals("")? null:option); 
		return basePage;

	}
	
	@RequestMapping(value = { "/public/about" })
	@CustomRequestInfo(title="About Us", pageUrl = "webpage/about-page")
	public String about(  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		setActivePage(request );

		model.addAttribute("page", "about"); 
		return basePage;

	}
	
	@RequestMapping(value = { "/public/suppliers" })
	@CustomRequestInfo(title="Our Suppliers", pageUrl = "webpage/supplier-page")
	public String suppliers(  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("page", "main"); 
		return basePage;

	}
	
	@RequestMapping(value = { "/test_sessions" })
	public void testSessionTxt(  HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionTemp =flatFileAccessor.printSessions();
		
		response.setContentType(MediaType.TEXT_PLAIN_VALUE);
		response.getWriter().write(sessionTemp);
	 

	}
	

}
