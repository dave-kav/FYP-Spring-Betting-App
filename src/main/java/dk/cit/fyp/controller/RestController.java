package dk.cit.fyp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.service.BetService;
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
	@Autowired
	BetService betService;
	
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
	public String appLogin(HttpServletRequest request) {
		logger.info("POST to '/api/login'");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Customer customer = null; 
		if (customerService.get(username).size() > 0) {
			customer = customerService.get(username).get(0);

			if (!customer.getPassword().equals(password)) {
				jsonObj.addProperty("result", "error");
				jsonObj.addProperty("error", "Invalid username/password");
				return jsonObj.toString();
			}
		} else {
			jsonObj.addProperty("result", "error");
			jsonObj.addProperty("error", "Invalid username/password");
			return jsonObj.toString();
		}
		
		jsonObj.addProperty("result", "ok");
			
		List<Bet> bets = betService.getCustomerBets(username);
		for (Bet b: bets) {
			b.setHorse(horseService.getById(Integer.parseInt(b.getSelection())));
			b.setRace(raceService.get(b.getRaceID()));
		}
		
		customer.setBets(bets);
		
		JsonElement customerJson = gson.toJsonTree(customer, Customer.class);
		jsonObj.add("customer", customerJson);
		return jsonObj.toString();
	}
	
	@RequestMapping(value={"/api/signup"}, method=RequestMethod.POST)
	@ResponseBody
	public String appSignup(HttpServletRequest request) {
		logger.info("POST to '/api/signup'");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String dob = request.getParameter("dob");
		
		if (customerService.get(username).size() > 0) {
			
			jsonObj.addProperty("result", "error");
			jsonObj.addProperty("error", "Username already taken");
			logger.info("returning error: username taken");
			
			return jsonObj.toString();
			
		} else {
			
			Customer customer = new Customer();
			customer.setFirstName(firstName);
			customer.setLastName(lastName);
			customer.setUsername(username);
			customer.setPassword(password);
			
			//parse date
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    java.util.Date parsed = null;
		    
		    try {
		        parsed = sdf.parse(dob);
		        logger.info(parsed.toString());
		    } catch (ParseException e) {
		    	e.printStackTrace();
		    	
		    	jsonObj.addProperty("result", "error");
				jsonObj.addProperty("error", "Date of Birth must be in format DD/MM/YYYY");
				logger.info("returning error");
				
				return jsonObj.toString();
		    }
		    
		    java.sql.Date data = new java.sql.Date(parsed.getTime());
			customer.setDOB(data);		    
		    
		    //jodatime date format to check if customer is over 18
		    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		    DateTime dt = formatter.parseDateTime(dob);
		    		    
		    if (Years.yearsBetween(dt, new DateTime()).getYears() < 18) {
		    	jsonObj.addProperty("result", "error");
				jsonObj.addProperty("error", "Customer must be 18 years +");
				logger.info("returning error");
				
				return jsonObj.toString();
		    }
			
			customerService.save(customer);
			
			logger.info("Customer Added!");
			
			jsonObj.addProperty("result", "ok");
			return jsonObj.toString();
		}
	}
	
	@RequestMapping(value={"/api/raceInfo"}, method=RequestMethod.GET)
	@ResponseBody
	public String getRaceInfo() {
		List<Race> allRaces = raceService.findAll();
		for (Race r: allRaces) {
			r.setAllHorses(horseService.getHorsesInRace(r.getRaceID()));
		}
		jsonObj.addProperty("result", "ok");
		jsonObj.add("races", gson.toJsonTree(allRaces));
		return jsonObj.toString();
	}	
}
