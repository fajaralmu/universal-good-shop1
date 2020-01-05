package com.fajar.service;

import java.lang.reflect.Field;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.dto.UserTempRequest;
import com.fajar.entity.RegisteredRequest;
import com.fajar.entity.User;
import com.fajar.entity.setting.RegistryModel;
import com.fajar.repository.RegisteredRequestRepository;
import com.fajar.repository.UserRepository;
import com.fajar.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RegisteredRequestRepository registeredRequestRepository;

	@Autowired
	private RegistryService registryService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public User getUserFromSession(HttpServletRequest request) {
		try {
			return (User) request.getSession(false).getAttribute("user");
		} catch (Exception ex) {
			return null;
		}
	}

	public User getUserFromRegistry(HttpServletRequest request) {
		String loginKey = request.getHeader("loginKey");
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
		removeAttribute(user, "role", "password");
		return user;
	}

	public boolean hasSession(HttpServletRequest request) {
		return hasSession(request, true);
	}

	public boolean hasSession(HttpServletRequest request, boolean setRequestURI) {
		if (setRequestURI) {

			request.getSession().setAttribute("requestURI", request.getRequestURI());
			log.info("---REQUESTED URI: " + request.getSession(false).getAttribute("requestURI"));
		}

		/**
		 * handle FE
		 */

		String remoteAddress = request.getRemoteAddr();
		int remotePort = request.getRemotePort();
		System.out.println("remoteAddress:" + remoteAddress + ":" + remotePort);
		if (request.getHeader("loginKey") != null) {
			boolean registered = getUserFromRegistry(request) != null;
			return registered;
		}

		/**
		 * end handle FE
		 */

		if (request.getSession().getAttribute("user") == null) {
			log.info("session user NULL");
			return false;
		}
		Object sessionObj = request.getSession().getAttribute("user");
		if (!(sessionObj instanceof User)) {
			log.info("session user NOT USER OBJECT =" + sessionObj.getClass());
			return false;
		}
		User sessionUser = (User) request.getSession().getAttribute("user");

		try {
			RegistryModel registryModel = registryService.getModel(sessionUser.getLoginKey().toString());

			if (sessionUser == null || registryModel == null || !sessionUser.equals(registryModel.getUser())) {
				System.out.println("==========USER NOT EQUALS==========");
				throw new Exception();
			}

			User loggedUser = userRepository.findByUsernameAndPassword(sessionUser.getUsername(),
					sessionUser.getPassword());

			System.out.println("USER HAS SESSION");
			return loggedUser != null;

		} catch (Exception ex) {
			System.out.println("USER DOSE NOT HAVE SESSION");
			ex.printStackTrace();
			return false;
		}
	}

	public User addUserSession(final User dbUser, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IllegalAccessException {
		RegistryModel registryModel = RegistryModel.builder().user(dbUser).userToken(UUID.randomUUID().toString())
				.build();

		try {
			String key = UUID.randomUUID().toString();
			dbUser.setLoginKey(key);
			boolean registryIsSet = registryService.set(key, registryModel);
			if (!registryIsSet) {
				throw new Exception();
			}

			httpResponse.addHeader("loginKey", key);
			httpResponse.addHeader("Access-Control-Expose-Headers", "*");
			httpRequest.getSession(true).setAttribute("user", dbUser);
			System.out.println(" > > > SUCCESS LOGIN :");
			return dbUser;
		} catch (Exception e) {
			System.out.println(" < < < FAILED LOGIN");
			throw new IllegalAccessException("Login Failed");
		}
	}

	public boolean logout(HttpServletRequest request) {
		User user = getUserFromSession(request);
		if (user == null) {
			user = getUserFromRegistry(request);
			if (user == null) {
				return false;
			}
		}
		try {
			boolean registryIsUnbound = registryService.unbind(user.getLoginKey().toString());

			if (!registryIsUnbound) {
				throw new Exception();
			}

			request.getSession(false).removeAttribute("user");
			request.getSession(false).invalidate();

			System.out.println(" > > > > > SUCCESS LOGOUT");
			return true;
		} catch (Exception e) {
			System.out.println(" < < < < < FAILED LOGOUT");
			e.printStackTrace();
		}
		return false;

	}

	public String getToken(User user) {
		RegistryModel reqModel = registryService.getModel(user.getLoginKey());
		String token = reqModel.getUserToken();
		return token;
	}

	public boolean validatePageRequest(HttpServletRequest req) {
		final String requestId = req.getHeader(RegistryService.PAGE_REQUEST_ID);
		System.out.println("Page request id: " + requestId);
		// check from DB
		RegisteredRequest registeredRequest = registeredRequestRepository.findTop1ByRequestId(requestId);

		if (registeredRequest != null) {
			System.out.println("x x x Found Registered Request: " + registeredRequest);
			return true;
		}
		System.out.println("Reuqest not registered");

		return registryService.validatePageRequest(req);
	}

	private static void removeAttribute(Object object, String... fields) {
		for (String fieldName : fields) {
			Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);
			try {
				field.setAccessible(true);
				field.set(object, null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				System.out.println("Error------- and catched");
				e.printStackTrace();
			}
		}
	}

	public ShopApiResponse getProfile(HttpServletRequest httpRequest) {

		User user = getUserFromRegistry(httpRequest);
		if (user != null) {
			removeAttribute(user, "role", "password");
		}
		return ShopApiResponse.builder().code("00").entity(user).build();
	}

}
