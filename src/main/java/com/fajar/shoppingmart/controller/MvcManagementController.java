package com.fajar.shoppingmart.controller;

import static com.fajar.shoppingmart.util.MvcUtil.constructCommonModel;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.annotation.Authenticated;
import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.entity.Message;
import com.fajar.shoppingmart.entity.Profile;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.entity.EntityManagementPageService;
import com.fajar.shoppingmart.util.EntityUtil;
import com.fajar.shoppingmart.util.MyJsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("management")
@Authenticated
@CustomRequestInfo(
		stylePaths = "entitymanagement", 
		scriptPaths = "entitymanagement", 
		pageUrl = "entity-management-component/entity-management-page")
public class MvcManagementController extends BaseController {
 
	@Autowired
	private EntityManagementPageService entityManagementPageService;

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
	public String commonManahementPage(@PathVariable("name") String name, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		model = entityManagementPageService.setModel(request, model, name); 
		return basePage;
	}

	@RequestMapping(value = { "/profile" })
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			checkUserAccess(userSessionService.getUserFromSession(request), "/management/profile");
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Profile.class, null);

		model = constructCommonModel(request, entityProperty, model, "shopProfile", "management");
		// override singleObject
		model.addAttribute("entityId", webAppConfiguration.getProfile().getId());
		model.addAttribute("singleRecord", true);
		return basePage;
	}

	/**
	 * RESTRICTED ACCESS
	 * 
	 * @throws Exception
	 **/

	@RequestMapping(value = { "/messages" })
	public String messages(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			checkUserAccess(userSessionService.getUserFromSession(request), "/management/messages");
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_404_PAGE;
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Message.class, null);
		entityProperty.setEditable(false);
		entityProperty.removeElements("color", "fontColor");
		System.out.println("================ELEMENTS:" + MyJsonUtil.listToJson(entityProperty.getElements()));
		model = constructCommonModel(request, entityProperty, model, "message", "management");
		return basePage;
	}

//	@RequestMapping(value = { "/productFlow" })
//	public String productflow(Model model, HttpServletRequest request, HttpServletResponse response)
//			throws IOException {
//
//		if (!userSessionService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		try {
//			checkUserAccess(userSessionService.getUserFromSession(request), "/management/productFlow");
//		} catch (Exception e) {
//			e.printStackTrace(); return ERROR_404_PAGE;
//		}
//		EntityProperty entityProperty = EntityUtil.createEntityProperty(ProductFlow.class, null); 
//		model = constructCommonModel(request,entityProperty, model, "productFlow", "management");
//		return basePage;
//	}

	@RequestMapping(value = { "/transaction", "/transaction/{option}" })
	public String transaction(@PathVariable(required = false) String option, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		 
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Transaction.class, null);
		entityProperty.setEditable(false);
		entityProperty.setWithDetail(true);
		model = constructCommonModel(request, entityProperty, model, "Transaction", "transaction", option);
		return basePage;
	}

	@RequestMapping(value = { "/user" })
	public String user(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		return commonManahementPage("user", model, request, response);
	}

//	@RequestMapping(value = { "/menu" })
//	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//		if (!userSessionService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		try {
//			checkUserAccess(userSessionService.getUserFromSession(request), "/management/menu");
//		} catch (Exception e) {
//			e.printStackTrace(); return ERROR_404_PAGE;
//		}
//		EntityProperty entityProperty = EntityUtil.createEntityProperty(Menu.class, null); 
//		model = constructCommonModel(request, entityProperty, model, "Menu", "management");
//		return basePage;
//	} 

	/**
	 * 
	 * NON ENTITY
	 * 
	 */

	@RequestMapping(value = { "/appsession" })
	@CustomRequestInfo(title = "Apps Sessions", pageUrl = "webpage/app-session", 
		stylePaths = "sessionmanagement", 
		scriptPaths = "sessionmanagement")
	public String appsession(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		 
		return basePage;
	}

	private void checkUserAccess(User user, String url) throws Exception {
		componentService.checkAccess(user, url);
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("com.fajar.entity.costflow");
	}

}
