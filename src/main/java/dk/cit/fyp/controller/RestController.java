package dk.cit.fyp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.service.CustomerService;
import dk.cit.fyp.service.HorseService;
import dk.cit.fyp.service.RaceService;

@Controller
public class RestController {
	
	@Autowired
	CustomerService customerService;
	@Autowired
	RaceService raceService;
	@Autowired
	HorseService horseService;
	
	private final static Logger logger = Logger.getLogger(RestController.class);
			
	@RequestMapping(value={"/api/account/{username}"}, method=RequestMethod.GET)
	public @ResponseBody Customer getAccountInfo(@PathVariable(value="username") String username) {
		logger.info("GET to /api/account/" + username);
		Customer customer = customerService.get(username).get(0);
		return customer;
	}
	
	@RequestMapping(value={"/api/track/{time}"}, method=RequestMethod.GET)
	public @ResponseBody Race getTracks(@PathVariable(value="time") String time) {
//		List<String> tracks = raceService.getTracks();
		return raceService.find(time).get(0);
	}
	
	@RequestMapping(value={"/api/horses/{time}"}, method=RequestMethod.GET)
	public @ResponseBody List<Horse> getHorses(@PathVariable(value="time") String time) {
		int raceID = raceService.find(time).get(0).getRaceID();
		return horseService.getHorsesInRace(raceID);
		
	}
}
