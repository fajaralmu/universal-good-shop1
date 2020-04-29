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

import com.fajar.entity.Page;
import com.fajar.service.ComponentService;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.ProductService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;

@Controller
@RequestMapping("webmart")
public class MvcPagesController extends BaseController{
	Logger log = LoggerFactory.getLogger(MvcPagesController.class);
	@Autowired
	private WebConfigService webAppConfiguration;
	private String basePage;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private ProductService productService;
	
	public MvcPagesController() {
		log.info("---------------------------Mvc Public Controller------------------------------");
	}
	
	@PostConstruct
	public void init() {
		basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}
 
	 
	@RequestMapping(value = { "/page/{code}" })
	public String suppliers(@PathVariable(name = "code") String code,  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Page page = componentService.getPage(code, request);
		
		if(null == page) {
			sendRedirect(response, request.getContextPath() + "/account/login");
			return basePage;
		}
		
		model.addAttribute("pageUrl", "shop/master-common-page");
		model.addAttribute("page", page); 
		return basePage;

	}
	
	
	

}
