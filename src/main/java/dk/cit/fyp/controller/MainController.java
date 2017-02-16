package dk.cit.fyp.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
	
	private final static Logger logger = Logger.getLogger(MainController.class);
	
	@RequestMapping(value={"/login"}, method=RequestMethod.GET)
	public String showLoginPage() {
		logger.info("Get request to '/login'");
		return "login";
	}
	
	@RequestMapping(value={"/login-error"}, method=RequestMethod.GET)
	public String failedLogin(Model model) {
		model.addAttribute("loginError", true);
		return "login";
	}
	
	@RequestMapping(value={"/", "/translate"}, method=RequestMethod.GET)
	public String showTranslatePage() {
		return "translate";
	}
	
	@RequestMapping(value={"/admin"}, method=RequestMethod.GET)
	public String showAdminPage() {
		return "admin";
	}
	
	@RequestMapping(value={"/customers"}, method=RequestMethod.GET)
	public String showCustomerPage() {
		return "customers";
	}
	
	@RequestMapping(value={"/review"}, method=RequestMethod.GET)
	public String showReviewPage() {
		return "review";
	}
	
	@RequestMapping(value={"/upload"}, method=RequestMethod.GET)
	public String showUploadPage() {
		return "upload";
	}

}
