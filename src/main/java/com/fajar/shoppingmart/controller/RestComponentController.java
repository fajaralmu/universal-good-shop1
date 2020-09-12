package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.dto.FontAwesomeIcon;
import com.fajar.shoppingmart.exception.InvalidRequestException;
import com.fajar.shoppingmart.service.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/component")
//@Authenticated(loginRequired = false)
public class RestComponentController extends BaseController{
	 
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public RestComponentController() {
		log.info("----------------------Rest Component Controller-------------------");
	}
	
	@PostMapping(value = "/fa-preview/{classAlias}",  produces = MediaType.TEXT_HTML_VALUE)
	public String moresupplier(@PathVariable(value="classAlias") String classAlias,   HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		//validatePageRequest(httpRequest);
		try {
			FontAwesomeIcon icon = FontAwesomeIcon.valueOf(classAlias);
			 
			return "<i class=\""+icon.value+"\"></i>";
		}catch (Exception e) {
			 
		}
		return "<i></i>";
	}
	
	
	public void validatePageRequest(HttpServletRequest req) { 
		boolean validated = userSessionService.validatePageRequest(req );
        if(!validated)  {
        	throw new InvalidRequestException("Invalid page request");
        }
	}
	
	
	 
}
