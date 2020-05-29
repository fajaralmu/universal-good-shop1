package com.fajar.shoppingmart.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomFilter implements javax.servlet.Filter {
  
	
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
  
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        boolean isRestEndpoint = true;//req.getMethod().toLowerCase().equals("post");
        
        if(isRestEndpoint) {
	        log.info("****************************** BEGIN API ***************************");
	        log.info("Content Type: {}", req.getContentType());
	        log.info("Method: {} Uri: {}",req.getMethod(), req.getRequestURI());
	        log.info("********************************************************************");
	         
        } 
        	
        chain.doFilter(request, response);
        
        if(isRestEndpoint) {
        	log.info("********************************************************************");
	        log.info("Status: {}", res.getStatus());
	        log.info("Content Type: {}", res.getContentType());
	        log.info("***************************** END API ******************************");
        }
    }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
 
    // other methods
}
