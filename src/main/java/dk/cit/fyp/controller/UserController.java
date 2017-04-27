package dk.cit.fyp.controller;

import java.security.Principal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.UserService;

@Controller
public class UserController {
	
	private final static Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	/**
	 * Process addition of new system user and saves to database.
	 * 
	 * @param user User to be added to database.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to admin interface, tab 3.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/users"}, method=RequestMethod.POST)
	public String addUser(User user, RedirectAttributes attributes) {
		logger.info("POST request to '/users'");
		
		List<User> allUsers = userService.findAll();
		for (User u: allUsers) {
			if (u.getUsername().equals(user.getUsername())) {
				attributes.addFlashAttribute("addUserErrorMessage", "User with name " + user.getUsername() + " already exists!");
				return "redirect:/admin?tab=3";
			}
		}
		
		logger.info("Adding user...");
		userService.save(user);
		logger.info("User added!");
		logger.info(" Redirecting to /admin");
		
		attributes.addFlashAttribute("successMessage", "New User added!");
		return "redirect:/admin?tab=3";
	}
	
	/**
	 * Process deletion of user account.
	 * 
	 * @param principal Principal object user to obtain logged in user details.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @param username String represnting username of user to be deleted.
	 * @return Redirect to admin interface, tab 4.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/users/delete/{username}"}, method=RequestMethod.POST)
	public String deleteUser(Principal principal, RedirectAttributes attributes, @PathVariable(value="username") String username) {
		logger.info("POST request to /users/delete/'" + username + "'");
		
		if (username.equals(principal.getName())) {
			attributes.addAttribute("errorDeleteMessage", "Cannot delete the logged in User!");
			return "redirect:/admin?tab=4";
		}
		
		userService.delete(username);
		attributes.addFlashAttribute("successDeleteMessage", "User deleted!");
		return "redirect:/admin?tab=4";
	}

}
