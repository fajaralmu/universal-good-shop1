package com.fajar.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.UserTempRequest;
import com.fajar.entity.User;
import com.fajar.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	@Autowired
	private UserRepository userRepository;

	private Map<String, Object> userTokens = new HashMap<String, Object>();

	public User getUser(HttpServletRequest request) {
		try {
			return (User) request.getSession(false).getAttribute("user");
		} catch (Exception ex) {
			return null;
		}
	}
	
	public boolean hasSession(HttpServletRequest request) {
		return hasSession(request, true);
	}

	public boolean hasSession(HttpServletRequest request, boolean setRequestURI) {
		if(setRequestURI) {
			 
			request.getSession().setAttribute("requestURI", request.getRequestURI());
			log.info("---REQUESTED URI: "+request.getSession(false).getAttribute("requestURI"));
		}
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
			User loggedUser = userRepository.findByUsernameAndPassword(sessionUser.getUsername(),
					sessionUser.getPassword());

			return loggedUser != null;
		} catch (Exception ex) {
			return false;
		}
	}

	public void addUserSession(User dbUser, HttpServletRequest httpRequest) {
		httpRequest.getSession(true).setAttribute("user", dbUser);
		setToken(dbUser);
		WebConfigService.putUserTempData(dbUser.getId().toString(), UserTempRequest.builder().user(dbUser)
				.requestURI(httpRequest.getRequestURI()).userId(dbUser.getId()).build());
	}

	public void logout(HttpServletRequest request) {
		User user = getUser(request);
		userTokens.remove(user.getId().toString());
		request.getSession(false).removeAttribute("user");
	}

	private void setToken(User dbUser) {
		userTokens.put(dbUser.getId().toString(), UUID.randomUUID().toString());

	}

	public String getToken(User user) {
		try {
			return (String) userTokens.get(user.getId().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
