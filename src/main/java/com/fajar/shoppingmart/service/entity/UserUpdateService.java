package com.fajar.shoppingmart.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.UserRepository;

@Service
public class UserUpdateService extends BaseEntityUpdateService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord,EntityUpdateInterceptor entityUpdateInterceptor) {
		User user = (User) copyNewElement(baseEntity, newRecord);
		User newUser = userRepository.save(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
