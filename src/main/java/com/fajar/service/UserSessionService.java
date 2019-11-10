package com.fajar.service;

import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.UserTempRequest;
import com.fajar.entity.User;
import com.fajar.entity.setting.RegistryModel;
import com.fajar.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private Registry registry;

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
		if (/* request.getUserPrincipal() == null || */request.getSession().getAttribute("user") == null) {
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
			RegistryModel registryModel = (RegistryModel) registry.lookup(sessionUser.getId().toString());
			System.out.println("Registry Model: "+registryModel);
			User loggedUser = userRepository.findByUsernameAndPassword(sessionUser.getUsername(),
					sessionUser.getPassword());

			return loggedUser != null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void addUserSession(User dbUser, HttpServletRequest httpRequest) {
		RegistryModel registryModel = RegistryModel.builder().user(dbUser).build();
		
		try {
			
			registry.bind(dbUser.getId().toString(), registryModel);
			httpRequest.getSession(true).setAttribute("user", dbUser);
		 
			System.out.println(" > > > SUCCESS LOGIN :"+httpRequest.getAuthType());
		} catch (Exception e) { 
			System.out.println(" < < < FAILED LOGIN");
			e.printStackTrace();
		}
		setToken(dbUser);
		WebConfigService.putUserTempData(dbUser.getId().toString(), UserTempRequest.builder().user(dbUser)
				.requestURI(httpRequest.getRequestURI()).userId(dbUser.getId()).build());
	}

	public void logout(HttpServletRequest request) {
		User user = getUser(request);
		try {
			userTokens.remove(user.getId().toString());
			registry.unbind(user.getId().toString());
			request.getSession(false).removeAttribute("user");
			request.getSession(false).invalidate();
//			request.logout();
			System.out.println(" > > > > > SUCCESS LOGOUT");
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			System.out.println(" < < < < < FAILED LOGOUT");
			e.printStackTrace();
		}
		
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
