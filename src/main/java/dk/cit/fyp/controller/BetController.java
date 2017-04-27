package dk.cit.fyp.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.domain.Status;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.HorseService;
import dk.cit.fyp.service.ImageService;
import dk.cit.fyp.service.RaceService;

@Controller
public class BetController {
	
	private final static Logger logger = Logger.getLogger(BetController.class);
	
	@Autowired 
	ImageService imgService;
	@Autowired
	BetService betService;
	@Autowired
	RaceService raceService;
	@Autowired
	HorseService horseService;
	
	/**
	 * Display interface where user can review list of all bets in system.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @return Review bets interface.
	 */
	@RequestMapping(value={"/bets/all"}, method=RequestMethod.GET)
	public String showReviewPage(Model model, Principal principal) {
		logger.info("GET request to '/review'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("reviewPage", true);
		
		List<Bet> bets = betService.findAll();
		model.addAttribute("bets", bets);
		return "review";
	}
	
	/**
	 * Show details of an individual bet.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param betID Unique identifying number of the bet to be displayed.
	 * @return Bet details interface.
	 */
	@RequestMapping(value={"/bets/{betID}"}, method=RequestMethod.GET)
	public String showBet(Model model, Principal principal, @PathVariable(value="betID") String betID) {
		logger.info("GET request to '/bets/'" + betID);
		model.addAttribute("userName", principal.getName());
		model.addAttribute("reviewPage", true);
		
		// get bet from repo using betID
		int betIDint = Integer.parseInt(betID);
		Bet bet = betService.get(betIDint);
		
		//encode image for display
		String imgSrc = "";
		if (!bet.getImagePath().contains("betting-app1-default-image-store.s3-eu-west-1.amazonaws.com")) {
			try {
				byte[] bytes = imgService.getBytes(bet.getImagePath());
				imgSrc = imgService.getImageSource(bytes);
				logger.info(imgSrc);
				model.addAttribute("imgSrc", imgSrc);			
			} catch (NullPointerException e) {
				logger.error("Image not found in server!");
			}	
		} else if (bet.getImagePath().contains("betting-app1-default-image-store.s3-eu-west-1.amazonaws.com")){
			imgSrc = bet.getImagePath();
			logger.info(imgSrc);
			model.addAttribute("imgSrc", imgSrc);  
		}
		
		//If bet has been translated already race info for the bet is added
		if (bet.getRaceID() != 0)
			model.addAttribute("race", raceService.get(bet.getRaceID()));
		else
			model.addAttribute("race", new Race());

		Horse h;
		try {
			h = horseService.getById(Integer.parseInt(bet.getSelection()));
			bet.setSelection(h.getName());
		} catch (NumberFormatException e) {
			logger.error("Horse not found for this bet: ID: " + bet.getSelection());
		}
		
		// add bet to model, along with horse, race and time info for edit translate purposes
		model.addAttribute("bet", bet);
		model.addAttribute("tracks", raceService.getTracks());
		model.addAttribute("horses", horseService.getHorses());
		model.addAttribute("times", raceService.getRaceTimes());
		
		return "editBet";
	}
	
	/**
	 * Process update bet request.
	 * @param request HttpServletRequest object to retrieve hidden input form values.
	 * @param bet Bet object to be updated.
	 * @return Redirect to 'review bets' page.
	 */
	@RequestMapping(value={"/bets/{betID}"}, method=RequestMethod.POST)
	public String updateBet(HttpServletRequest request, RedirectAttributes attributes, Principal principal, Bet bet) {
		logger.info("POST to /bets/'" + bet.getBetID() + "'");

		logger.info("before update: " + bet.toString());
		
		//map from horse name/number to selection id
		String selection = bet.getSelection();
		int selectionID;
		try {
			selectionID = Integer.parseInt(selection);
			bet.setSelection(selectionID + "");
		} catch (NumberFormatException e) {
			selectionID = horseService.get(selection).get(0).getSelectionID();
		}
	 		
		bet.setSelection(selectionID + "");			
		bet.setRaceID(horseService.get(selection).get(0).getRaceID());		
		bet.setTranslated(true);
		bet.setStatus(Status.OPEN);
		bet.setTranslatedBy(principal.getName());
		
		logger.info("after update: " + bet.toString());
		
		betService.save(bet);
		
		attributes.addFlashAttribute("editBetSuccess", "Successfully updated bet: " + bet.getBetID());
		return "redirect:/bets/all";
	}
}
