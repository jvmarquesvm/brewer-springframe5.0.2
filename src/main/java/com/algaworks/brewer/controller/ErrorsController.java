package com.algaworks.brewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorsController {
	
	@GetMapping("/404")
	public String paginaNaoEncontrada() {
		return "404";
	}
	
	@RequestMapping( value = "/500", method = RequestMethod.POST)
	public String paginaComErroPost() {
		return "500";
	}
	
	@GetMapping("/500")
	public String paginaComErroGet() {
		return "500";
	}

}
