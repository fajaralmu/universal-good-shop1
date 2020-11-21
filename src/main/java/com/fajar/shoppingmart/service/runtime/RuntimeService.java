package com.fajar.shoppingmart.service.runtime;

import static com.fajar.shoppingmart.util.SessionUtil.PAGE_REQUEST;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.controller.BaseController;
import com.fajar.shoppingmart.dto.SessionData;
import com.fajar.shoppingmart.dto.UserSessionModel;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.sessions.SessionValidationService;
import com.fajar.shoppingmart.util.MapUtil;
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
		set(SessionValidationService.SESSION_DATA, new SessionData());
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
			log.error("set runtime data error");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean remove(String key, Class<? extends Serializable> _class) {
		try {
			tempSessionService.remove(key, _class);
			return true;
		} catch (Exception e) {
			log.error("runtime data error");
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * @param cookieValue
	 * @return
	 */
	public String addPageRequest(String cookieValue) {
		String pageRequestId = generateIdKey();

		UserSessionModel model;
		if (getModel(PAGE_REQUEST, UserSessionModel.class) != null) {

			model = getModel(PAGE_REQUEST, UserSessionModel.class);
			model.addPageCookies(pageRequestId, cookieValue);
			log.info("page_request_data_holder exist");
		} else {

			model = new UserSessionModel();
			model.addPageCookies(pageRequestId, cookieValue);
			log.info("create new page_request_data_holder");
		}
		if (set(PAGE_REQUEST, model)) {
			log.info("GENERATED pageRequestId: {}", pageRequestId);
			return pageRequestId;
		} else {
			log.error("ERROR GENERATING PAGE REQUEST ID");
			return null;
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
				log.info("MODEL IS NULL");
				return false;
			}

			Cookie jsessionCookie = BaseController.getJSessionIDCookie(httpServletRequest);
			String pageRequestId = SessionUtil.getPageRequestId(httpServletRequest);

			boolean exist = model.hasCookie(pageRequestId);

			if (exist) {
				String savedCookie = (String) model.getCookie(pageRequestId);

				boolean requestIdMatchCookie = savedCookie.equals(jsessionCookie.getValue());

				log.info("sessionId value: {} vs JSessionId cookie: {}", savedCookie, jsessionCookie.getValue());
				log.info("sessionIdMatchCookie: {}", requestIdMatchCookie);

				return requestIdMatchCookie;
			} else {
				log.info("x x x x Request ID not found x x x x");
				return false;
			}
		} catch (Exception e ) {
			log.error("runtime data error");
			e.printStackTrace();
			return false;
		}
	}

	public boolean createNewSessionData() { 
		return  set(SessionValidationService.SESSION_DATA, new SessionData());
	}
	
	private String generateIdKey() {

		return StringUtil.generateRandomNumber(15);
	}
	
	public UserSessionModel getUserSessionModel(String key) {
		return this.getModel(key, UserSessionModel.class);
	}

}
