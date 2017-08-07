package com.eqan.web.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloController {
	private static final Logger LOG = LoggerFactory.getLogger(HelloController.class); 
	/*
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	*/

	/*
	@RequestMapping("/hello")
	public Hello hello(@RequestParam("name") String name) {
		Hello hello = new Hello(counter.incrementAndGet(), String.format(template, name));
		return hello;
	}
	*/
	@RequestMapping("/hello")
	public ModelAndView hello(Model model) {
		LOG.info("Received request for hello controller");
		model.addAttribute("greeting", "noob");
		return new ModelAndView("hello", "message", "Hello, World!");
	}
	
	/*
	@RequestMapping("*")
	@ResponseBody
	public String noob(HttpServletRequest request) {
		System.out.println(request.getRequestURI());
		return "hello world";
	}
	*/
	
	@PostConstruct
	public void sayHello() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Constructed hello controller");
		}
	}

}
