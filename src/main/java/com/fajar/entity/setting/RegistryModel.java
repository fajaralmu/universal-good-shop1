package com.fajar.entity.setting;

import java.rmi.Remote;
import java.util.HashMap;

import com.fajar.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryModel implements Remote {
	private User user;
	private HashMap<String, Object> tokens;
	private String userToken;

}
