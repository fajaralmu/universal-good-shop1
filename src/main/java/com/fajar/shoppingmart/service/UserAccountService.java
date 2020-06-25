package com.fajar.shoppingmart.service;

import static com.fajar.shoppingmart.service.UserSessionService.ATTR_REQUEST_URI;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.repository.UserRepository;
import com.fajar.shoppingmart.repository.UserRoleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAccountService { 
	
	public static final String ATTR_REGISTERED_REQUEST_ID = "registered_request_id";
	@Autowired
	private UserSessionService userSessionService; 
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	
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
	 
	/**
	 * login to system
	 * @param request
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws IllegalAccessException
	 */
	public WebResponse login(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IllegalAccessException {
		User dbUser = userSessionService.getUserByUsernameAndPassword(request);
		 
		if(dbUser == null) {
			return new WebResponse("01","invalid credential");
		} 
		 
		String loginKey = userSessionService.addUserSession(dbUser, httpRequest, httpResponse);
		dbUser.setLoginKey(loginKey); 
		dbUser.setPassword(null);
//		dbUser.setRole(null); 
		
		WebResponse response = new WebResponse("00","success");   
		response.setEntity(dbUser);
		WebResponse reqIdGenerated = userSessionService.requestId(httpRequest, httpResponse);
		httpRequest.getSession(false).setAttribute(ATTR_REGISTERED_REQUEST_ID, reqIdGenerated.getMessage());
		
		log.info("LOGIN SUCCESS");
		
		if(httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI) != null) {
			log.info("WILL REDIRECT TO REQUESTED URI: "+httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI));
			response.setRedirectUrl(httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI).toString());			
		}
		response.setMessage(reqIdGenerated.getMessage());
		return response;
	} 
	
	/**
	 * logout from system
	 * @param httpRequest
	 * @return
	 */
	public boolean logout(HttpServletRequest httpRequest) { 
		 
		boolean logoutResult = userSessionService.logout(httpRequest);
		return logoutResult;
	} 

	/**
	 * validate session token & registry token
	 * @param httpRequest
	 * @return
	 */
	public boolean validateToken(HttpServletRequest httpRequest) {
		String requestToken = httpRequest.getHeader(UserSessionService.HEADER_REQUEST_TOKEN);
		/**
		 * TESTING
		 */
		boolean pageRequestValidated = userSessionService.validatePageRequest(httpRequest );
		if(pageRequestValidated) {
			return true;
		}else {
			return validateToken(requestToken, httpRequest);
		} 
	}

	/**
	 * compare token
	 * @param requestToken
	 * @param httpRequest
	 * @return
	 */
	private boolean validateToken(String requestToken, HttpServletRequest httpRequest) {
		
		if(requestToken == null) {
			log.error("NULL TOKEN, invalid request");
			return false;
			
		}else {
			
			String existingToken = userSessionService.getToken(httpRequest);
			log.info("|| REQUEST_TOKEN: "+requestToken+" vs EXISTING:"+existingToken+"||");
			
			boolean tokenEquals = requestToken.equals(existingToken); 
			return tokenEquals;
		}
	}
	
	public UserRole getRole(User user) {
		log.info("Role From User: {}", user.getRole());
		Optional<UserRole> dbUserRole = userRoleRepository.findById(user.getRole().getId());
		return dbUserRole.get();
	}
 

}
