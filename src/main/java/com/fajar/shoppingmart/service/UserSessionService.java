package com.fajar.shoppingmart.service;

import static com.fajar.shoppingmart.controller.BaseController.getJSessionIDCookie;
import static com.fajar.shoppingmart.util.SessionUtil.getLoginKey;
import static com.fajar.shoppingmart.util.SessionUtil.getPageRequestId;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.SessionData;
import com.fajar.shoppingmart.dto.UserSessionModel;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.RegisteredRequest;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.RegisteredRequestRepository;
import com.fajar.shoppingmart.repository.UserRepository;
import com.fajar.shoppingmart.service.runtime.RuntimeService;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.SessionUtil;
import com.fajar.shoppingmart.util.StringUtil;

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
	private RuntimeService runtimeService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public User getUserFromSession(HttpServletRequest request) {
		try {
			return SessionUtil.getSessionUser(request);
		} catch (Exception ex) { return null; }
	}

	public User getUserFromRuntime(HttpServletRequest request) {
		String loginKey = SessionUtil.getLoginKey(request);
		UserSessionModel sessionModel = getUserSessionModel(loginKey);

		if (sessionModel == null) { return null; } 
		return sessionModel.getUser();
	}

	public User getUserFromRuntime(String loginKey) {
		UserSessionModel sessionModel = getUserSessionModel(loginKey);

		if (sessionModel == null) { return null; } 
		return sessionModel.getUser();
	}

	public boolean hasSession(HttpServletRequest request) {
		return hasSession(request, true);
	}

	public boolean hasSession(HttpServletRequest request, boolean setRequestURI) {
		
		if (setRequestURI && saveRequestUri(request)) { 
			SessionUtil.setSessionRequestUri(request);
		}

		/**
		 * handle standAlone Client
		 */
		String loginKey = SessionUtil.getLoginKey(request);
		if (loginKey != null) {

			String remoteAddress = request.getRemoteAddr();
			int remotePort = request.getRemotePort();
			log.info("remoteAddress:" + remoteAddress + ":" + remotePort);
			
			boolean registered = getUserFromRuntime(request) != null;
			return registered;
		}

		/**
		 * end handle standAlone Client
		 */  
		try {
			User sessionUser = SessionUtil.getSessionUser(request);
			UserSessionModel sessionModel = getUserSessionModel(sessionUser.getLoginKey());

			if (sessionUser == null || sessionModel == null || !userEquals(sessionUser, sessionModel.getUser())) {
				log.error("==========USER NOT EQUALS==========");
				throw new Exception();
			}
			log.info("USER HAS SESSION, return true");
			return true;

		} catch (Exception ex) {
			log.info("USER DOES NOT HAVE SESSION, return FALSE"); 
			return false;
		}
	}

	private boolean saveRequestUri(HttpServletRequest request) {
		 
		return request.getMethod().toLowerCase().equals("get")
				&& request.getRequestURI().contains("login") == false;
	}

	private boolean userEquals(User user1, User user2) {
		try {
			log.info("httpSession loginKey: {}, sessionModel loginKey: {}", user1.getLoginKey(), user2.getLoginKey());
			return user1.getLoginKey().equals(user2.getLoginKey());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public User addUserSession(final User dbUser, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws Exception {

		try {

			String loginKey = generateLoginKey();
			dbUser.setLoginKey(loginKey);
			dbUser.setPassword(null);

			UserSessionModel sessionModel = new UserSessionModel(dbUser, generateUserToken());
			boolean sessionIsSet = runtimeService.set(loginKey, sessionModel);

			if (!sessionIsSet) {
				throw new RuntimeException("Error saving session");
			}

			SessionUtil.setLoginKeyHeader(httpResponse, loginKey);
			SessionUtil.setAccessControlExposeHeader(httpResponse);
			SessionUtil.setSessionUser(httpRequest, dbUser);

			log.info(" > > > SUCCESS LOGIN :");

			return dbUser;
		} catch (Exception e) {

			e.printStackTrace();
			log.info(" < < < FAILED LOGIN");
			throw new IllegalAccessException("Login Failed");
		}
	}

	

	public boolean logout(HttpServletRequest request) {

		try {
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
	 * get logged user from httpSession or runtime
	 * @param request
	 * @return
	 */
	public User getLoggedUser(HttpServletRequest request) {

		try {
			User user = getUserFromSession(request);
			if (user == null && getLoginKey(request) != null) {
				user = getUserFromRuntime(request);
			}
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private void invalidateSessionUser(HttpServletRequest request) {
		User user = getLoggedUser(request);
		removeUserFromRuntime(user);
		SessionUtil.removeSessionUserAndInvalidate(request);
		runtimeService.updateSessionId(getJSessionIDCookie(request).getValue(), getPageRequestId(request));

	}
 
	public String getTokenByServletRequest(HttpServletRequest httpRequest) {
		User user = getLoggedUser(httpRequest);
		log.info("::loggedUser: " + (user == null ? null : user.getUsername()));

		if (user == null) { return null; }
		return getTokenByUser(user);
	}
 
	public String getTokenByUser(User user) {
		UserSessionModel reqModel = getUserSessionModel(user.getLoginKey());
		if (reqModel == null) {
			throw new RuntimeErrorException(null, "Invalid Session");
		}
		String token = reqModel.getUserToken();
		return token;
	}

	

	public boolean validatePageRequest(HttpServletRequest httpServletRequest) {
		final String requestId = SessionUtil.getPageRequestId(httpServletRequest);

		log.debug("requestId Provided: {}", requestId);
		if (null == requestId) {
			return false;
		}
		// check if request id is exist from DB
		RegisteredRequest registeredRequest = getRegisteredRequest(requestId);

		if (registeredRequest != null) {
			log.debug("Found Registered Request: " + registeredRequest);
			return true;
		}
		log.debug("REQUEST not registered");

		return runtimeService.validatePageRequest(httpServletRequest);
	}

	public RegisteredRequest getRegisteredRequest(String requestId) {

		SessionData sessionData = getSessionData();
		RegisteredRequest registeredRequest = null;
		if (null != sessionData) {
			registeredRequest = sessionData.getRequest(requestId);
		}
		if (null == registeredRequest) {
			registeredRequest = registeredRequestRepository.findTop1ByRequestId(requestId);
			log.info("registeredRequest from DB with req Id ({}): {}", requestId, registeredRequest); 
		}

		return registeredRequest;
	}
  

	/**
	 * ===================SESSION MANAGEMENT========================
	 * 
	 */

	public WebResponse generateRequestId(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		log.info("generateRequestId... ");
		String requestId;

		if (validatePageRequest(servletRequest)) {
			requestId = SessionUtil.getPageRequestId(servletRequest);// servletRequest.getHeader(RuntimeService.PAGE_REQUEST_ID);

			if (hasSession(servletRequest)) {
				String loginKey = SessionUtil.getLoginKey(servletRequest);
				SessionUtil.setLoginKeyHeader(servletResponse, loginKey);
			}

		} else {

			requestId = generateRequestId();
		}

		SessionData sessionData = generateSessionData(servletRequest, requestId);

		if (!runtimeService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error generating request id");

		log.info("NEW Session Data Created: {}", (SessionData) getSessionData());
		realtimeService.sendUpdateSession(getAvailableSessions());

		return WebResponse.builder().code("00").message(requestId).build();
	}

	 
	private SessionData generateSessionData(HttpServletRequest servletRequest, String requestId) {

		SessionData sessionData = getSessionData();

		if (null == sessionData) {
			if (!runtimeService.createNewSessionData()) {
				throw new RuntimeErrorException(null, "Error getting session data");
			}
			sessionData = getSessionData();
		}

		RegisteredRequest requestv2 = SessionUtil.buildRegisteredRequest(servletRequest, requestId);
		sessionData.addNewApp(requestv2);
		return sessionData;
	}

	/**
	 * key for client app
	 * 
	 * @return
	 */
	public WebResponse getAvailableSessions() {

		List<BaseEntity> appSessions = CollectionUtil.convertList(getAvailableSessionList());

//		for (BaseEntity appSession : appSessions) {
//			List<BaseEntity> messages = messagingService.getMessages(((RegisteredRequest) appSession).getRequestId());
//			((RegisteredRequest) appSession).setMessages(messages);
//		}
		return WebResponse.builder().code("00").entities(appSessions).build();
	}

	private List<RegisteredRequest> getAvailableSessionList() {
		SessionData sessionData = getSessionData();

		if (null == sessionData) {
			log.info("Session Data IS NULL");
			boolean successSettingsession = runtimeService.createNewSessionData();

			if (!successSettingsession)
				throw new RuntimeErrorException(null, "Error updating session data");

			sessionData = getSessionData();
		} else {
			log.info("sessionData found: {}", sessionData);
		}

		List<RegisteredRequest> appSessions = CollectionUtil.mapToList(sessionData.getRegisteredApps());

		return appSessions;
	}

	public void setActiveSession(String requestId, boolean active) {
		SessionData sessionData = getSessionData();
		if (null == sessionData) {
			return;
		}
		sessionData.setActiveSession(requestId, active);
		runtimeService.set(SESSION_DATA, sessionData);
	}

	public RegisteredRequest getAvailableSession(String requestId) {

		List<RegisteredRequest> sessionList = CollectionUtil.convertList(getAvailableSessionList());
		for (RegisteredRequest baseEntity : sessionList) {
			if (baseEntity.getRequestId().equals(requestId)) {
				return baseEntity;
			}
		}
		return null;
	}

	public WebResponse deleteSession(WebRequest request) {
		SessionData sessionData = getSessionData();
		String requestId = request.getRegisteredrequest().getRequestId();

		sessionData.remove(requestId);

		if (!runtimeService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error updating session data");

		return WebResponse.builder().code("00").sessionData(sessionData).build();
	}

	public WebResponse clearSessions() {
		SessionData sessionData = getSessionData();
		sessionData.clear();

		if (!runtimeService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error updating session data");

		sessionData = getSessionData();

		realtimeService.sendUpdateSession(getAvailableSessions());
		return WebResponse.builder().code("00").sessionData(sessionData).build();
	} 

	// ======================================================================
	
	public String getPageCode(HttpServletRequest request) {
		log.info("getPageCode");
		try {
			String pageCode = SessionUtil.getSessionPageCode(request);
			log.info("pageCode: {}", pageCode);
			return pageCode;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setActivePage(HttpServletRequest request, String pageCode) {
		if (null == pageCode) {
			log.info("will not setActivePage, pageCode IS NULL");
			return;
		}
		log.info("setActivePage: {}", pageCode);
		try {
			SessionUtil.setSessionPageCode(request, pageCode);
			log.info("pageCode: {}", request.getSession().getAttribute("page-code"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
 
	public User getUserByUsernameAndPassword(WebRequest request) {
		User requestUser = request.getUser();
		User dbUser = userRepository.findByUsername(requestUser.getUsername());

		if (dbUser != null) {
			log.info("username: {} exist", dbUser.getUsername());
		} else {
			log.error("username: {} does not exist", requestUser.getUsername());
			return null;
		}

		boolean passwordMatched = comparePassword(dbUser, requestUser.getPassword());
		log.info("Logged User Role: {}", dbUser.getRole());
		return passwordMatched ? dbUser : null;
	}

	private boolean comparePassword(User dbUser, String password) {
		if (null == password || dbUser == null) {
			return false;
		}

		boolean match = password.equals(dbUser.getPassword());
		log.info("Password match: {}", match);

		return match;
	}

	//==========================RUNTIME=========================
	private SessionData getSessionData() {
		return runtimeService.getModel(SESSION_DATA, SessionData.class);
	}
	private UserSessionModel getUserSessionModel(String key) {
		return runtimeService.getModel(key, UserSessionModel.class);
	}
	private void removeUserFromRuntime(User user) {
		runtimeService.remove(user.getLoginKey());
	}
	
	//===========================================================
	
	private String generateLoginKey() {
		return UUID.randomUUID().toString() + "-" + StringUtil.generateRandomNumber(10);
	}

	private String generateUserToken() {
		return StringUtil.generateRandomNumber(10) + "-" + UUID.randomUUID().toString();
	}
	
	private String generateRequestId() {
		return StringUtil.generateRandomNumber(17);
	}
}
