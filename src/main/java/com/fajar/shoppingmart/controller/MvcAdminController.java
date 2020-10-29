package com.fajar.shoppingmart.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.TransactionMode;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Page;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.transaction.TransactionHistoryService;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.DateUtil;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("admin")
@Authenticated
public class MvcAdminController extends BaseController {

	@Autowired
	private TransactionHistoryService transactionHistoryService;
	
	public MvcAdminController() {
		log.info("-----------------Mvc Admin Controller------------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/home" })
	@CustomRequestInfo(title = "Dashboard", pageUrl = "dashboard-component/dashboard-page", stylePaths = { "dashboard", "filter-box" })
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Calendar cal = Calendar.getInstance(); 

		model.addAttribute("menus", componentService.getDashboardMenus(request));
		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
		model.addAttribute("page", "dashboard");
		model.addAttribute("currentMonth", cal.get(Calendar.MONTH) + 1);
		model.addAttribute("currentYear", cal.get(Calendar.YEAR));
		int minYear = transactionService.getMinTransactionYear();
		model.addAttribute("minYear", minYear);
		model.addAttribute("maxYear", cal.get(Calendar.YEAR));
		model.addAttribute("months", DateUtil.months());
		model.addAttribute("years", DateUtil.yearArray(minYear, cal.get(Calendar.YEAR)));
		return basePage;
	}

	@RequestMapping(value = { "/product/{code}" })
	@CustomRequestInfo(pageUrl = "webpage/product-detail-page")
	public String productDetail(@PathVariable(required = true) String code, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Calendar cal = Calendar.getInstance();

		Map<String, Object> fieldsFilter = new HashMap<String, Object>();
		fieldsFilter.put("code", code);
		fieldsFilter.put("withStock", true);
		fieldsFilter.put("withSupplier", true);
		Filter filter = Filter.builder().exacts(true).limit(1).contains(false).fieldsFilter(fieldsFilter).build();
		WebRequest requestObject = WebRequest.builder().entity("product").filter(filter).build();
		WebResponse productResponse = productService.getProductsCatalog(requestObject, request.getHeader("requestId"));

		Product product = (Product) productResponse.getEntities().get(0);

		List<String> imageUrlList = CollectionUtil.arrayToList(product.getImageUrl().split("~"));
		List<Map<String, Object>> imageUrlObjects = new ArrayList<>();
		for (String string : imageUrlList) {
			imageUrlObjects.add(new HashMap<String, Object>() { 
				private static final long serialVersionUID = 1055027585947531920L;

				{
					put("value", string);
				}
			});
		}
		model.addAttribute("product", product);
		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("title", product.getName());

		model.addAttribute("imageUrlList", imageUrlList);
		model.addAttribute("productUnit", product.getUnit().getName());
		model.addAttribute("productCategory", product.getCategory().getName());
		model.addAttribute("page", "management");
		model.addAttribute("currentMonth", cal.get(Calendar.MONTH) + 1);
		model.addAttribute("currentYear", cal.get(Calendar.YEAR));
		model.addAttribute("productId", product.getId());
		int minYear = transactionService.getMinTransactionYear();
		model.addAttribute("minYear", minYear);
		model.addAttribute("maxYear", cal.get(Calendar.YEAR));
//		model.addAttribute("maxYear", transactionYears[1]);
		return basePage;

	}

	@RequestMapping(value = { "/resourcesinfo" })
	@CustomRequestInfo(pageUrl = "webpage/resources-info", title = "Cash Balance")
	public String resourcesInfo(Model model, HttpServletRequest request, HttpServletResponse response) {

		Date now = new Date();
		
		model.addAttribute("currentMonth", DateUtil.getCalendarMonth(now)+1);
		model.addAttribute("currentYear", DateUtil.getCalendarYear(now));
		model.addAttribute("currentDay", DateUtil.getCalendarDayOfMonth(now));
		return basePage;
	}

//	@RequestMapping(value = { "/management" })
//	public String menuManagement(Model model, HttpServletRequest request, HttpServletResponse response)
//			throws IOException {
//
//		if (!userService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		model.addAttribute("menus", componentService.getManagementMenus(request));
//		model.addAttribute("contextPath", request.getContextPath());
//		model.addAttribute("title", "Shop::Management");
//		model.addAttribute("pageUrl", "webpage/management-page");
//		model.addAttribute("page", "management");
//		return basePage;
//	}

//	@RequestMapping(value = { "/transaction" })
//	public String transaction(Model model, HttpServletRequest request, HttpServletResponse response)
//			throws IOException {
//
//		if (!userService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		model.addAttribute("menus", componentService.getTransactionMenus(request));
//		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
//		model.addAttribute("title", "Shop::Transaction");
//		model.addAttribute("pageUrl", "webpage/transaction-page");
//		model.addAttribute("page", "transaction");
//		return basePage;
//	}

	@RequestMapping(value = { "/transaction/in", "/transaction/in/", "/transaction/in/{transactionCode}" })
	@CustomRequestInfo(scriptPaths = { "transaction" }, stylePaths = {
			"transaction-sell-purc" }, title = "Purchase", pageUrl = "transaction-component/transaction-purchasing-page")
	public String purchasingTransaction(@PathVariable(required = false) String transactionCode, Model model,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (null != transactionCode) {
			model.addAttribute("requestCode", transactionCode);
		} 
		model.addAttribute("transactionModes", CollectionUtil.listToKeyVal(Arrays.asList(TransactionMode.values())));
		
		return basePage;
	}

	@RequestMapping(value = { "/transaction/out", "/transaction/out/", "/transaction/out/{transactionCode}" })
	@CustomRequestInfo(scriptPaths = { "transaction" }, stylePaths = {
			"transaction-sell-purc" }, title = "Selling", pageUrl = "transaction-component/transaction-selling-page")
	public String sellingTransaction(@PathVariable(required = false) String transactionCode, Model model,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (null != transactionCode) {
			model.addAttribute("requestCode", transactionCode);
		} 
		model.addAttribute("transactionModes", CollectionUtil.listToKeyVal(Arrays.asList(TransactionMode.values())));

		return basePage;
	}

	@RequestMapping(value = { "/sidemenudisplayorder" })
	@CustomRequestInfo(title = "Menu Sequence Order", pageUrl = "webpage/sequenceordering", stylePaths = {
			"sequenceordering" })
	public String sideMenuDisplayOrder(Model model, HttpServletRequest request, HttpServletResponse response) {

//		model.addAttribute("pages", componentService.getAllPages());
		model.addAttribute("idField", EntityUtil.getIdFieldOfAnObject(Page.class).getName());
		model.addAttribute("displayField", "name");
		model.addAttribute("entityName", "page");
		model.addAttribute("withAdditionalSetting", true);
		model.addAttribute("resetSequenceLink", "/account/websetting?action=resetmenu"); 
		return basePage;

	}
	
	@RequestMapping(value = { "/transactionreceipt", "/transactionreceipt/{code}" })
	public String transactionReceipt(Model model, @PathVariable(name = "code", required = false)String code) {

		if(code == null) {
			
			throw new IllegalArgumentException("transaction not found because the code is not provided");
			//return null;
		} 
		
		WebResponse reportData = transactionHistoryService.getTransactionData(code);
		if(reportData.getTransaction() == null) {
			throw new IllegalArgumentException("transaction not found with code:"+code);
		}
		Transaction transaction = reportData.getTransaction();
		model.addAttribute("title", "Transaction :"+code); 
		model.addAttribute("transaction", transaction);
		model.addAttribute("totalPrice", ProductFlow.calculateTotalPrice(transaction.getProductFlows()));
		model.addAttribute("totalQuantity", ProductFlow.calculateTotalQuantity(transaction.getProductFlows()));
		return "transaction-component/receipt-page";

	}
	
	

}
