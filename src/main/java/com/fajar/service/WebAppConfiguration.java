package com.fajar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.fajar.annotation.Dto;
import com.fajar.entity.ShopProfile;
import com.fajar.repository.ShopProfileRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this class is autowired via XML
 * @author Republic Of Gamers
 *
 */
@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebAppConfiguration {
	
	@Autowired
	private ShopProfileRepository shopProfileRepository;
	
	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath;
	private String martCode;
	
	 
	
	public ShopProfile getProfile() {
		ShopProfile dbProfile= shopProfileRepository.findByMartCode(martCode);
		if(null == dbProfile) {
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
		profile.setAbout("Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae.");
		return profile;
	}
	 
	
}
