package com.fajar.controller;

import static com.fajar.util.MvcUtil.constructCommonModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.ListUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entity.BaseEntity;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.Category;
import com.fajar.entity.Cost;
import com.fajar.entity.CostFlow;
import com.fajar.entity.Customer;
import com.fajar.entity.Menu;
import com.fajar.entity.Message;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.RegisteredRequest;
import com.fajar.entity.ShopProfile;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.Unit;
import com.fajar.entity.User;
import com.fajar.entity.UserRole;
import com.fajar.entity.setting.EntityProperty;
import com.fajar.service.ComponentService;
import com.fajar.service.EntityManagementPageService;
import com.fajar.service.EntityService;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;
import com.fajar.util.CollectionUtil;
import com.fajar.util.EntityUtil;
import com.fajar.util.MyJsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("management")
public class MvcManagementController extends BaseController {

	@Autowired
	private UserSessionService userService;
	@Autowired
	private EntityService entityService;
	@Autowired
	private WebConfigService webAppConfiguration;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private EntityManagementPageService entityManagementPageService;

	private static String basePage;
	private static final String ERROR_404_PAGE = "error/notfound";

	public MvcManagementController() {
		log.info("-----------------Mvc Management Controller------------------");
	}

	@PostConstruct
	private void init() {
		basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}
	
	@RequestMapping(value = { "/common/{name}" })
	public String unit(@PathVariable("name")String name, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/common/"+name);
		} catch (Exception e) {
			return ERROR_404_PAGE;
		} 
		model = entityManagementPageService.setModel(request, model, name);
		return basePage;
	}

	
 

	@RequestMapping(value = { "/profile" })
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/profile");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(ShopProfile.class, null); 
		
		model = constructCommonModel(request, entityProperty, model, "shopProfile", "management");
		// override singleObject
		model.addAttribute("entityId", webAppConfiguration.getProfile().getId());
		model.addAttribute("singleRecord", true);
		return basePage;
	}

	@RequestMapping(value = { "/menu" })
	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<BaseEntity>> listObject = new HashMap<>();
		listObject.put("page", CollectionUtil.convertList(componentService.getAllPages()));
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Menu.class, listObject); 
		model = constructCommonModel(request, entityProperty, model, "Menu", "management");
		return basePage;
	}
	
	@RequestMapping(value = { "/costflow" })
	public String costflow(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/costflow");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<BaseEntity>> listObject = new HashMap<>();
		listObject.put("cost", CollectionUtil.convertList(entityService.getAllCostType()));
		EntityProperty entityProperty = EntityUtil.createEntityProperty(CostFlow.class, listObject); 
		model = constructCommonModel(request, entityProperty, model, "CostFlow", "management");
		return basePage;
	}

	@RequestMapping(value = { "/capitalflow" })
	public String capitalflow(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/capitalflow");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<BaseEntity>> listObject = new HashMap<String, List<BaseEntity>>() {
			{
				 put("capital", CollectionUtil.convertList(entityService.getAllCapitalType()));
			}
		}; 
		EntityProperty entityProperty = EntityUtil.createEntityProperty(CapitalFlow.class, listObject); 
		
		model = constructCommonModel(request, entityProperty, model, "CapitalFlow", "management");
		return basePage;
	}

	/** RESTRICTED ACCESS **/

	@RequestMapping(value = { "/messages" })
	public String messages(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/messages");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Message.class, null); 
		entityProperty.setEditable(false);
		entityProperty.removeElements("color", "fontColor");
		System.out.println("================ELEMENTS:"+MyJsonUtil.listToJson(entityProperty.getElements()));
		model = constructCommonModel(request,entityProperty, model, "message", "management");
		return basePage;
	}
	
//	@RequestMapping(value = { "/productFlow" })
//	public String productflow(Model model, HttpServletRequest request, HttpServletResponse response)
//			throws IOException {
//
//		if (!userService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		try {
//			checkUserAccess(userService.getUserFromSession(request), "/management/productFlow");
//		} catch (Exception e) {
//			return ERROR_404_PAGE;
//		}
//		EntityProperty entityProperty = EntityUtil.createEntityProperty(ProductFlow.class, null); 
//		model = constructCommonModel(request,entityProperty, model, "productFlow", "management");
//		return basePage;
//	}

	@RequestMapping(value = { "/transaction", "/transaction/{option}" })
	public String transaction(@PathVariable(required = false) String option, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/transaction");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Transaction.class, null);
		entityProperty.setEditable(false);
		entityProperty.setWithDetail(true);
		model = constructCommonModel(request, entityProperty, model, "Transaction", "transaction", option);
		return basePage;
	}

	@RequestMapping(value = { "/user" })
	public String user(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/user");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<BaseEntity>> listObject = new HashMap<>();
		listObject.put("userRole", CollectionUtil.convertList(entityService.getAllUserRole()));
		EntityProperty entityProperty = EntityUtil.createEntityProperty(User.class, listObject); 
		model = constructCommonModel(request, entityProperty, model, "User", "management");
		return basePage;
	}

//	@RequestMapping(value = { "/menu" })
//	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//		if (!userService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		try {
//			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
//		} catch (Exception e) {
//			return ERROR_404_PAGE;
//		}
//		EntityProperty entityProperty = EntityUtil.createEntityProperty(Menu.class, null); 
//		model = constructCommonModel(request, entityProperty, model, "Menu", "management");
//		return basePage;
//	} 
	
	/**
	 * 
						NON ENTITY

	 */
	
	@RequestMapping(value = { "/appsession" })
	public String appsession(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		model.addAttribute("title", "Apps Sessions");
		model.addAttribute("pageUrl", "shop/app-session"); 
		model.addAttribute("page", "management");
		return basePage;
	} 

	private void checkUserAccess(User user, String url) throws Exception {
		componentService.checkAccess(user, url);
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("com.fajar.entity.costflow");
	}

}
