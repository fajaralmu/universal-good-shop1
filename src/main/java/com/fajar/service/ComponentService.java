package com.fajar.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entity.Menu;
import com.fajar.repository.MenuRepository;

@Service
public class ComponentService {
	@Autowired
	private MenuRepository menuRepository;
	
	public List<Menu> getHomeMenus(HttpServletRequest request){
		List<Menu> menus =  menuRepository.findByPage("HOME");
		for (Menu menu : menus) {
			menu.setUrl(request.getContextPath()+menu.getUrl());
		}
		return menus;
	}

}
