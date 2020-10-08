package com.fajar.shoppingmart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserDetailServiceTools implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	public UserDetailServiceTools() {
		log.info("========== UserDetailServiceTools ==========");
	}
	@Override
	public UserDetails loadUserByUsername(String username) {
		log.info("&&&&&&&&&&&&& loadUserByUsernameL {} &&&&&&&&&&&&&", username);
		User user = userRepository.findByUsername(username);
		
		log.info("Found user: {}", user);
		return user;
	}
}