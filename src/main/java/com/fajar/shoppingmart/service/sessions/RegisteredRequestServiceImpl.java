package com.fajar.shoppingmart.service.sessions;

import static com.fajar.shoppingmart.service.sessions.SessionValidationService.SESSION_DATA;

import java.util.List;

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
import com.fajar.shoppingmart.repository.RegisteredRequestRepository;
import com.fajar.shoppingmart.service.RealtimeService2;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.runtime.RuntimeService;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.SessionUtil;
import com.fajar.shoppingmart.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegisteredRequestServiceImpl implements RegisteredRequestService {
	@Autowired
	private RegisteredRequestRepository registeredRequestRepository;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private RealtimeService2 realtimeService;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private WebConfigService webConfigService;
	
	@Override
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

	@Override
	public WebResponse getAvailableSessions() {
		
		List<BaseEntity> appSessions = CollectionUtil.convertList(this.getAvailableSessionList());
		return WebResponse.builder().code("00").entities(appSessions).build();
	}

	@Override
	public WebResponse deleteSession(WebRequest request) {
		
		SessionData sessionData = getSessionData();
		String requestId = request.getRegisteredrequest().getRequestId();

		sessionData.remove(requestId);

		if (!runtimeService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error updating session data");

		return WebResponse.builder().code("00").sessionData(sessionData).build();
	}

	@Override
	public WebResponse clearSessions() {
		
		SessionData sessionData = getSessionData();
		sessionData.clear();

		if (!runtimeService.set(SESSION_DATA, sessionData))
			throw new RuntimeErrorException(null, "Error updating session data");

		sessionData = getSessionData();

		realtimeService.sendUpdateSession(getAvailableSessions());
		return WebResponse.builder().code("00").sessionData(sessionData).build();
	}

	@Override
	public WebResponse generateRequestId(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		
		log.info("generateRequestId... ");
		String requestId;
		WebResponse response = new WebResponse();

		if (sessionValidationService.validatePageRequest(servletRequest)) {
			requestId = SessionUtil.getPageRequestId(servletRequest);// servletRequest.getHeader(RuntimeService.PAGE_REQUEST_ID);

			if (sessionValidationService.hasSession(servletRequest)) {
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
		
		///////////
		try {
			String loginKey = SessionUtil.getLoginKey(servletRequest);
			UserSessionModel userSessionModel = runtimeService.getUserSessionModel(loginKey); 
			if (null != userSessionModel) {
				 
				response.setSessionData(SessionData.builder().user(userSessionModel.getUser()).build());
				response.setLoggedIn(true);
			}  
		 }catch (Exception e) {
			// TODO: handle exception
		}
		/////////
		response.setMessage(requestId);
		response.setApplicationProfile(webConfigService.getProfile());
		return response;
	}
	
	
	/////////////////////// privates ///////////////////////////
	
	private SessionData getSessionData() {
		return runtimeService.getModel(SESSION_DATA, SessionData.class);
	}
	
	private List<RegisteredRequest> getAvailableSessionList() {
		SessionData sessionData = getSessionData();

		if (null == sessionData) {
			log.info("Session Data IS NULL");
			boolean created = runtimeService.createNewSessionData();

			if (!created) {
				throw new RuntimeErrorException(null, "Error updating session data");
			}

			sessionData = getSessionData();
		} else {
			log.info("sessionData found: {}", sessionData);
		}

		List<RegisteredRequest> appSessions = CollectionUtil.mapToList(sessionData.getRegisteredApps());

		return appSessions;
	}

	
	private String generateRequestId() {
		return StringUtil.generateRandomNumber(17);
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
		sessionData.registerNewRequest(requestv2);
		return sessionData;
	}
}
