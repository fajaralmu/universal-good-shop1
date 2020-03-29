package com.fajar.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Menu;
import com.fajar.repository.MenuRepository;

@Service
public class MenuUpdateService extends BaseEntityUpdateService{

	@Autowired
	private MenuRepository menuRepository; 
	 
	@Override
	public ShopApiResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		Menu menu = (Menu) copyNewElement(baseEntity, newRecord);
		String base64Image = menu.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage("MN", base64Image);
				menu.setIconUrl(imageName);
			} catch (IOException e) {

				menu.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Menu> dbMenu = menuRepository.findById(menu.getId());
				if (dbMenu.isPresent()) {
					menu.setIconUrl(dbMenu.get().getIconUrl());
				}
			}
		}
		Menu newMenu = menuRepository.save(menu);
		return ShopApiResponse.builder().entity(newMenu).build();
	}
}