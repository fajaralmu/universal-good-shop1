package com.fajar.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entity.BaseEntity;
import com.fajar.entity.Category;
import com.fajar.entity.Menu;
import com.fajar.repository.CategoryRepository;
import com.fajar.repository.MenuRepository;
import com.fajar.util.EntityUtil;

@Service
public class ComponentService {
	@Autowired
	private MenuRepository menuRepository; 
	@Autowired
	private CategoryRepository categoryRepository;
	
	public Object getDashboardMenus(HttpServletRequest request){
		List<Menu> menus =  menuRepository.findByPage("HOME");
		List<BaseEntity> entities = new ArrayList<BaseEntity>();
		for (Menu menu : menus) {
			menu.setUrl(request.getContextPath()+menu.getUrl());
			entities.add(menu);
		}
		return EntityUtil.validateDefaultValue(entities);
	}

	public Object getManagementMenus(HttpServletRequest request) {
		List<Menu> menus =  menuRepository.findByPage("MNGMNT");
		List<BaseEntity> entities = new ArrayList<BaseEntity>();
		for (Menu menu : menus) {
			menu.setUrl(request.getContextPath()+menu.getUrl());
			entities.add(menu);
		}
		return EntityUtil.validateDefaultValue(entities);
	}

	public Object getTransactionMenus(HttpServletRequest request) {
		List<Menu> menus =  menuRepository.findByPage("TRX");
		List<BaseEntity> entities = new ArrayList<BaseEntity>();
		for (Menu menu : menus) {
			menu.setUrl(request.getContextPath()+menu.getUrl());
			entities.add(menu);
		}
		return EntityUtil.validateDefaultValue(entities);
	}

	public Object getPublicMenus(HttpServletRequest request) {
		List<Menu> menus =  menuRepository.findByPage("PUBLIC");
		List<BaseEntity> entities = new ArrayList<BaseEntity>();
		for (Menu menu : menus) {
			menu.setUrl(request.getContextPath()+menu.getUrl());
			entities.add(menu);
		}
		return EntityUtil.validateDefaultValue(entities);
	}
	
	public List<Category> getAllCategories(){
		return categoryRepository.findByDeletedFalse();
	}

}
