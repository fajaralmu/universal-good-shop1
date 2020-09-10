package com.fajar.shoppingmart.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.fajar.shoppingmart.dto.KeyValue;
import com.fajar.shoppingmart.entity.Page;
import com.fajar.shoppingmart.entity.Profile;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.service.ComponentService;
import com.fajar.shoppingmart.service.UserAccountService;
import com.fajar.shoppingmart.service.UserSessionService;
import com.fajar.shoppingmart.service.WebConfigService;
import com.fajar.shoppingmart.service.runtime.RuntimeService;
import com.fajar.shoppingmart.service.transaction.ProductService;
import com.fajar.shoppingmart.service.transaction.TransactionService;
import com.fajar.shoppingmart.util.DateUtil;
import com.fajar.shoppingmart.util.MvcUtil;
import com.fajar.shoppingmart.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BaseController {

	protected String basePage;

	@Autowired
	protected WebConfigService webAppConfiguration;
	@Autowired
	protected UserSessionService userSessionService;
	@Autowired
	protected UserAccountService accountService;
	@Autowired
	protected RuntimeService registryService;
	@Autowired
	protected UserSessionService userService;
	@Autowired
	protected TransactionService transactionService;
	@Autowired
	protected ProductService productService;
	@Autowired
	protected ComponentService componentService;

	@ModelAttribute("shopProfile")
	@Deprecated // new @ModelAttribute = 'profile'
	public Profile getShopProfile(HttpServletRequest request) {
//		System.out.println("Has Session: "+userSessionService.hasSession(request, false));
		return webAppConfiguration.getProfile();
	}

	@ModelAttribute("profile")
	public Profile getProfile(HttpServletRequest request) {
		return getShopProfile(request);
	}

	@ModelAttribute("timeGreeting")
	public String timeGreeting(HttpServletRequest request) {
		return DateUtil.getTimeGreeting();
	}

	@ModelAttribute("loggedUser")
	public User getLoggedUser(HttpServletRequest request) {
		if (userSessionService.hasSession(request, false)) {
			return userSessionService.getUserFromSession(request);
		} else
			return null;
	}

	@ModelAttribute("pageIconUrl")
	public String iconUrl(HttpServletRequest request) {
		Profile Profile = webAppConfiguration.getProfile();

		String icon;
		if (null != Profile.getPageIcon()) {
			icon = "/WebAsset/Shop1/Images/ICON/" + Profile.getPageIcon();
		} else {
			icon = "/res/img/javaEE.ico";
		}

		return icon;
	}

	@ModelAttribute("host")
	public String getHost(HttpServletRequest request) {
		return MvcUtil.getHost(request);
	}

	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	@ModelAttribute("fullImagePath")
	public String getFullImagePath(HttpServletRequest request) {
		return getHost(request) + getContextPath(request) + "/" + getUploadedImagePath(request) + "/";
	}

	@ModelAttribute("imagePath")
	public String getUploadedImagePath(HttpServletRequest request) {
		return webAppConfiguration.getUploadedImagePath();
	}

	@ModelAttribute("pageToken")
	public String pageToken(HttpServletRequest request) {
		try {
			return userSessionService.getTokenByServletRequest(request);
		}catch (Exception e) {
			return "";
		}
	}

	@ModelAttribute("requestId")
	public String getPublicRequestId(HttpServletRequest request) {
		try {
			Cookie cookie = getCookie(SessionUtil.JSESSSIONID, request.getCookies());
			String cookieValue = cookie == null ? UUID.randomUUID().toString() : cookie.getValue();
			return registryService.addPageRequest(cookieValue);
		}catch (Exception e) {
			
			return "";
		}

	}

	@ModelAttribute("registeredRequestId")
	public String getRegisteredRequestId(HttpServletRequest request) {

		return SessionUtil.getSessionRegisteredRequest(request);
	}

	@ModelAttribute("pages")
	public List<Page> pages(HttpServletRequest request) {

		return componentService.getPages(request);
	}

	@ModelAttribute("year")
	public int getCurrentYear(HttpServletRequest request) {
		return DateUtil.getCalendarItem(new Date(), Calendar.YEAR);
	}

	@ModelAttribute("authenticated")
	public boolean authenticated(HttpServletRequest request) {
		return userSessionService.hasSession(request);
	}

	public String activePage(HttpServletRequest request) {
		return userSessionService.getPageCode(request);
	}

	public void setActivePage(HttpServletRequest request) {

		String pageCode = componentService.getPageCode(request);
		userSessionService.setActivePage(request, pageCode);
	}

	public void setActivePage(HttpServletRequest request, String pageCode) {

		userSessionService.setActivePage(request, pageCode);
	}

	/**
	 * ====================================================== Statics
	 * ======================================================
	 * 
	 */

	public static Cookie getCookie(String name, Cookie[] cookies) {
		try {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		} catch (Exception ex) {
			log.error("ERROR GET COOKIE NAME: {}", name);
		}
		return null;
	}

	public static Cookie getJSessionIDCookie(HttpServletRequest request) {

		return getCookie(SessionUtil.JSESSSIONID, request.getCookies());
	}

	/**
	 * send to login page URL
	 * 
	 * @param request
	 * @param response
	 */
	public static void sendRedirectLogin(HttpServletRequest request, HttpServletResponse response) {
		sendRedirect(response, request.getContextPath() + "/account/login");
	}

	/**
	 * send to specified URL
	 * 
	 * @param response
	 * @param url
	 */
	public static void sendRedirect(HttpServletResponse response, String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private static void addResourcePaths(ModelAndView modelAndView, String resourceName, String... paths) {
		List<KeyValue<String, String>> resoucePaths = new ArrayList<>();
		for (int i = 0; i < paths.length; i++) {
			KeyValue<String, String> keyValue = new KeyValue<String, String>();
			keyValue.setValue(paths[i]);

			resoucePaths.add(keyValue);
			log.info("{}. Add {} to {} , value: {}", i, resourceName, modelAndView.getViewName(), paths[i]);
		}
		setModelAttribute(modelAndView, resourceName, resoucePaths);
	}

	private static void setModelAttribute(ModelAndView modelAndView, String attrName, Object attrValue) {
		if (null == attrValue) {
			return;
		}
		modelAndView.getModel().put(attrName, attrValue);
	}

	public static void addStylePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalStylePaths", paths);
	}

	public static void addJavaScriptResourcePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalScriptPaths", paths);
	}

	public static void addTitle(ModelAndView modelAndView, String title) {
		if (null == title || title.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "title", title);
	}

	public static void addPageUrl(ModelAndView modelAndView, String pageUrl) {
		if (null == pageUrl || pageUrl.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "pageUrl", pageUrl);

	}
}
