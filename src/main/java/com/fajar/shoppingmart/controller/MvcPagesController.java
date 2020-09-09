package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.annotation.CustomRequestInfo;
import com.fajar.shoppingmart.entity.Page;
import com.fajar.shoppingmart.service.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("webapp")
@Slf4j
public class MvcPagesController extends BaseController {

	public MvcPagesController() {
		log.info("---------------------------Mvc Pages Controller------------------------------");
	}

	@PostConstruct
	public void init() {

		basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/page/{code}" })
	@CustomRequestInfo(pageUrl = "webpage/master-common-page")
	public String page(@PathVariable(name = "code") String code, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Page page = componentService.getPage(code, request);

		if (null == page) {
			sendRedirect(response, request.getContextPath() + "/account/login");
			return basePage;
		}

		model.addAttribute("title", page.getName());
		model.addAttribute("page", page);
		return basePage;

	}

}
