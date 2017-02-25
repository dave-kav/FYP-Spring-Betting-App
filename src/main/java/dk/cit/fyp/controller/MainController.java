package dk.cit.fyp.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.ImageService;
import dk.cit.fyp.service.RaceService;

@Controller
public class MainController {
	
	@Autowired
	BetService betService;
	@Autowired
	RaceService raceService;
	@Autowired 
	ImageService imgService;
	
	private final static Logger logger = Logger.getLogger(MainController.class);
	
	@RequestMapping(value={"/login"}, method=RequestMethod.GET)
	public String showLoginPage() {
		logger.info("GET request to '/login'");
		return "login";
	}
	
	@RequestMapping(value={"/login-error"}, method=RequestMethod.GET)
	public String failedLogin(Model model) {
		logger.info("GET request to '/login-error'");
		model.addAttribute("loginError", true);
		return "login";
	}
	
	@RequestMapping(value={"/", "/translate"}, method=RequestMethod.GET)
	public String showTranslatePage(Model model, Principal principal) {
		logger.info("GET request to '/translate'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("translatePage", true);
		
		model = betService.getNext(model);
		
		return "translate";
	}
	
	@RequestMapping(value={"/translate"}, method=RequestMethod.POST)
	public String translate(Model model, Principal principal, Bet tempBet, Race tempRace) {
		logger.info("POST request to '/translate'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("translatePage", true);
		
		Bet bet = betService.get(tempBet.getBetID());
		bet.setSelection(tempBet.getSelection());
		bet.setTranslated(true);
		bet.setEachWay(tempBet.isEachWay());
		
		logger.info("Race time: " + tempRace.getTime());
		
		List<Race> races = raceService.find(tempRace.getTime());
		if (races.size() > 0) {
			Race race = races.get(0);
			bet.setRaceID(race.getRaceID());
		}
	
		betService.save(bet);
		
		model = betService.getNext(model);				
		return "translate";
	}
		
	@RequestMapping(value={"/upload"}, method=RequestMethod.GET)
	public String showUploadPage(Model model, Principal principal) {
		logger.info("GET request to '/upload'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		return "upload";
	}
	
	/**
	 * This function retrieves an image from a POST, saves the image to file-system and 
	 * returns the encoded image to be displayed on the front-end. The image is the saved 
	 * to the database as a new bet. 
	 */
	@RequestMapping(value={"/upload"}, method=RequestMethod.POST)
	public String uploadImage(Model model, Principal principal, 
			@RequestParam MultipartFile file,HttpSession session, 
			RedirectAttributes attributes) {
		
		logger.info("POST request to '/upload'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		
		// save image to file system
		String path = session.getServletContext().getRealPath("/");  
        String fileName = file.getOriginalFilename();
        
        //check file selected and file is correct type
        if (fileName.equals("")) {
        	attributes.addFlashAttribute("noFile", "Please select a file.");
        	return "redirect:upload";
        } 
        
        if (!fileName.endsWith(".jpg")) {
        	attributes.addFlashAttribute("wrongFile", "Please select a '.jpg' file.");
        	return "redirect:upload";
        }
        
        byte bytes[] = null;
        try{  
        	bytes = file.getBytes();  
	          
	        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path + "/" + fileName));  
	        bout.write(bytes);  
	        bout.flush();  
	        bout.close();  
	          
        } catch(Exception e) {
        	System.out.println(e);
        }
        
        // encode bytes for display
        String filePath = path  + fileName;
        logger.info(filePath);

        String imgSrc = imgService.getImageSource(bytes);
        
		model.addAttribute("bet", new Bet());
        model.addAttribute("isImg", true);
		model.addAttribute("imgSrc", imgSrc);
		
		imgService.storeLastImgPath(filePath); 
		
        return "upload";  
	}
	
	@RequestMapping(value={"/confirmUpload"}, method=RequestMethod.POST)
	public String confirmUpload(Model model, Principal principal, Bet bet) {		
		logger.info("POST request to '/confirmUpload'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		
		logger.info(bet.getStake());
		String imagePath = imgService.getLastImagePath();
		logger.info(imagePath);
		
		bet.setImage(imagePath);
		betService.save(bet);
		
        return "upload";  
	}
	
	@RequestMapping(value={"/bets/all"}, method=RequestMethod.GET)
	public String showReviewPage(Model model, Principal principal) {
		logger.info("GET request to '/review'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("reviewPage", true);
		
		List<Bet> bets = betService.findAll();
		model.addAttribute("bets", bets);
		return "review";
	}
	
	@RequestMapping(value={"/bets/{betID}"}, method=RequestMethod.GET)
	public String showBet(Model model, Principal principal, @PathVariable(value="betID") String betID) {
		logger.info("GET request to '/review'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("reviewPage", true);
		
		logger.info("BET ID:" + betID);
		
		int betIDint = Integer.parseInt(betID);
		Bet bet = betService.get(betIDint);
		model.addAttribute("bet", bet);
		return "edit";
	}
	
	@RequestMapping(value={"/admin"}, method=RequestMethod.GET)
	public String showAdminPage(Model model, Principal principal) {
		logger.info("GET request to '/admin'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("adminPage", true);
		return "admin";
	}
	
	@RequestMapping(value={"/customers"}, method=RequestMethod.GET)
	public String showCustomerPage(Model model, Principal principal) {
		logger.info("GET request to '/customers'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("customerPage", true);
		return "customers";
	}

}
