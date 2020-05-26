package com.fajar.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.ShopProfile;
import com.fajar.repository.ShopProfileRepository;

@Service
public class ShopProfileUpdateService extends BaseEntityUpdateService{

	@Autowired
	private ShopProfileRepository shopProfileRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord,EntityUpdateInterceptor entityUpdateInterceptor) {
		ShopProfile shopProfile = (ShopProfile) copyNewElement(baseEntity, newRecord);
		String base64Image = shopProfile.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
				shopProfile.setIconUrl(imageName);
			} catch (IOException e) {

				shopProfile.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<ShopProfile> dbShopProfile = shopProfileRepository.findById(shopProfile.getId());
				if (dbShopProfile.isPresent()) {
					shopProfile.setIconUrl(dbShopProfile.get().getIconUrl());
				}
			}
		}
		ShopProfile newShopProfile = shopProfileRepository.save(shopProfile);
		return WebResponse.builder().entity(newShopProfile).build();
	}
	
}

