package dk.cit.fyp.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.service.CustomerService;

@Controller
public class CustomerController {
	
	private final static Logger logger = Logger.getLogger(CustomerController.class);
	
	@Autowired
	CustomerService customerService;
	
	/**
	 * Display customer management interface.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @return Customer interface.
	 */
	@RequestMapping(value={"/customers"}, method=RequestMethod.GET)
	public String showCustomerPage(Model model, Principal principal) {
		logger.info("GET request to '/customers'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("customerPage", true);
		
		model.addAttribute("customers", customerService.findAll());
		model.addAttribute("newCustomer", new Customer());
		return "customers";
	}
	
	/**
	 * Add new customer record to the database.
	 * 
	 * @param customer Customer object to be added to database.
	 * @param bindingResult BindingResult object used to validate errors.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to customer interface.
	 */
	@RequestMapping(value={"/customers"}, method=RequestMethod.POST)
	public String addCustomer(Customer customer, BindingResult bindingResult, RedirectAttributes attributes) {
		if (bindingResult.hasErrors())
			return "redirect:/customers";
		
		List<Customer> customers = customerService.findAll();
		for (Customer c: customers) {
			if (c.getUsername().equals(customer.getUsername())) {
				attributes.addFlashAttribute("addCustomerError", "Username already taken!");
				return "redirect:/customers";
			}
		}
		
		logger.info("POST request to '/customers'");
		customerService.save(customer);
		
		attributes.addFlashAttribute("addCustomerSuccess", "Successfully added new customer account!");
		return "redirect:customers";		
	}
	
	/**
	 * Display interface to edit customer account or update balance.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param username String value representing the customer's username.
	 * @return editCustomer interface.
	 */
	@RequestMapping(value={"/customers/{customer}"}, method=RequestMethod.GET)
	public String editCustomer(Model model, Principal principal, @PathVariable(value="customer") String customer) {
		logger.info("GET request to '/customers/" + customer + "'");
		model.addAttribute("userName", principal.getName());
		
		model.addAttribute("customer", customerService.get(customer).get(0));
		
		return "editCustomer";
	}
	
	/**
	 * Process update to customer account info.
	 * 
	 * @param customer Customer object to be update and saved.
	 * @return Redirect to customer management interface.
	 */
	@RequestMapping(value={"/customers/{username}"}, method=RequestMethod.POST)
	public String updateCustomer(Customer customer, RedirectAttributes attributes) {
		logger.info("POST request to '/customers/'" + customer.getUsername());
		
		customerService.save(customer);
		
		attributes.addFlashAttribute("addCustomerSuccess", "Successfully updated account details for: " + customer.getUsername());
		return "redirect:/customers";
	}
	
	/**
	 * Update customer's account balance.
	 * 
	 * @param request HttpServletRequest object used to obtain hidden input fields.
	 * @param username String value representing the customer's username.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to edit customer details interface.
	 */
	@RequestMapping(value={"/balance/{username}"}, method=RequestMethod.POST)
	public String updateBalance(HttpServletRequest request, @PathVariable(value="username") String username, 
			RedirectAttributes attributes) {
		Customer customer = customerService.get(username).get(0);
		logger.info("POST request to '/balance/" + customer.getUsername() + "'");
		
		String amountString = request.getParameter("amount");
		double amount = Double.parseDouble(amountString);

		// process deposit
		if (request.getParameter("deposit") != null) {
			customer.setCredit(customer.getCredit() + amount);
			customerService.save(customer);
			attributes.addFlashAttribute("successMessage", String.format("Deposited: \u20ac%.2f", amount));
			return "redirect:/customers/" + username;
		}
		// process withdrawal
		else if (request.getParameter("withdraw") != null) {
			if (customer.getCredit() >= amount) {
				customer.setCredit(customer.getCredit() - amount);
				customerService.save(customer);
				attributes.addFlashAttribute("successMessage", String.format("Withdrew: \u20ac%.2f", amount));
				return "redirect:/customers/" + username;
			}
			// not enough credit
			else {
				attributes.addFlashAttribute("errorMessage", String.format("Insufficient Credit - Max withdrawal: \u20ac%.2f", customer.getCredit()));
				return "redirect:/customers/" + username;
			}
		}
		// an error occurred
		else {
			logger.info("deposit/withdrawal not found");
			attributes.addFlashAttribute("errorMessage", "Sorry, an error occurred...");
			return "redirect:/customers/" + username;
		}
	}

}
