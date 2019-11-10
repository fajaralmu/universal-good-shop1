package com.fajar.config;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Configuration
public class CustomFilter implements javax.servlet.Filter {
 
	 	
	@Bean
	public Registry registry() throws RemoteException {
		Registry reg;
		try {
			System.out.println("========== REGISTRY CREATING ========= ");
			reg = java.rmi.registry.LocateRegistry.createRegistry(12345);
			System.out.println("========== REGISTRY CREATED ========== ");
			return reg;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
	}
	
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
  
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        System.out.println(" ________________________________BEGIN________________________________");
      
        log.info(
          "Logging Request  {} : {}", req.getMethod(), 
          req.getRequestURI());
        chain.doFilter(request, response);
        log.info(
          "Logging Response :{}", 
          res.getContentType());
        System.out.println(" ________________________________END________________________________ ");
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
