package com.fajar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.dto.UniversalObject;
import com.fajar.entity.Product;
import com.fajar.service.ComponentService;
import com.fajar.service.ProductService;
import com.fajar.service.TransactionService;
import com.fajar.service.UserSessionService;
import com.fajar.service.WebConfigService;
import com.fajar.util.CollectionUtil;
import com.fajar.util.MVCUtil;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("admin")
public class MvcAdminController extends BaseController {

	Logger log = LoggerFactory.getLogger(MvcAdminController.class);
	@Autowired
	private UserSessionService userService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private WebConfigService webAppConfiguration;
	
	
	
	private String basePage;

	public MvcAdminController() {
		log.info("-----------------MvcAdminController------------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
	}

	@RequestMapping(value = { "/home" })
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Calendar cal = Calendar.getInstance();

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		
		model.addAttribute("menus", componentService.getDashboardMenus(request)); 
		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
		model.addAttribute("title", "Shop::Dashboard");
		model.addAttribute("pageUrl", "shop/home-page");
		model.addAttribute("page", "dashboard");
		model.addAttribute("currentMonth", cal.get(Calendar.MONTH) + 1);
		model.addAttribute("currentYear", cal.get(Calendar.YEAR));
		Integer[] transactionYears = transactionService.getMinAndMaxTransactionYear();
		System.out.println("========TRX YEARS: " + transactionYears);
		model.addAttribute("minYear", transactionYears[0]);
		model.addAttribute("maxYear", transactionYears[1]);
		return basePage;
	}
	
	
	@RequestMapping(value = { "/product/{code}" })
	public String productDetail(@PathVariable(required = true) String code, Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Calendar cal = Calendar.getInstance();

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		} 
		
		Map<String, Object> fieldsFilter = new HashMap<String, Object>();
		fieldsFilter.put("code", code);
		fieldsFilter.put("withStock", true);
		fieldsFilter.put("withSupplier", true);
		Filter filter = Filter.builder()
				.exacts(true).limit(1).contains(false)
				.fieldsFilter(fieldsFilter).build();
		ShopApiRequest requestObject = ShopApiRequest.builder().entity("product")
				.filter(filter ).build();
		ShopApiResponse productResponse = productService.getProductsCatalog(requestObject, request.getHeader("requestId") );
		System.out.println(" c c c c c c Product Response: "+productResponse);
		
		Product product = (Product) productResponse.getEntities().get(0);
		
		List<String> imageUrlList =CollectionUtil.arrayToList(product.getImageUrl().split("~"));
		List<UniversalObject> imageUrlObjects = new ArrayList<>();
		for (String string : imageUrlList) {
			imageUrlObjects.add(UniversalObject.builder().value(string).build());
		}
		System.out.println(" . . . . . IMAGE URL LIST:"+imageUrlList);
		model.addAttribute("product", product); 
		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("title", product.getName());
		model.addAttribute("pageUrl", "shop/product-detail-page");
		model.addAttribute("imageUrlList", imageUrlList);
		model.addAttribute("productUnit", product.getUnit().getName());
		model.addAttribute("productCategory", product.getCategory().getName());
		model.addAttribute("page", "management");
		model.addAttribute("currentMonth", cal.get(Calendar.MONTH) + 1);
		model.addAttribute("currentYear", cal.get(Calendar.YEAR));
		model.addAttribute("productId", product.getId());
		Integer[] transactionYears = transactionService.getMinAndMaxTransactionYear();
		System.out.println("========TRX YEARS: " + transactionYears);
		model.addAttribute("minYear", transactionYears[0]);
		model.addAttribute("maxYear", transactionYears[1]);
		return basePage;
		 
	}

	@RequestMapping(value = { "/management" })
	public String menuManagement(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		model.addAttribute("menus", componentService.getManagementMenus(request)); 
		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("title", "Shop::Management");
		model.addAttribute("pageUrl", "shop/management-page");
		model.addAttribute("page", "management");
		return basePage;
	}

	@RequestMapping(value = { "/transaction" })
	public String transaction(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		model.addAttribute("menus", componentService.getTransactionMenus(request)); 
		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
		model.addAttribute("title", "Shop::Transaction");
		model.addAttribute("pageUrl", "shop/transaction-page");
		model.addAttribute("page", "transaction");
		return basePage;
	}

	@RequestMapping(value = { "/transaction/in","/transaction/in/","/transaction/in/{transactionCode}" })
	public String incomingTransaction(@PathVariable(required = false) String transactionCode, Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		if(null!=transactionCode) {
			model.addAttribute("requestCode", transactionCode);
		}
		model.addAttribute("title", "Shop::Supply");
		model.addAttribute("pageUrl", "shop/transaction-in-page");
		model.addAttribute("page", "transaction");
		return basePage;
	}

	@RequestMapping(value = { "/transaction/out","/transaction/out/","/transaction/out/{transactionCode}"  })
	public String outTransaction(@PathVariable(required = false) String transactionCode, Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (!userService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/account/login");
			return basePage;
		}
		if(null!=transactionCode) {
			model.addAttribute("requestCode", transactionCode);
		}
		model.addAttribute("title", "Shop::Purchase");
		model.addAttribute("pageUrl", "shop/transaction-out-page");
		model.addAttribute("page", "transaction");
		return basePage;
	}

}
