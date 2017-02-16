package dk.cit.fyp.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
	
	private final static Logger logger = Logger.getLogger(MainController.class);
	
	@RequestMapping(value={"/", "/login"}, method=RequestMethod.GET)
	public String showLoginPage() {
		logger.info("Get request to '/login'");
		return "login";
	}
	
	@RequestMapping(value={"/translate"}, method=RequestMethod.GET)
	public String translate() {
		return "translate";
	}

}
