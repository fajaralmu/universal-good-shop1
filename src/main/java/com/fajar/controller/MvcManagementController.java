package com.fajar.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.config.EntityProperty;
import com.fajar.entity.UserRole;
import com.fajar.service.EntityService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebAppConfiguration;
import com.fajar.util.EntityUtil;
import com.fajar.util.JSONUtil;
import com.fajar.util.MVCUtil;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("management")
public class MvcManagementController extends BaseController{

	Logger log = LoggerFactory.getLogger(MvcManagementController.class);
	@Autowired
	private UserSessionService userService;
	@Autowired
	private EntityService entityService;
	@Autowired
	private WebAppConfiguration webAppConfiguration;

	private static String basePage;

	public MvcManagementController() {
		log.info("-----------------MvcManagementController------------------");
	}

	@PostConstruct
	private void init() {
		basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/unit" })
	public String unit(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Unit", null);
		model.addAttribute("entityProperty", entityProperty);
		model = constructCommonModel(request, model, "Unit", "management");
		return basePage;
	}
	
	@RequestMapping(value = { "/profile" })
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("ShopProfile", null);
		model.addAttribute("entityProperty", entityProperty);
		model = constructCommonModel(request, model, "shopProfile", "management");
		return basePage;
	}

	@RequestMapping(value = { "/supplier" })
	public String supplier(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Supplier", null);
		model.addAttribute("entityProperty", entityProperty);
		model = constructCommonModel(request, model, "Supplier", "management");
		return basePage;
	}

	@RequestMapping(value = { "/customer" })
	public String customer(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Customer", null);
		model.addAttribute("entityProperty", entityProperty);
		model = constructCommonModel(request, model, "Customer", "management");
		return basePage;
	}

	@RequestMapping(value = { "/product" })
	public String product(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Product", null);
		model.addAttribute("entityProperty", entityProperty);
		model = constructCommonModel(request, model, "Product", "management");
		return basePage;
	}

	@RequestMapping(value = { "/category" })
	public String category(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Category", null);
		model.addAttribute("entityProperty", entityProperty);
		model = constructCommonModel(request, model, "Category", "management");
		return basePage;
	}

	/** RESTRICTED ACCESS **/

	@RequestMapping(value = { "/productFlow" })
	public String productflow(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("ProductFlow", null);
		model.addAttribute("entityProperty", entityProperty);
		model = constructCommonModel(request, model, "productFlow", "management");
		return basePage;
	}

	@RequestMapping(value = { "/transaction", "/transaction/{option}" })
	public String transaction(@PathVariable(required = false) String option, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Transaction", null);
		entityProperty.setEditable(false);
		entityProperty.setWithDetail(true);
		model.addAttribute("entityProperty", entityProperty); 
		model = constructCommonModel(request, model, "Transaction", "transaction",option);
		return basePage;
	}

	@RequestMapping(value = { "/user" })
	public String user(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		HashMap<String, Object> listObject = new HashMap<>();
		List<UserRole> roles = entityService.getAllUserRole();
		listObject.put("userRole", roles);
		EntityProperty entityProperty = EntityUtil.createEntityProperty("User", listObject);
		model.addAttribute("entityProperty", entityProperty);
		log.info("============ENTITY PROPERTY: " + entityProperty);
		model = constructCommonModel(request, model, "User", "management");
		return basePage;
	}

	@RequestMapping(value = { "/menu" })
	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
		}
		EntityProperty entityProperty = EntityUtil.createEntityProperty("Menu", null);
		model.addAttribute("entityProperty", entityProperty);
		log.info("============ENTITY PROPERTY: " + entityProperty);
		model = constructCommonModel(request, model, "Menu", "management");
		return basePage;
	}

	private Model constructCommonModel(HttpServletRequest request, Model model, String string, String string2) {
		// TODO Auto-generated method stub
		return constructCommonModel(request, model, string, string2, null);
	}

	private Model constructCommonModel(HttpServletRequest request, Model model, String title, String page,
			String option) {
		model.addAttribute("contextPath", request.getContextPath());
		String host = MVCUtil.getHost(request);
		model.addAttribute("host", host);
		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
		model.addAttribute("title", "Management::" + title);
		model.addAttribute("pageUrl", "shop/entity-management-page");
		model.addAttribute("page", page);
		boolean withOption  = false;
		String optionJson = "null";
		
		if (null != option) {
			System.out.println("=========REQUEST_OPTION: "+option);
			String[] options = option.split("&");
			Map<String, Object> optionMap = new HashMap<String,Object>();
			for (String optionItem : options) {
				String[] optionKeyValue = optionItem.split("=");
				if(optionKeyValue == null || optionKeyValue.length !=2) {
					continue;
				}
				optionMap.put(optionKeyValue[0], optionKeyValue[1]);
			}
			if(optionMap.isEmpty()==false) {
				withOption = true;
				optionJson = JSONUtil.mapToJson(optionMap);
				System.out.println("=========GENERATED_OPTION: "+optionMap);
				System.out.println("=========OPTION_JSON: "+optionJson);
			}
		}
		model.addAttribute("withOption", withOption);
		model.addAttribute("options", optionJson); 
		return model;
	}

}
