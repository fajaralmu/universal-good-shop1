package com.fajar.shoppingmart.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Menu;
import com.fajar.shoppingmart.entity.Page;
import com.fajar.shoppingmart.entity.Sequenced;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.UserRole;
import com.fajar.shoppingmart.entity.setting.EntityManagementConfig;
import com.fajar.shoppingmart.repository.CategoryRepository;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.repository.MenuRepository;
import com.fajar.shoppingmart.repository.PageRepository;
import com.fajar.shoppingmart.service.entity.EntityValidation;
import com.fajar.shoppingmart.service.sessions.SessionValidationService;
import com.fajar.shoppingmart.service.sessions.UserSessionService;
import com.fajar.shoppingmart.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComponentService { 
	
	private static final String SETTING = "setting";
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private UserAccountService userAccountService;
	@Autowired
	private PageRepository pageRepository; 
	@Autowired
	private EntityRepository entityRepository;

	public List<Page> getPages(HttpServletRequest request){
		
		boolean hasSession = sessionValidationService.hasSession(request);
		
		if(hasSession)
			return pageRepository.findByOrderBySequenceAsc();
		else
			return pageRepository.findByAuthorizedOrderBySequenceAsc(0);
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
	
 
	
	public Page getPage(String code, HttpServletRequest request) { 
		Page page = pageRepository.findByCode(code); 
		
		if (page.getAuthorized() == 1 && !sessionValidationService.hasSession(request)) {
			
			return null;
		}
		
		List<Menu> menus = getMenuListByPageCode(code);
		page.setMenus(menus );
		return page;
	} 
	
	public WebResponse getMenuByPageCode(String pageCode) {

		List<Menu> menus = getMenuListByPageCode(pageCode);

		return WebResponse.builder().entities(CollectionUtil.convertList(menus)).build();
	}
	
	private Menu defaultMenu() {
		Menu menu = new Menu();
		menu.setCode("000");
		menu.setName("Menu Management");
		menu.setUrl("/management/menu");
		Page menuPage = pageRepository.findByCode(SETTING);
		menu.setMenuPage(menuPage);
		return menu;
	}
	
	public List<Menu> getMenuListByPageCode(String pageCode) {

		List<Menu> menus = menuRepository.findByMenuPage_code(pageCode);

		if (menus == null || menus.size() == 0) {

			if (pageCode.equals(SETTING)) {
				Menu menu = defaultMenu();
				final Menu savedMenu = entityRepository.save(menu);
				return CollectionUtil.listOf(savedMenu);			
			}
		}

		EntityValidation.validateDefaultValues(menus, entityRepository);
		return menus;
	}
	 
	private boolean hasAccess(User user, String menuAccess) {
		UserRole userRole = userAccountService.getRole(user);
		boolean hasAccess = false;
		
		for (String userAccess : userRole.getAccess().split(",")) {
			if (userAccess.equals(menuAccess)) {
				hasAccess = true;
				break;
			}
		}

		return hasAccess;
	}

 

	 

	public List<Category> getAllCategories() {
		return categoryRepository.findByDeletedFalse();
	}

 

	public WebResponse saveEntitySequence(WebRequest request, String entityName) {

		List<BaseEntity> orderedEntities = request.getOrderedEntities();
		EntityManagementConfig entityConfig = entityRepository.getConfig(entityName);
		Class<? extends BaseEntity> cls = entityConfig.getEntityClass();
		try {

			for (int i = 0; i < orderedEntities.size(); i++) {
				BaseEntity page = orderedEntities.get(i);
				updateSequence(i, page.getId(), cls);
			}

			WebResponse response = WebResponse.success();
			return response;

		} catch (Exception e) {
			log.error("Error saving page sequence");
			e.printStackTrace();
			return WebResponse.failed(e.getMessage());
		}
	}

	private void updateSequence(int sequence, Long id, Class<? extends BaseEntity> cls) {
		
		final BaseEntity dbRecord = entityRepository.findById(cls, id);
		if (dbRecord != null) {
			 
			((Sequenced)dbRecord).setSequence(sequence);
			entityRepository.save(dbRecord);
		}
	} 

	

}
