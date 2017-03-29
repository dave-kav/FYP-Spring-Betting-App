package dk.cit.fyp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
	private JsonObject jsonObj = new JsonObject();
	private Gson gson = new Gson();
			
	@RequestMapping(value={"/api/account/{username}"}, method=RequestMethod.GET)
	@ResponseBody 
	public Customer getAccountInfo(@PathVariable(value="username") String username) {
		logger.info("GET to /api/account/" + username);
		Customer customer = customerService.get(username).get(0);
		return customer;
	}
	
	@RequestMapping(value={"/api/race/{time}"}, method=RequestMethod.GET)
	@ResponseBody 
	public Race getRace(@PathVariable(value="time") String time) {
		Race race;
		
		try {
			 race = raceService.find(time).get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		
		race.setAllHorses(horseService.getHorsesInRace(race.getRaceID()));
		return race;
	}
	
	@RequestMapping(value={"/api/race/horse/{horse}"}, method=RequestMethod.GET)
	@ResponseBody 
	public Race getRaceByHorse(@PathVariable(value="horse") String name) {
		Horse horse;
		
		try {
			horse = horseService.get(name).get(0);
		} catch  (IndexOutOfBoundsException e) {
			return null;
		}
		
		Race race = raceService.get(horse.getRaceID());
		race.setAllHorses(horseService.getHorsesInRace(race.getRaceID()));
		return race;
	}
	
	@RequestMapping(value={"/api/race/track/{track}"}, method=RequestMethod.GET)
	@ResponseBody 
	public String getRacesByTrack(@PathVariable(value="track") String track) {	
		List<String> times = raceService.getTimesByTrack(track);
		JsonElement timesJson = gson.toJsonTree(times);
		jsonObj.add("times", timesJson);
		
		List<String> horses = new ArrayList<>();
		for (String time: times) {
			Race r = raceService.find(time).get(0);
			for (Horse h: horseService.getHorsesInRace(r.getRaceID())) {
				horses.add(h.getName());
			}
		}
		
		JsonElement horseJson = gson.toJsonTree(horses);
		jsonObj.add("horses", horseJson);	
		return jsonObj.toString();
	}
	
	@RequestMapping(value={"/api/login"}, method=RequestMethod.POST)
	@ResponseBody
	public Customer appLogin(HttpServletRequest request) {
		logger.info("POST to '/api/login'");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Customer customer = null; 
		if (customerService.get(username).size() > 0) {
			customer = customerService.get(username).get(0);

			if (!customer.getPassword().equals(password))
				return null;
		}
		
		return customer;
	}
	
	@RequestMapping(value={"/api/signup"}, method=RequestMethod.POST)
	@ResponseBody
	public Customer appSignup(HttpServletRequest request) {
		logger.info("POST to '/api/login'");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Customer customer = null; 
 
		
		return customer;
	}
}
