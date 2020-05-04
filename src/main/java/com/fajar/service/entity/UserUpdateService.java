package com.fajar.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.User;
import com.fajar.repository.UserRepository;

@Service
public class UserUpdateService extends BaseEntityUpdateService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		User user = (User) copyNewElement(baseEntity, newRecord);
		User newUser = userRepository.save(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
