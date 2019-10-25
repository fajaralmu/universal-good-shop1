package com.fajar.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.User;
import com.fajar.entity.UserRole;
import com.fajar.repository.UserRepository;
import com.fajar.repository.UserRoleRepository;

@Service
public class AccountService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private UserSessionService userSessionService;

	public ShopApiResponse registerUser(ShopApiRequest request) {
		// TODO Auto-generated method stub
		ShopApiResponse response  = new ShopApiResponse();
		UserRole regularRole = userRoleRepository.findById(2L).get();
		User user = new User();
		user.setDisplayName(request.getUser().getDisplayName());
		user.setDeleted(false);
		user.setRole(regularRole);
		user.setPassword(request.getUser().getPassword());
		user.setUsername(request.getUser().getUsername());
		try {
			User newUser = userRepository.save(user);
			response.setUser(newUser);
			return response;
		}catch (Exception e) {
			response.setCode("01");
			response.setMessage("Error Record new Data");
			return response;
		}
	}

	public ShopApiResponse login(ShopApiRequest request, HttpServletRequest httpRequest) {
		User dbUser = userRepository.findByUsernameAndPassword(request.getUser().getUsername(), request.getUser().getPassword());
		 
		if(dbUser == null) {
			return new ShopApiResponse("01","invalid credential");
		}
		userSessionService.addUserSession(dbUser,httpRequest);
		return new ShopApiResponse("00","success");
	}

}
