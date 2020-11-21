package com.fajar.shoppingmart.service.sessions;

import static com.fajar.shoppingmart.util.SessionUtil.getLoginKey;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.SessionData;
import com.fajar.shoppingmart.dto.UserSessionModel;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.UserRepository;
import com.fajar.shoppingmart.service.runtime.RuntimeService;
import com.fajar.shoppingmart.util.JwtUtil;
import com.fajar.shoppingmart.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserSessionServiceImpl implements UserSessionService {
	
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private UserRepository userRepository;

	@Override
	public User getUserFromSession(HttpServletRequest request) {
		
		try {
			return SessionUtil.getSessionUser(request);
		} catch (Exception ex) { return null; }
	}

	@Override
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

	@Override
	public User addUserSession(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception{
		
		try {
			User dbUser = this.getUserByUsernameAndPassword(request);
			dbUser.setLoginKeyAndPasswordNull(randomLoginKey()); 
			String requestId = SessionUtil.getPageRequestId(httpRequest);
			if(null == requestId) {
				throw new RuntimeException("Invalid Request ID");
			}

			UserSessionModel storedUserSessionModel = setNewUserSessionModel(dbUser , requestId);  

			if (null == storedUserSessionModel) {
				throw new RuntimeException("Error saving session");
			}

			SessionUtil.setLoginKeyHeader(httpResponse,  storedUserSessionModel.getJwt());
			SessionUtil.setAccessControlExposeHeader(httpResponse);
			SessionUtil.setSessionUser(httpRequest, storedUserSessionModel.getUser());

			log.info("success login");

			return storedUserSessionModel.getUser();
		} catch (Exception e) {

			e.printStackTrace();
			log.error("failed login");
			throw new IllegalAccessException("Login Failed: "+e);
		}
	}

	@Override
	public boolean removeUserSession(HttpServletRequest httpRequest) {
		
		try {
			invalidateSessionUser(httpRequest);
			log.info("success logout");
			return true;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			log.info("failed logout");
			return false;
		}
	}
	
	/////////////////// privates ///////////////////////////
	
	private User getUserFromRuntime(HttpServletRequest request) {
		String loginKey = SessionUtil.getLoginKey(request);
		UserSessionModel sessionModel = runtimeService.getUserSessionModel(loginKey);

		if (sessionModel == null) { return null; } 
		return sessionModel.getUser();
	}
	
	private User getUserByUsernameAndPassword(WebRequest request) {
		User userFromRequest = request.getUser();
		User userFromDatabase = userRepository.findByUsername(userFromRequest.getUsername());

		if (userFromDatabase != null) {
			log.info("username: {} exist", userFromDatabase.getUsername());
		} else {
			log.error("username: {} does not exist", userFromRequest.getUsername());
			return null;
		}

		boolean passwordMatched = comparePassword(userFromDatabase, userFromRequest.getPassword());
		log.info("Logged User Role: {}", userFromDatabase.getRole());
		
		return passwordMatched ? userFromDatabase : null;
	}
	
	private boolean comparePassword(User dbUser, String password) {
		if (null == password || dbUser == null) {
			return false;
		}

		boolean match = password.equals(dbUser.getPassword());
		log.info("Password match: {}", match);

		return match;
	}
	
	private UserSessionModel setNewUserSessionModel(User user, String requestId) {
		user.setRequestId(requestId);
		UserSessionModel sessionModel = new UserSessionModel(user, user.getLoginKey(), requestId); 
		String jwtKey = generateJwt(sessionModel);
		sessionModel.setJwt(jwtKey);
		boolean result = runtimeService.set(user.getLoginKey(), sessionModel);
		log.info("SET NEW USER SESSION MODEL TO TEMP DATA:{}", result);
		
		return result ? sessionModel : null;
	}
	
	private void invalidateSessionUser(HttpServletRequest request) {
		User user = getLoggedUser(request);
		removeUserFromRuntime(user);
		SessionUtil.removeSessionUserAndInvalidate(request);
	}
	
	private void removeUserFromRuntime(User user) {
		runtimeService.remove(user.getLoginKey(), UserSessionModel.class);
	}
	
	private String generateJwt(UserSessionModel dbUser) {
		return JwtUtil.generateJWT(new SessionData(), dbUser);
	}

	private String randomLoginKey() {
		return UUID.randomUUID().toString();
	}
	
	

}
