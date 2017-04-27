package dk.cit.fyp.controller;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.cit.fyp.domain.Race;
import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.RaceService;
import dk.cit.fyp.service.UserService;

/**
 * Primary controller of Betting Application. Performs background processing,
 * orchestrates data, serves interfaces.
 * 
 * @author Dave Kavanagh
 *
 */
@Controller
public class AdminController {
	
	@Autowired
	BetService betService;
	@Autowired
	RaceService raceService;
	@Autowired
	UserService userService;

	private final static Logger logger = Logger.getLogger(AdminController.class);
	
	/**
	 * Display login page.
	 * 
	 * @return Login page.
	 */
	@RequestMapping(value={"/login"}, method=RequestMethod.GET)
	public String showLoginPage() {
		logger.info("GET request to '/login'");
		return "login";
	}
	
	/**
	 * Handle failed login, display login page with error.
	 * 
	 * @param attributes RedirectAttributes object used to pass error message. 
	 * @return Login page.
	 */
	@RequestMapping(value={"/login-error"}, method=RequestMethod.GET)
	public String failedLogin(RedirectAttributes attributes) {
		logger.info("GET request to '/login-error'");
		attributes.addFlashAttribute("loginError", true);
		return "redirect:login";
	}	

	/**
	 * Display admin interface, only available to admin users.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @return Admin interface.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/admin"}, method=RequestMethod.GET)
	public String showAdminPage(Model model, Principal principal) {
		logger.info("GET request to '/admin'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("adminPage", true);
		
		// Domain objects necessary for addition of user/races and settling of races.
		model.addAttribute("user", new User());
		model.addAttribute("tempRace", new Race());
		model.addAttribute("allRaces", raceService.findAll());
		model.addAttribute("allUsers", userService.findAll());
		
		return "admin";
	}			
}
