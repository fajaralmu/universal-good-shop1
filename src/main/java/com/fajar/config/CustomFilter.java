package com.fajar.config;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fajar.service.UserSessionService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomFilter implements javax.servlet.Filter {
 
	@Autowired
	private UserSessionService userSessionService;
	
	
	
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
  
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        boolean isRestEndpoint = req.getRequestURI().contains("universal-good-shop/api");
        
        if(isRestEndpoint) {
	        System.out.println("=====================BEGIN API==================");
	        System.out.println(new Date());
	        System.out.println(req.getMethod()) ;
	        System.out.println(req.getRequestURI());
	         
        } 
        	
        chain.doFilter(request, response);
        
        if(isRestEndpoint) {
	        System.out.println(res.getStatus());
	        System.out.println(res.getContentType());
	        System.out.println("================END API=================");
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
