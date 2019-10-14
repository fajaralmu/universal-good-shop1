package com.fajar.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.parameter.EntityParameter;
import com.fajar.service.RealtimeService;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("web")
public class GeneralController {
	
	Logger log = LoggerFactory.getLogger(GeneralController.class);
	@Autowired
	private RealtimeService realtimeService;
	
	public GeneralController() {
		log.info("-----------------GENERAL CONTROLLER------------------");
	}

	@GetMapping(value = "notfound")
	public String halamanNotFound(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak ditemukan");
		return "error/notfound";
	}
	
	@GetMapping(value = "noaccess")
	public String halamanNotAccessable(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak dapat diakses");
		return "error/notfound";
	}
	
	/**
	 * Realtime
	 */
	@GetMapping(value = "test-chatv1")
	public String testChat(Model model)  {
		return "websocket/chat";
	}
	@GetMapping( value = "test-chatv2")
	public String testChat2(Model model)  {
		return "websocket/chat2";
	}
	@GetMapping(value = "canvasv1")
	public String canvasv1(Model model)  {
		model.addAttribute("winW", EntityParameter.WIN_W);
		model.addAttribute("winH", EntityParameter.WIN_H);
		model.addAttribute("baseHealth", EntityParameter.baseHealth);
		model.addAttribute("rolePlayer", EntityParameter.ROLE_PLAYER);
		model.addAttribute("roleBonusLife", EntityParameter.ROLE_BONUS_LIFE);
		model.addAttribute("roleBonusArmor", EntityParameter.ROLE_BONUS_ARMOR);
		 return "websocket/anim/canvas1";
	}
}

