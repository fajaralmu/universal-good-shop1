package com.fajar.service;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.RegistryModel;
import com.fajar.dto.SessionData;
import com.fajar.dto.WebRequest;
import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.User;
import com.fajar.entity.UserRole;
import com.fajar.repository.UserRepository;
import com.fajar.repository.UserRoleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAccountService {
	
	private static final String ATTR_REQUEST_URI = SessionData.ATTR_REQUEST_URI;
	private static final String HEADER_LOGIN_KEY = "loginKey";
	private static final String HEADER_REQUEST_TOKEN = "requestToken";
	
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
	
	/**
	 * add new user
	 * @param request
	 * @return
	 */
	public WebResponse registerUser(WebRequest request) { 
		WebResponse response  = new WebResponse();
		Optional<UserRole> regularRoleOpt = userRoleRepository.findById(2L);
		
		if(regularRoleOpt.isPresent() == false) {
			throw new RuntimeException("invalid role");
		}
		UserRole regularRole = regularRoleOpt.get();
		
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

	public WebResponse login(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IllegalAccessException {
		User dbUser = userRepository.findByUsernameAndPassword(request.getUser().getUsername(), request.getUser().getPassword());
		 
		if(dbUser == null) {
			return new WebResponse("01","invalid credential");
		} 
		 
		User loggedUser = userSessionService.addUserSession(dbUser,httpRequest,httpResponse);
		log.info("LOGIN SUCCESS");
		
		WebResponse response = new WebResponse("00","success");
		 
		BaseEntity registeredUser = userSessionService.getUserFromRegistry(loggedUser.getLoginKey());
		BaseEntity clonedUser = new User();
		BeanUtils.copyProperties(registeredUser, clonedUser, "password","role");
		
		response.setEntity(clonedUser);
		
		if(httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI) != null) {
			log.info("WILL REDIRECT TO REQUESTED URI: "+httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI));
			response.setRedirectUrl(httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI).toString());			
		}
		return response;
	}
	
	public boolean logout(HttpServletRequest httpRequest) {
		User user = userSessionService.getUserFromSession(httpRequest);
		
		if(user == null) {
			if(httpRequest.getHeader(HEADER_LOGIN_KEY)!=null) {
				String apiKey = httpRequest.getHeader(HEADER_LOGIN_KEY);
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
	
	/**
	 * get token from session
	 * @param httpRequest
	 * @return
	 */
	public String getToken(HttpServletRequest httpRequest) {
		User user = userSessionService.getUserFromSession(httpRequest);
		log.info("==loggedUser: "+user);
		
		if(user == null)
			return null;
		return (String) userSessionService.getToken(user);
	}

	/**
	 * validate session token & registry token
	 * @param httpRequest
	 * @return
	 */
	public boolean validateToken(HttpServletRequest httpRequest) {
		String requestToken = httpRequest.getHeader(HEADER_REQUEST_TOKEN);
		/**
		 * TESTING
		 */
		boolean validated = userSessionService.validatePageRequest(httpRequest );
		if(validated) {
			return true;
		}
		if(requestToken == null) {
			log.info("NULL TOKEN");
			return false;
		}
		String existingToken = getToken(httpRequest);
		log.info("|| REQ_TOKEN: "+requestToken+" vs EXISTING:"+existingToken+"||");
		
		boolean tokenEquals = requestToken.equals(existingToken); 
		return tokenEquals;
	}
 

}
