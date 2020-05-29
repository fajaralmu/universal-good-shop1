package com.fajar.shoppingmart.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

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
  
	private Date startTime, endTime = new Date();
	
	public CustomFilter() {
		log.info("_________________CustomFilter______________");
	}
	
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
  
    	startTime = new Date();
        HttpServletRequest req = (HttpServletRequest) request; 
        
        boolean isRestEndpoint = true;//req.getMethod().toLowerCase().equals("post");
        
        if(isRestEndpoint) {
	        System.out.println("****************************** BEGIN API ***************************"); 
	        System.out.println(uriInfo(req));
	        printRequestHeaders(req);
	        System.out.println(contentTypeInfo(req));
	        System.out.println("********************************************************************");
	        System.out.println();
	         
        } 
        	
        chain.doFilter(request, response);
        HttpServletResponse res = (HttpServletResponse) response;
        
        endTime = new Date();
        
        if(isRestEndpoint) {
        	System.out.println();
        	System.out.println("***************************** END API *******************************");
        	System.out.println(uriInfo(req));
        	printResponseHeaders(res);
	        System.out.println("Status: "+ res.getStatus()+" Duration: "+ getDuration()+" ms");
	        System.out.println(contentTypeInfo(req));
	        System.out.println("*********************************************************************");
        }
    }
    
    private void printResponseHeaders(HttpServletResponse res) {
    	Collection<String> headers = res.getHeaderNames(); 
		for (String header : headers) {
			System.out.println(header+": "+res.getHeader(header));
		}
		
	}

	private void printRequestHeaders(HttpServletRequest req) {
		Enumeration<String> headers = req.getHeaderNames(); 
		while(headers.hasMoreElements()) {
			String header = headers.nextElement();
			System.out.println(header+": "+req.getHeader(header));
		}
		 
	}

	private String contentTypeInfo(HttpServletRequest req) {

    	String contentInfo = "Content Type: "+ req.getContentType();
		return contentInfo;
	}

	private String uriInfo(HttpServletRequest req) {
    	return("URI: ["+req.getMethod()+"]"+req.getRequestURI());
    }

    private long getDuration() {
    	return endTime.getTime() - startTime.getTime();
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
