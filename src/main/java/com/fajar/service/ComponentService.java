package com.fajar.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entity.BaseEntity;
import com.fajar.entity.Category;
import com.fajar.entity.Menu;
import com.fajar.entity.Page;
import com.fajar.entity.User;
import com.fajar.repository.CategoryRepository;
import com.fajar.repository.MenuRepository;
import com.fajar.repository.PageRepository;
import com.fajar.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComponentService { 
	
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private PageRepository pageRepository;

	public List<Page> getPages(HttpServletRequest request){
		
		boolean hasSession = userSessionService.hasSession(request);
		
		if(hasSession)
			return pageRepository.findAll();
		else
			return pageRepository.findByAuthorized(0);
	}
	
	/**
	 * get page code
	 * @param request
	 * @return
	 */
	public String getPageCode(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String link = uri.replace(request.getContextPath(), "");
		
		log.info("link: {}", link);
		Page page = pageRepository.findTop1ByLink(link);
		
		log.info("page from db : {}", page);
		if(null == page) {
			return "";
		}
		
		log.info("page code found: {}", page.getCode());
		return page.getCode();
	}
	
	public List<Page> getAllPages() { 
		return pageRepository.findAll(); 
	}
	
	public List<Menu> getDashboardMenus(HttpServletRequest request) {
		List<Menu> menus = menuRepository.findByPageStartsWith("HOME");
		List<BaseEntity> entities = new ArrayList<BaseEntity>();
		menus = getAvailableMenusForUser(userSessionService.getUserFromSession(request), menus);
		for (Menu menu : menus) {
			menu.setUrl(request.getContextPath() + menu.getUrl());
			entities.add(menu);
		}
		return EntityUtil.validateDefaultValue(entities);
	}
	
	public Page getPage(String code, HttpServletRequest request) { 
		Page page = pageRepository.findByCode(code); 
		
		if (page.getAuthorized() == 1 && !userSessionService.hasSession(request)) {
			
			return null;
		}
		
		List<Menu> menus = getMenuByPageCode(code);
		page.setMenus(menus );
		return page;
	}

	public List<Menu > getMenuByPageCode(String pageCode){
		
		List<Menu> menus = menuRepository.findByMenuPage_code(pageCode);
		EntityUtil.validateDefaultValues(menus);
		return menus;
	}
	
	 
	private boolean hasAccess(User user, String menuAccess) {
		boolean hasAccess = false;
		
		for (String userAccess : user.getRole().getAccess().split(",")) {
			if (userAccess.equals(menuAccess)) {
				hasAccess = true;
				break;
			}
		}

		return hasAccess;
	}

	private List<Menu> getAvailableMenusForUser(User user, List<Menu> menus) {
		List<Menu> newMenus = new ArrayList<>();
		
		for (Menu menu : menus) {
			String[] menuAccess = menu.getPage().split("-");
			if (menuAccess.length <= 1) {
				newMenus.add(menu);
				continue;
			} else if (hasAccess(user, menuAccess[1])) {
				newMenus.add(menu);
				continue;
			}

		}
		return newMenus;
	}

	 

	public List<Category> getAllCategories() {
		return categoryRepository.findByDeletedFalse();
	}

	public void checkAccess(User user, String url) throws Exception {
		Menu menu = menuRepository.findTop1ByUrl(url);
		if (menu == null) {
			throw new Exception("Not Found");

		}
		String[] menuAccess = menu.getPage().split("-");
		if (menuAccess.length > 1) {
			String access 			= menuAccess[1];
			String[] userAccesses 	= user.getRole().getAccess().split(",");
			boolean hasAccess 		= hasAccess(user, access);
			if (!hasAccess) {
				throw new Exception("Has No Access");
			}
		}

	}

	

}
