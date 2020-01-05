package com.fajar.service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.User;
import com.fajar.entity.UserRole;
import com.fajar.entity.setting.RegistryModel;
import com.fajar.repository.UserRepository;
import com.fajar.repository.UserRoleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAccountService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private RegistryService registryService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	
	public ShopApiResponse registerUser(ShopApiRequest request) { 
		ShopApiResponse response  = new ShopApiResponse();
		UserRole regularRole = userRoleRepository.findById(2L).get();
		User user = new User();
		user.setDisplayName(request.getUser().getDisplayName());
		user.setDeleted(false);
		user.setRole(regularRole);
		user.setPassword(request.getUser().getPassword());
		user.setUsername(request.getUser().getUsername());
		try {
			User newUser = userRepository.save(user);
			response.setUser(newUser);
			return response;
		}catch (Exception e) {
			response.setCode("01");
			response.setMessage("Error Record new Data");
			return response;
		}
	}

	public ShopApiResponse login(ShopApiRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		User dbUser = userRepository.findByUsernameAndPassword(request.getUser().getUsername(), request.getUser().getPassword());
		 
		if(dbUser == null) {
			return new ShopApiResponse("01","invalid credential");
		}
		 
		userSessionService.addUserSession(dbUser,httpRequest,httpResponse);
		log.info("--------LOGIN SUCCESS");
		
		ShopApiResponse response = new ShopApiResponse("00","success");
		if(httpRequest.getSession(false).getAttribute("requestURI")!=null) {
			log.info("WILL REDIRECT TO REQUESTED URI: "+httpRequest.getSession(false).getAttribute("requestURI"));
			response.setRedirectUrl(httpRequest.getSession(false).getAttribute("requestURI").toString());			
		}
		return response;
	}
	
	public boolean logout(HttpServletRequest httpRequest) {
		User user = userSessionService.getUserFromSession(httpRequest);
		if(user == null) {
			if(httpRequest.getHeader("loginKey")!=null) {
				String apiKey = httpRequest.getHeader("loginKey");
				RegistryModel registryModel = registryService.getModel(apiKey);
				if(registryModel == null) {
					return false;
				}
				user = registryModel.getUser();
			}else
				return false;
		}
		 
		userSessionService.logout(httpRequest);
		return true;
	}
	
	public String getToken(HttpServletRequest httpRequest) {
		User user = userSessionService.getUserFromSession(httpRequest);
		System.out.println("==loggedUser: "+user);
		if(user == null)
			return null;
		return (String) userSessionService.getToken(user);
	}

	public boolean validateToken(HttpServletRequest httpRequest) {
		String requestToken = httpRequest.getHeader("requestToken");
		/**
		 * TESTING
		 */
		boolean validated = userSessionService.validatePageRequest(httpRequest );
		if(validated) {
			return true;
		}
		if(requestToken == null) {
			System.out.println("NULL TOKEN");
			return false;
		}
		String existingToken = getToken(httpRequest);
		System.out.println("_______REQ_TOKEN: "+requestToken+" vs EXISTING:"+existingToken);
		return requestToken.equals(existingToken);
	}
 

}
