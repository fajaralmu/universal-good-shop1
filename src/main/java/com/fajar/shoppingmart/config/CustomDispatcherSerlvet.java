package com.fajar.shoppingmart.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomDispatcherSerlvet extends DispatcherServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8040052984943558941L;
	
	public CustomDispatcherSerlvet() {
		super(); 
		log.debug(" Initialized CustomDispatcherSerlvet ");
	}
	public CustomDispatcherSerlvet(WebApplicationContext webApplicationContext) {
		super(webApplicationContext); 
		log.debug(" Initialized CustomDispatcherSerlvet with webApplicationContext ");
	}
	
	@Override
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug(" doDispatch {}", request.getRequestURI());
		super.doDispatch(request, response);
	}
	
	
	

}
