package com.fajar.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.User;
import com.fajar.repository.UserRepository;

@Service
public class UserUpdateService extends BaseEntityUpdateService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public ShopApiResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		User user = (User) copyNewElement(baseEntity, newRecord);
		User newUser = userRepository.save(user);
		return ShopApiResponse.builder().entity(newUser).build();
	}
}
