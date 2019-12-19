package com.fajar.service;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.controller.BaseController;
import com.fajar.entity.setting.RegistryModel;

@Service
public class RegistryService {

	public static final String PAGE_REQUEST = "page_req_id";

	public static final String PAGE_REQUEST_ID = "requestId";

	public static final String JSESSSIONID = "JSESSIONID";

	@Autowired
	private Registry registry;

	public <T> T getModel(String key) {
		try {
			T object = (T) registry.lookup(key);
			System.out.println("==registry model: " + object);
			return object;
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public boolean set(String key, Remote registryModel) {
		try {
			if (getModel(key) == null) {
				registry.bind(key, registryModel);
			} else {
				registry.rebind(key, registryModel);
			}
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean unbind(String key) {
		try {
			registry.unbind(key);
			return true;
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return false;

	}

	public String addPageRequest(String cookie) {
		String pageRequestId = UUID.randomUUID().toString();
		if (getModel(PAGE_REQUEST) != null) {
			RegistryModel model = getModel(PAGE_REQUEST);
			model.getTokens().put(pageRequestId, cookie);
			if (set(PAGE_REQUEST, model)) {
				return pageRequestId;
			}

		} else {
			RegistryModel model = RegistryModel.builder().tokens(new HashMap<>()).build();
			model.getTokens().put(pageRequestId, cookie);
			if (set(PAGE_REQUEST, model)) {
				return pageRequestId;
			}
		}
		return null;

	}

	public boolean validatePageRequest(HttpServletRequest req) {
		System.out.println("Will validate page request");
		try {
			RegistryModel model = getModel(PAGE_REQUEST);

			if (null == model) { return false; }

			Cookie jsessionCookie = BaseController.getCookie(JSESSSIONID, req.getCookies());
			String pageRequestId = req.getHeader(PAGE_REQUEST_ID);
			boolean exist = model.getTokens().get(pageRequestId) != null;
			if (exist) {
				String reuqestIdValue = (String) model.getTokens().get(pageRequestId);

				System.out.println(" . . . . . Request ID value: " + reuqestIdValue + " vs JSessionId: "
						+ jsessionCookie.getValue());

				return reuqestIdValue.equals(jsessionCookie.getValue());
			} else {
				System.out.println("x x x x Request ID not found x x x x");
			}

			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
