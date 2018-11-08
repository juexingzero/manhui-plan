package com.manhui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {


	/**
	 * 注册
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/register")
	public String register(Model model) {
		return "register";
	}

}
