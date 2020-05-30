package com.fajar.shoppingmart.service;

import java.lang.reflect.Field;
import java.rmi.Remote;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.RegistryModel;
import com.fajar.shoppingmart.dto.SessionData;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.RegisteredRequest;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.RegisteredRequestRepository;
import com.fajar.shoppingmart.repository.UserRepository;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	public static final String SESSION_DATA = "session_data";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RegisteredRequestRepository registeredRequestRepository;
	
	@Autowired
	private RealtimeService2 realtimeService;

	@Autowired
	private RuntimeService registryService;
	
	@Autowired
	private MessagingService messagingService;
	
	public static final String PAGE_CODE = "page-code";  
	public static final String ATTR_USER = "user"; 
	public static final String ACCESS_CONTROL_EXPOSE_HEADER = "Access-Control-Expose-Headers";
	public static final String ATTR_REQUEST_URI = SessionData.ATTR_REQUEST_URI;
	public static final String HEADER_LOGIN_KEY = "loginKey";
	public static final String HEADER_REQUEST_TOKEN = "requestToken";
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	/**
	 * get user from httpSession
	 * @param request
	 * @return
	 */
	public User getUserFromSession(HttpServletRequest request) {
		try {
			return (User) request.getSession(false).getAttribute(ATTR_USER);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * get user from runtime
	 * @param request
	 * @return
	 */
	public User getUserFromRegistry(HttpServletRequest request) {
		String loginKey = request.getHeader(HEADER_LOGIN_KEY);
		RegistryModel registryModel = registryService.getModel(loginKey);

		if (registryModel == null) {
			return null;
		}

		return registryModel.getUser();
	}

	public User getUserFromRegistry(String loginKey) {
		RegistryModel registryModel = registryService.getModel(loginKey);

		if (registryModel == null) {
			return null;
		}
		User user = registryModel.getUser();

		return user;
	}

	public boolean hasSession(HttpServletRequest request) {
		return hasSession(request, true);
	}

	public boolean hasSession(HttpServletRequest request, boolean setRequestURI) {
		if (setRequestURI && request.getMethod().toLowerCase().equals("get") && request.getRequestURI().contains("login") == false) {

			request.getSession().setAttribute(ATTR_REQUEST_URI, request.getRequestURI());
			log.info("REQUESTED URI: " + request.getSession(false).getAttribute(ATTR_REQUEST_URI));
		} 
		
		/**
		 * handle Client
		 */ 
		 
		if (request.getHeader(HEADER_LOGIN_KEY) != null) {
			String remoteAddress = request.getRemoteAddr();
			int remotePort = request.getRemotePort(); 
			log.info("remoteAddress:" + remoteAddress + ":" + remotePort);
			boolean registered = getUserFromRegistry(request) != null;
			return registered;
		}

		/**
		 * end handle Client
		 */
 
		Object sessionObj = request.getSession().getAttribute(ATTR_USER);
		
		if (sessionObj == null || !(sessionObj instanceof User)) {
			log.info("invalid session object: {}", sessionObj);
			return false;
		}
		User sessionUser = (User) request.getSession().getAttribute(ATTR_USER);

		try {
			RegistryModel registryModel = registryService.getModel(sessionUser.getLoginKey().toString());

			if (sessionUser == null || registryModel == null || !sessionUser.equals(registryModel.getUser())) {
				log.error("==========USER NOT EQUALS==========");
				throw new Exception();
			}  
			log.info("USER HAS SESSION");
			return true;

		} catch (Exception ex) {
			log.info("USER DOSE NOT HAVE SESSION");
			ex.printStackTrace();
			return false;
		}
	}

	public String addUserSession(final User dbUser, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws IllegalAccessException {
		RegistryModel registryModel = null;
		try {
			registryModel = new RegistryModel(); 
			registryModel.setUser(dbUser);
			registryModel.setUserToken(UUID.randomUUID().toString()); 
		 
			String key = UUID.randomUUID().toString();
			dbUser.setLoginKey(key);
			
			boolean registryIsSet = registryService.set(key, registryModel);
			if (!registryIsSet) {
				throw new Exception();
			}

			httpResponse.addHeader(HEADER_LOGIN_KEY, key);
			httpResponse.addHeader(ACCESS_CONTROL_EXPOSE_HEADER, "*");
			
			httpRequest.getSession(true).setAttribute(ATTR_USER, dbUser);
			
			log.info(" > > > SUCCESS LOGIN :");
			return dbUser.getLoginKey();
		} catch (Exception e) {
			e.printStackTrace();
			log.info(" < < < FAILED LOGIN");
			throw new IllegalAccessException("Login Failed");
		}
	}

	public boolean logout(HttpServletRequest request) { 
		 
		try {   
			
			User user = getLoggedUser(request);  
			registryService.unbind(user.getLoginKey().toString()); 
			invalidateSessionUser(request);

			log.info(" > > > > > SUCCESS LOGOUT");
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
			log.info(" < < < < < FAILED LOGOUT");
			return false;
		} 
	}

	/**
	 * get logged user
	 * @param request
	 * @return
	 */
	private User getLoggedUser(HttpServletRequest request) {
		User user = getUserFromSession(request);  
		 
		try {
			if (user == null && request.getHeader(HEADER_LOGIN_KEY)!=null) {
				user = getUserFromRegistry(request); 
			} 
			return user;
		}catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	private void invalidateSessionUser(HttpServletRequest request) { 
		request.getSession(false).removeAttribute(ATTR_USER);
		request.getSession(false).invalidate();
	}

	/**
	 * get token
	 * @param httpRequest
	 * @return
	 */
	public String getToken(HttpServletRequest httpRequest) {
		User user = getUserFromSession(httpRequest);
		log.info("==loggedUser: "+user);
		
		if(user == null)
			return null;
		return getToken(user);
	}
	
	/**
	 * get token
	 * @param user
	 * @return
	 */
	public String getToken(User user) {
		RegistryModel reqModel = registryService.getModel(user.getLoginKey());
		if(reqModel == null) {
			throw new RuntimeErrorException(null, "Invalid Session");
		}
		String token = reqModel.getUserToken();
		return token;
	}

	public boolean validatePageRequest(HttpServletRequest req) {
		final String requestId = req.getHeader(RuntimeService.PAGE_REQUEST_ID);
		log.info("Page request id: " + requestId);
		
		if(null == requestId) {
			return false;
		}
		
		// check from DB
		RegisteredRequest registeredRequest = registeredRequestRepository.findTop1ByRequestId(requestId);
		SessionData sessionData = null;
		
		if (null == registeredRequest) {
			sessionData = registryService.getModel(SESSION_DATA);  
		}
		if (null != sessionData) {
			registeredRequest = sessionData.getRequest(requestId);
		}
		if (registeredRequest != null) {
			log.info("Found Registered Request: " + registeredRequest);
			return true;
		}
		log.info("Reuqest not registered");

		return registryService.validatePageRequest(req);
	}

	private static void removeAttribute(Object object, String... fields) {
		for (String fieldName : fields) {
			Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);
			try {
				field.setAccessible(true);
				field.set(object, null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.info("Error------- and catched");
				e.printStackTrace();
			}
		}
	}

	public WebResponse getProfile(HttpServletRequest httpRequest) {

		User user = getUserFromRegistry(httpRequest);
		if (user != null) {
			removeAttribute(user, "role", "password");
		}
		return WebResponse.builder().code("00").entity(user).build();
	}
	
	/**
	 * ===================SESSION MANAGEMENT========================
	 * 
	 */

	public WebResponse requestId(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		String requestId;
		 
		if(validatePageRequest(servletRequest)) {
			 requestId = servletRequest.getHeader(RuntimeService.PAGE_REQUEST_ID);
			 
			 if(hasSession(servletRequest)) {
				 servletResponse.addHeader(HEADER_LOGIN_KEY,servletRequest.getHeader(HEADER_LOGIN_KEY));
			 } 
			 
		} else {
		
			requestId = UUID.randomUUID().toString();   
		}
		
		SessionData sessionData = generateSessionData(servletRequest, servletResponse,requestId);
		if (!registryService.set(SESSION_DATA, sessionData ))
			throw new  RuntimeErrorException(null,"Error generating request id");
		
		log.info("NEW Session Data Created: {}", (SessionData) registryService.getModel(SESSION_DATA));
		realtimeService.sendUpdateSession(getAvailableSessions()); 
		
		return WebResponse.builder().code("00").message(requestId).build();
	}
	
	private SessionData generateSessionData(HttpServletRequest servletRequest, HttpServletResponse servletResponse, String requestId) {
		
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		
		if (null == sessionData) {
			if (!registryService.set(SESSION_DATA, new SessionData()))
				throw new RuntimeErrorException(null, "Error getting session data");
			
			sessionData  = registryService.getModel(SESSION_DATA);
		}
		
		String ipAddress = servletRequest.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {  
		    ipAddress = servletRequest.getRemoteAddr();  
		}
		
		String referrer =  servletRequest.getHeader("Referer");
		String userAgent = servletRequest.getHeader("User-Agent");
		
		RegisteredRequest request = RegisteredRequest.builder().
				ipAddress(ipAddress).
				referrer(referrer).
				userAgent(userAgent).
				requestId(requestId).
				created(new Date()).
				value(null).
				build();
		
		sessionData.addNewApp(request);
		return sessionData;
	}

	public RegisteredRequest getRegisteredRequest(String requestId) {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		RegisteredRequest registeredRequest = sessionData.getRequest(requestId);
		
		if(null == registeredRequest) {
			throw new RuntimeException("Invalid Session Data");
		}
		return registeredRequest;
	}

	/**
	 * key for client app
	 * @return
	 */
	public WebResponse getAvailableSessions() { 
		
		List<BaseEntity> appSessions = getAvailableSessionList();
		 
		for (BaseEntity appSession : appSessions) {
			List<BaseEntity> messages = messagingService.getMessages(((RegisteredRequest)appSession).getRequestId());
			((RegisteredRequest)appSession).setMessages(messages);
		}
		return WebResponse.builder().code("00").entities(appSessions).build();
	}
	
	private List<BaseEntity> getAvailableSessionList() {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		
		if (null == sessionData) {
			log.info("Session Data IS NULL");
			boolean successSettingRegistry = registryService.set(SESSION_DATA, new SessionData());
			
			if (!successSettingRegistry )
				throw new  RuntimeErrorException(null,"Error updating session data");
			
			sessionData  = registryService.getModel(SESSION_DATA);
		}else {
			log.info("sessionData found: {}", sessionData);
		}
		
		List<BaseEntity> appSessions = CollectionUtil.mapToList(sessionData.getRegisteredApps());
		 
		return appSessions;
	}
	
	public void setActiveSession(String requestId, boolean active) {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		if(null == sessionData) {
			return;
		}
		((SessionData)registryService.getModel(SESSION_DATA)).setActiveSession(requestId, active);
	}

	public RegisteredRequest getAvailableSession(String requestId) {
		
		List<RegisteredRequest> sessionList = CollectionUtil.convertList(getAvailableSessionList());
		for (RegisteredRequest baseEntity : sessionList) {
			if(baseEntity.getRequestId().equals(requestId)) {
				return baseEntity;
			}
		}
		return null;
	}

	public WebResponse deleteSession(WebRequest request) {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		sessionData.remove(request.getRegisteredRequest().getRequestId());
		
		if (!registryService.set(SESSION_DATA, sessionData))
			throw new  RuntimeErrorException(null, "Error updating session data");

		return WebResponse.builder().code("00").sessionData(sessionData).build();
	}

	public WebResponse clearSessions() {
		SessionData sessionData = registryService.getModel(SESSION_DATA);
		sessionData.clear();
		
		if (!registryService.set(SESSION_DATA, sessionData))
			throw new  RuntimeErrorException(null, "Error updating session data");
		sessionData  = registryService.getModel(SESSION_DATA);
		
		realtimeService.sendUpdateSession(getAvailableSessions());
		return WebResponse.builder().code("00").sessionData(sessionData).build();
	}
	
	public String getPageCode(HttpServletRequest request) {
		log.info("getPageCode");
		try {
			String pageCode = request.getSession().getAttribute(PAGE_CODE).toString();
			log.info("pageCode: {}", pageCode);
			return pageCode;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setActivePage(HttpServletRequest request, String pageCode) {
		log.info("setActivePage: {}", pageCode);
		try {
			request.getSession(false).setAttribute(PAGE_CODE, pageCode);
			log.info("pageCode: {}", pageCode);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * get user by userName and password
	 * @param request
	 * @return
	 */
	public User getUserByUsernameAndPassword(WebRequest request) {
		User requestUser = request.getUser();
		User dbUser = userRepository.findByUsername (requestUser.getUsername());
		
		if(dbUser != null) {
			log.info("username: {} exist", dbUser.getUsername() );
		}else {
			log.error("username: {} does not exist", requestUser.getUsername());
		}
		
		boolean passwordMatched = comparePassword(dbUser, requestUser.getPassword());
		log.info("LOgged User Role: {}", dbUser.getRole());
		return passwordMatched ? dbUser : null;
	}
	
	private boolean comparePassword(User dbUser, String password) {
		if(null == password || dbUser == null) {
			return false;
		}
		
		boolean match = password.equals(dbUser.getPassword());
		log.info("Password match: {}", match);
		
		return match;
	}

}
