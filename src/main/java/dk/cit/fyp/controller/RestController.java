package dk.cit.fyp.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.service.CustomerService;

@Controller
public class RestController {
	
	@Autowired
	CustomerService customerService;
	
	private final static Logger logger = Logger.getLogger(RestController.class);
			
	@RequestMapping(value={"/api/account/{username}"}, method=RequestMethod.GET)
	public @ResponseBody Customer getAccountInfo(@PathVariable(value="username") String username) {
		Customer customer = customerService.get(username).get(0);
		return customer;
	}

}
