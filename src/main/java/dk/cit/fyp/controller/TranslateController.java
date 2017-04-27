package dk.cit.fyp.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dk.cit.fyp.bean.UserBetBean;
import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.HorseService;
import dk.cit.fyp.service.ImageService;
import dk.cit.fyp.service.RaceService;
import dk.cit.fyp.service.UserService;

@Controller
public class TranslateController {
	
	private final static Logger logger = Logger.getLogger(TranslateController.class);
	
	@Autowired
	BetService betService;
	@Autowired
	UserService userService;
	@Autowired
	UserBetBean userBetBean;
	@Autowired 
	ImageService imgService;
	@Autowired
	HorseService horseService;
	@Autowired
	RaceService raceService;
	
	/**
	 * Display the translate page to the user. 
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @return Translate page.
	 */
	@RequestMapping(value={"/", "/translate", "/home"}, method=RequestMethod.GET)
	public String showTranslatePage(Model model, Principal principal) {
		User user = userService.get(principal.getName()).get(0);
		
		logger.info("GET request to '/translate'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("translatePage", true);
		
		// get next bet in queue, if one exists
		List<Bet> bets = betService.top();
		if (bets.size() != 0) {
			Bet bet = bets.get(0);
			// add mapping for user and bet - used to manage queue
			userBetBean.setBet(user.getUsername(), bet);
			betService.onScreen(bet);
		
			logger.info("Loading image for bet_id " + bet.getBetID());
			
			String imgSrc = "";
			if (!bet.getImagePath().contains("betting-app1-default-image-store.s3-eu-west-1.amazonaws.com")) {
				
				logger.info(bet.getImagePath());
				
				try {
					byte[] bytes = imgService.getBytes(bet.getImagePath());
					imgSrc = imgService.getImageSource(bytes);					
				} catch (NullPointerException e) {
					logger.error("Image file not found in server");
				}
			} else {
				
				BufferedImage img = null;
				
				try {
					img = ImageIO.read(new URL(bet.getImagePath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					ImageIO.write(img, "jpg", baos);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				byte[] bytes = baos.toByteArray();
				imgSrc = imgService.getImageSource(bytes);
			}			
			 
			model.addAttribute("imgSrc", imgSrc);
			model.addAttribute("img", true);
			model.addAttribute("bet", bet);
			model.addAttribute("race", new Race());
			model.addAttribute("queue", betService.getNumUntranslated());
		}
		else {
			//empty objects used in fields 
			model.addAttribute("race", new Race());
			model.addAttribute("bet", new Bet());
		}
		
		// add data for translate fields
		model.addAttribute("tracks", raceService.getTracks());
		model.addAttribute("horses", horseService.getHorses());
		model.addAttribute("times", raceService.getRaceTimes());
		
		return "translate";
	}
	
	/**
	 * Process translation of bet, verifying fields and saving to the database.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param tempBet Bet object to be translated and saved to DB
	 * @param tempRace Race object used to aid translation.
	 * @param bindingResult BindingResult object used to validate errors.
	 * @return Redirect to translate page once done.
	 */
	@RequestMapping(value={"/translate"}, method=RequestMethod.POST)
	public String translate(Model model, Principal principal, Bet tempBet, Race tempRace, BindingResult bindingResult) {
	
		if (bindingResult.hasErrors()) {
			return "redirect:/translate";
		}
		
		logger.info("POST request to '/translate'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("translatePage", true);
		
		Bet bet = betService.get(tempBet.getBetID());
		betService.onScreen(bet);
		
		//map from horse name/number to selection id
		String selection = tempBet.getSelection();
		int selectionID;
		try {
			selectionID = Integer.parseInt(selection);
			bet.setSelection(selectionID + "");
		} catch (NumberFormatException e) {
			selectionID = horseService.get(selection).get(0).getSelectionID();
		}
	 		
		//set dependent fields and save to DB
		bet.setSelection(selectionID + "");
		bet.setTranslated(true);
		bet.setEachWay(tempBet.isEachWay());
		bet.setOdds(tempBet.getOdds());
		bet.setRaceID(raceService.find(tempRace.getTime()).get(0).getRaceID());
		bet.setTranslatedBy(principal.getName());
		betService.save(bet);
		
		return "redirect:/translate";
	}

}
