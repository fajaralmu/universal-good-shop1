package com.fajar.shoppingmart.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController extends BaseController {

	@RequestMapping(value = "app-error", method = RequestMethod.GET)
	public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
		ModelAndView errorPage = new ModelAndView("error/errorPage");
		String errorMsg = "";
		int httpErrorCode = getErrorCode(httpRequest);
		 errorPage.addObject("title", "Error: "+ httpErrorCode);
		 errorPage.addObject("errorMessage", "Error occured ("+httpErrorCode+")");
		 printHttpRequestAttrs(httpRequest);
		return errorPage;
	}
	
	private void printHttpRequestAttrs(HttpServletRequest httpRequest) {
		Enumeration<String> attrNames = httpRequest.getAttributeNames();
		while(attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			System.out.println(attrName+":"+httpRequest.getAttribute(attrName));
		}
	}

	private int getErrorCode(HttpServletRequest httpRequest) {
		try {
			return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
		}catch (Exception e) {
			
			return 500;
		}
	}
}
