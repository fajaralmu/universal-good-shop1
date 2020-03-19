package com.fajar.controller;

import static com.fajar.util.MvcUtil.constructCommonModel;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
import com.fajar.service.EntityService;
import com.fajar.service.LogProxyFactory;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;
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

	@RequestMapping(value = { "/unit" })
	public String unit(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/unit");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Unit.class, null); 
		model = constructCommonModel(request, entityProperty, model, "Unit", "management");
		return basePage;
	}

	@RequestMapping(value = { "/profile" })
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
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

	@RequestMapping(value = { "/supplier" })
	public String supplier(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/supplier");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Supplier.class, null);

		model = constructCommonModel(request, entityProperty, model, "Supplier", "management");
		return basePage;
	}
	
	@RequestMapping(value = { "/registeredrequest" })
	public String registeredRequest(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/registeredrequest");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(RegisteredRequest.class, null);

		model = constructCommonModel(request, entityProperty, model, "RegisteredRequest", "management");
		return basePage;
	}

	@RequestMapping(value = { "/customer" })
	public String customer(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/customer");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Customer.class, null); 
		model = constructCommonModel(request, entityProperty, model, "Customer", "management");
		return basePage;
	}

	@RequestMapping(value = { "/product" })
	public String product(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/product");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Product.class, null); 
		model = constructCommonModel(request, entityProperty, model, "Product", "management");
		return basePage;
	}

	@RequestMapping(value = { "/category" })
	public String category(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/category");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Category.class, null); 
		model = constructCommonModel(request, entityProperty, model, "Category", "management");
		return basePage;
	}
	
	@RequestMapping(value = { "/cost" })
	public String cost(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/cost");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Cost.class, null); 
		model = constructCommonModel(request, entityProperty, model, "Cost", "management");
		return basePage;
	}
	@RequestMapping(value = { "/costflow" })
	public String costflow(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/costflow");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, Object> listObject = new HashMap<>();
		listObject.put("costType", entityService.getAllCostType());
		EntityProperty entityProperty = EntityUtil.createEntityProperty(CostFlow.class, listObject); 
		model = constructCommonModel(request, entityProperty, model, "CostFlow", "management");
		return basePage;
	}

	@RequestMapping(value = { "/userrole" })
	public String userRole(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/userrole");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(UserRole.class, null); 
		model = constructCommonModel(request, entityProperty, model, "UserRole", "management");
		return basePage;
	}

	/** RESTRICTED ACCESS **/

	@RequestMapping(value = { "/messages" })
	public String messages(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
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
	
	@RequestMapping(value = { "/productFlow" })
	public String productflow(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/productFlow");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(ProductFlow.class, null); 
		model = constructCommonModel(request,entityProperty, model, "productFlow", "management");
		return basePage;
	}

	@RequestMapping(value = { "/transaction", "/transaction/{option}" })
	public String transaction(@PathVariable(required = false) String option, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
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
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/user");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, Object> listObject = new HashMap<>();
		listObject.put("userRole", entityService.getAllUserRole());
		EntityProperty entityProperty = EntityUtil.createEntityProperty(User.class, listObject); 
		model = constructCommonModel(request, entityProperty, model, "User", "management");
		return basePage;
	}

	@RequestMapping(value = { "/menu" })
	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Menu.class, null); 
		model = constructCommonModel(request, entityProperty, model, "Menu", "management");
		return basePage;
	} 
	
	/**
	 * 
						NON ENTITY

	 */
	
	@RequestMapping(value = { "/appsession" })
	public String appsession(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
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

}
