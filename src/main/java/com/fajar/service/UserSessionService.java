package com.fajar.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entity.User;
import com.fajar.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	@Autowired
	private UserRepository userRepository;
	
	public User getUser(HttpServletRequest request) {
		return (User) request.getSession(false).getAttribute("user");
	}

	public boolean hasSession(HttpServletRequest request) {
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
		User loggedUser = userRepository.findByUsernameAndPassword(sessionUser.getUsername(),
				sessionUser.getPassword());

		return loggedUser != null;
	}

	public void addUserSession(User dbUser, HttpServletRequest httpRequest) {
		httpRequest.getSession(true).setAttribute("user", dbUser);
		
	}

	public void logout(HttpServletRequest request) {
		 request.getSession(false).removeAttribute("user");
	}

}
