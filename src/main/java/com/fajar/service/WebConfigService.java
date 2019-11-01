package com.fajar.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fajar.annotation.Dto;
import com.fajar.dto.UserTempRequest;
import com.fajar.entity.ShopProfile;
import com.fajar.repository.ShopProfileRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this class is autowired via XML
 * 
 * @author Republic Of Gamers
 *
 */
@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebConfigService {

	@Autowired
	private ShopProfileRepository shopProfileRepository;

	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath;
	private String martCode;

	private static Map<String, UserTempRequest> userTemporaryData;

	public ShopProfile getProfile() {
		ShopProfile dbProfile = shopProfileRepository.findByMartCode(martCode);
		if (null == dbProfile) {
			return defaultProfile();
		}
		return dbProfile;
	}

	private ShopProfile defaultProfile() {
		ShopProfile profile = new ShopProfile();
		profile.setName("Universal Good Shop");
		profile.setAddress("Spring Mvc, Java Virtual Machine, Win 10 64");
		profile.setContact("087737666614");
		profile.setWebsite("http://localhost:8080/universal-good-shop");
		profile.setIconUrl("DefaultIcon.BMP");
		profile.setMartCode(martCode);
		profile.setAbout(
				"Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae.");
		return profile;
	}

	public static UserTempRequest getUserTemporaryData(String key) {
		if (userTemporaryData == null) {
			userTemporaryData = new HashMap<>();
		}
		return userTemporaryData.get(key);
	}

	public static  Map<String, UserTempRequest> putUserTempData(String key, UserTempRequest userData) {
		if (userTemporaryData == null) {
			userTemporaryData = new HashMap<>();
		}
		userTemporaryData.put(key, userData);
		return userTemporaryData;
	}

}
