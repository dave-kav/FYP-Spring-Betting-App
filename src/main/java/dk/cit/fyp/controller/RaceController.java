package dk.cit.fyp.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.HorseService;
import dk.cit.fyp.service.RaceService;
import dk.cit.fyp.wrapper.RaceWrapper;

@Controller
public class RaceController {
		
	private final static Logger logger = Logger.getLogger(RaceController.class);
	
	@Autowired
	RaceService raceService;
	@Autowired
	BetService betService;
	@Autowired
	HorseService horseService;
	
	/**
	 * Load Race details interface to facilitate 'settling' of race. 
	 *
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param raceID integer value representing the ID of the race to be settled.
	 * @return Race details interface.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races/{raceID}"}, method=RequestMethod.GET)
	public String viewRace(Model model, Principal principal, @PathVariable(value="raceID") int raceID) {
		Race race = raceService.get(raceID);
		
		//add message if race already settled
		if (race.getWinnerID() != 0) {
			race.setWinner(horseService.getById(race.getWinnerID()));
			String message = "Race already settled! Winner: " + race.getWinner().getName();
			if (race.getPlacedHorseIDs().size() > 0) {
				if (race.getPlacedHorseIDs().get(0) != 0)
					message += " Placed: ";
				List<Horse> places = new ArrayList<>();
				for (int i: race.getPlacedHorseIDs()) {
					if (i != 0) {
						Horse h = horseService.getById(i);
						message += h.getName() + " ";
					}
				}
				race.setPlacedHorses(places);
			}
			model.addAttribute("settleMessage", message);
		}
		
		logger.info("GET request to '/races/" + raceID + "'");
		model.addAttribute("userName", principal.getName());		
		model.addAttribute("horses", horseService.getHorsesInRace(raceID)); 
		model.addAttribute("race", race); 
		
		ArrayList<Horse> placedHorses = new ArrayList<>();
		for (int i = 0; i < race.getPlaces() - 1; i++)
			placedHorses.add(new Horse());
		
		RaceWrapper wrapper = new RaceWrapper();
		wrapper.setRace(race);
		wrapper.setHorseList(placedHorses);
		wrapper.setWinner(new Horse());
		model.addAttribute("wrapper", wrapper);
		
		return "race";
	}
	
	/**
	 * Process addition of new race and save to database.
	 * 
	 * @param wrapper RaceWrapper object used to manage Race object and List of Horse objects.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to admin interface, tab 2.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races/add"}, method=RequestMethod.POST)
	public String addRace(RaceWrapper wrapper, RedirectAttributes attributes) {
		logger.info("POST request to '/races/add'");
		
		// validate all fields have been filled correctly 
		for (Horse h: wrapper.getHorseList()) {
			if (h.getName().equals("")) {
				logger.info("blank name supplied, redirecting");
				attributes.addFlashAttribute("blankName", "Add Race Failed: Please ensure that you enter a name for each horse");
				return "redirect:/admin?tab=2";
			}
		}
		
		// save race object and obtain raceID
		Race race = wrapper.getRace();
		raceService.save(race);
		int raceID = raceService.find(race.getTime()).get(0).getRaceID();		
		
		// assign horses and their numbers to the race.
		int number = 1;
		for (Horse h: wrapper.getHorseList()) {
			h.setRaceID(raceID);
			h.setNumber(number++);
			horseService.save(h);
		}
		
		attributes.addFlashAttribute("successRaceMessage", "New race added!");
		return "redirect:/admin?tab=2";
	}
	
	/**
	 * Return horses interface for adding horses to a new race.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param tempRace Race object that indicates the number of runners.
	 * @param bindingResult BindingResult object used to validate errors.
	 * @return Add horses interface.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races"}, method=RequestMethod.POST)
	public String addHorses(Model model, Principal principal, @Valid Race tempRace, BindingResult bindingResult, RedirectAttributes attributes) {
		logger.info("POST request to '/races'");
		if (bindingResult.hasErrors()) {
			logger.info("Error: redirecting");
			attributes.addFlashAttribute("errorRaceMessage", "Failed to add race, are you sure those details are correct?");
			return "redirect:/admin?tab=2";
		}
		model.addAttribute("userName", principal.getName());

		List<Horse> horses = horseService.getRaceRunners(tempRace.getRunners());
		RaceWrapper wrapper = new RaceWrapper();
		wrapper.setHorseList((ArrayList<Horse>) horses);
		wrapper.setRace(tempRace);
		model.addAttribute("wrapper", wrapper);
		
		return "horses";
	}	
}
