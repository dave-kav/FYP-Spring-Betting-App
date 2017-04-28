package dk.cit.fyp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
public class SettlingController {
	
	private final static Logger logger = Logger.getLogger(SettlingController.class);

	@Autowired
	BetService betService;
	@Autowired
	RaceService raceService;
	@Autowired
	HorseService horseService;
	
	/**
	 * Process 'settling' of race. Save details to database.
	 * 
	 * @param request HttpServletRequest used to obtain hidden input fields.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @param raceID integer value representing the ID of the race to be settled.
	 * @param wrapper RaceWrapper object used to manage Race object and List of Horse objects.
	 * @return Redirect to admin interface.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races/{raceID}"}, method=RequestMethod.POST)
	public String settleRace(HttpServletRequest request, RedirectAttributes attributes, @PathVariable(value="raceID") int raceID, RaceWrapper wrapper) {
		logger.info("POST request to '/races/" + raceID + "'");
	
		//redirect if no horse selected
		if (wrapper.getWinner().getName().equals("0")) {
			attributes.addFlashAttribute("errorMessage", "Invalid option selected - Please choose a horse");
			return "redirect:/races/" + raceID;
		}
		
		Horse winner = horseService.get(wrapper.getWinner().getName()).get(0);
		Race race = raceService.get(wrapper.getRace().getRaceID());
		
		ArrayList<Horse> placedHorses = new ArrayList<>();
		
		if (race.getPlaces() > 1) {
			//array for comparing selected indexes
			ArrayList<Horse> allHorses = new ArrayList<>();
			allHorses.add(winner);
			
			for (Horse place: wrapper.getHorseList()) {		
				//redirect if no horse selected
				if(place.getName().equals("0")) {
					attributes.addFlashAttribute("errorMessage", "Invalid option selected - Please choose a horse");
					return "redirect:/races/" + raceID;
				}
				
				placedHorses.add(horseService.get(place.getName()).get(0));
				allHorses.add(horseService.get(place.getName()).get(0));
			}
			
			//check same indexes not selected
			for (int i = 0; i < allHorses.size(); i++) {
				for (int j = i + 1; j < allHorses.size(); j++ ) {
					if(allHorses.get(i).getName().equals(allHorses.get(j).getName())) {
						attributes.addFlashAttribute("errorMessage", "A horse can not be assigned multiple places");
						return "redirect:/races/" + raceID;
					}
				}
			}
		}

		race.setWinner(winner);
		race.setPlacedHorses(placedHorses);
		race.setSettled(true);
		raceService.save(race);
		betService.settleBets(race);
		
		attributes.addFlashAttribute("successSettleMessage", "All bets on " + race.getTime() + " at " + race.getTrack() + " now being settled");
		return "redirect:/admin";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races/{raceID}/resettle"}, method=RequestMethod.POST)
	public String resettleRace(HttpServletRequest request, RedirectAttributes attributes, @PathVariable(value="raceID") int raceID) {
		logger.info("POST to '/races/" + raceID + "/resettle'");
		Race race = raceService.get(raceID);
		logger.info(race.toString());
		
		race.setWinnerID(0);		
		race.setSettled(false);
		List<Integer> placedIDs = race.getPlacedHorseIDs(); 
		for (int i = 0; i <placedIDs.size(); i++)
			placedIDs.set(i, 0);
		race.setPlacedHorseIDs(placedIDs);
		
		betService.unsettleBets(raceID);
		raceService.save(race);
		
		logger.info(race.toString());
		return "redirect:/races/" + raceID;
	}
}
