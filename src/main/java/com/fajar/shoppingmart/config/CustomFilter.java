package com.fajar.shoppingmart.config;

import java.io.IOException;
import java.util.Date;

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
        HttpServletResponse res = (HttpServletResponse) response;
        
        boolean isRestEndpoint = true;//req.getMethod().toLowerCase().equals("post");
        
        if(isRestEndpoint) {
	        System.out.println("****************************** BEGIN API ***************************");
	        System.out.println("Content Type: "+ req.getContentType());
	        System.out.println("Method: "+req.getMethod()+" \nURI: "+ req.getRequestURI());
	        System.out.println("********************************************************************");
	        System.out.println();
	         
        } 
        	
        chain.doFilter(request, response);
        endTime = new Date();
        
        if(isRestEndpoint) {
        	System.out.println();
        	System.out.println("********************************************************************");
        	System.out.println("URI: "+req.getRequestURI());
	        System.out.println("Status: "+ res.getStatus()+" Duration: "+ getDuration()+" ms");
	        System.out.println("Content Type: "+ res.getContentType());
	        System.out.println("***************************** END API ******************************");
        }
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
