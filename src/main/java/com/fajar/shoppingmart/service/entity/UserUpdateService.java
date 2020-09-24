package com.fajar.shoppingmart.service.entity;

import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.User;

@Service
public class UserUpdateService extends BaseEntityUpdateService<User>{
 
	
	@Override
	public WebResponse saveEntity(User baseEntity, boolean newRecord) {
		try {
			User user = (User) copyNewElement(baseEntity, newRecord);
			this.validateEntityFields(user, newRecord);
			User newUser = entityRepository.save(user);
			return WebResponse.builder().success(true).entity(newUser).build();
		}catch (Exception e) {
			return WebResponse.builder().success(false).entity(null).build();
		}
	}
}
