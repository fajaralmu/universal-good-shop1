package com.fajar.shoppingmart.service.runtime;

import static com.fajar.shoppingmart.util.SessionUtil.PAGE_REQUEST;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.controller.BaseController;
import com.fajar.shoppingmart.dto.SessionData;
import com.fajar.shoppingmart.dto.UserSessionModel;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.UserSessionService;
import com.fajar.shoppingmart.util.SessionUtil;
import com.fajar.shoppingmart.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RuntimeService {
 
//	private final Map<String, Serializable> SESSION_MAP = new LinkedHashMap<>();
	
	@Autowired
	private TempSessionService tempSessionService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		set(UserSessionService.SESSION_DATA, new SessionData());
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T getModel(String key, Class<T> _class) {
		try {
			Serializable serializable = tempSessionService.get(key, _class);
			T finalObj = (T) serializable;

			log.info("==registry model: " + finalObj);
			return finalObj;

		} catch (Exception e ) {
			log.error("runtime data error");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param key
	 * @param registryModel
	 * @return
	 */
	public boolean set(String key, Serializable value) {
		try {
			tempSessionService.put(key, value);
			return true;
		} catch (Exception e) {
			log.error("runtime data error");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		try {
			tempSessionService.remove(key);
			return true;
		} catch (Exception e) {
			log.error("runtime data error");
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * @param value
	 * @return
	 */
	public String addPageRequest(String value) {
		String pageRequestId = generateIdKey();

		UserSessionModel model;
		if (getModel(PAGE_REQUEST, UserSessionModel.class) != null) {

			model = getModel(PAGE_REQUEST, UserSessionModel.class);
			model.getTokens().put(pageRequestId, value);

		} else {

			model = new UserSessionModel();
			model.setTokens(new HashMap<String, Object>() {
				{
					put(pageRequestId, value);
				}
			});
		}
		if (set(PAGE_REQUEST, model)) {
			return pageRequestId;
		} else {
			return null;
		}

	}

	private String generateIdKey() {

		return StringUtil.generateRandomNumber(15);
	}

	public void updateSessionId(String newSessionId, String requestId) {
		try {
			((UserSessionModel) getModel(PAGE_REQUEST, UserSessionModel.class)).getTokens().put(requestId, newSessionId);
			log.info("SessionID UPDATED!!");
			
		} catch (Exception e) {
			log.error("runtime data error");
			e.printStackTrace();
		}
	}

	/**
	 * check page request against cookie jsessionID
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	public boolean validatePageRequest(HttpServletRequest httpServletRequest) {
		log.info("Will validate page request");

		try {
			UserSessionModel model =  getModel(PAGE_REQUEST, UserSessionModel.class);

			if (null == model) {
				log.debug("MODEL IS NULL");
				return false;
			}

			Cookie jsessionCookie = BaseController.getJSessionIDCookie(httpServletRequest);
			String pageRequestId = SessionUtil.getPageRequestId(httpServletRequest);

			boolean exist = model.getTokens().get(pageRequestId) != null;

			if (exist) {
				String sessionId = (String) model.getTokens().get(pageRequestId);

				boolean requestIdMatchCookie = sessionId.equals(jsessionCookie.getValue());

				log.debug("sessionId value: {} vs JSessionId cookie: {}", sessionId, jsessionCookie.getValue());
				log.debug("sessionIdMatchCookie: {}", requestIdMatchCookie);

				return requestIdMatchCookie;
			} else {
				log.debug("x x x x Request ID not found x x x x");
				return false;
			}
		} catch (Exception e ) {
			log.error("runtime data error");
			e.printStackTrace();
			return false;
		}
	}

	public boolean createNewSessionData() { 
		return set(UserSessionService.SESSION_DATA, new SessionData());
	}

}
