package com.fajar.shoppingmart.service;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.controller.BaseController;
import com.fajar.shoppingmart.dto.RegistryModel;
import com.fajar.shoppingmart.dto.SessionData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RuntimeService {

	public static final String PAGE_REQUEST = "page_req_id";

	public static final String PAGE_REQUEST_ID = "requestId";

	public static final String JSESSSIONID = "JSESSIONID";

//	@Autowired
//	private Registry registry;
	private final Map<String, Remote> registry = new LinkedHashMap<>();

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		set(UserSessionService.SESSION_DATA, new SessionData());
	}

	/**
	 * get remote object
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T extends Remote> T getModel(String key) {
		try {
			Remote object = registry.get(key);
			T finalObj = (T) object;
			log.info("==registry model: " + finalObj);
			return finalObj;
//		} catch (RemoteException | NotBoundException e) {
//			log.info("key not bound");
//			return null;
		} catch (Exception ex) {
			log.info("Unexpected error");
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * set registry remote object
	 * 
	 * @param key
	 * @param registryModel
	 * @return
	 */
	public boolean set(String key, Remote registryModel) {
		try {
//			if (getModel(key) == null) {
				registry.put(key, registryModel);
//			} else {
//				registry.rebind(key, registryModel);
//			}
			return true;
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (AlreadyBoundException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/**
	 * unbind remote object
	 * 
	 * @param key
	 * @return
	 */
	public boolean unbind(String key) {
		try {
			registry.remove(key);
			return true;
//		} catch (RemoteException | NotBoundException e) {
//			e.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return false;

	}

	/**
	 * register new page request to request list
	 * 
	 * @param cookie
	 * @return
	 */
	public String addPageRequest(String cookie) {
		String pageRequestId = UUID.randomUUID().toString();
		
		if (getModel(PAGE_REQUEST) != null) {
			
			RegistryModel model = (RegistryModel) getModel(PAGE_REQUEST);
			model.getTokens().put(pageRequestId, cookie);
			
			if (set(PAGE_REQUEST, model)) {
				return pageRequestId;
			}

		} else { 
			 
				RegistryModel	model = new  RegistryModel();
				model.setTokens(new HashMap<String, Object>() {{put(pageRequestId, cookie);}});  
				
				if (set(PAGE_REQUEST, model)) {
					return pageRequestId;
				} 
		}
		return null;

	}

	/**
	 * check page request against cookie jsessionID
	 * 
	 * @param req
	 * @return
	 */
	public boolean validatePageRequest(HttpServletRequest req) {
		log.info("Will validate page request");
		try {
			RegistryModel model = (RegistryModel) getModel(PAGE_REQUEST);

			if (null == model) {
				return false;
			}

			Cookie jsessionCookie = BaseController.getCookie(JSESSSIONID, req.getCookies());
			String pageRequestId = req.getHeader(PAGE_REQUEST_ID);
			boolean exist = model.getTokens().get(pageRequestId) != null;
			if (exist) {
				String reuqestIdValue = (String) model.getTokens().get(pageRequestId);

				log.info(" . . . . . Request ID value: " + reuqestIdValue + " vs JSessionId: "
						+ jsessionCookie.getValue());

				return reuqestIdValue.equals(jsessionCookie.getValue());
			} else {
				log.info("x x x x Request ID not found x x x x");
			}

			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
