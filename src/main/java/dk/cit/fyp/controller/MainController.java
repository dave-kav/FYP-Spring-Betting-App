package dk.cit.fyp.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.jgroups.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import dk.cit.fyp.bean.UserBetBean;
import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.domain.Status;
import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.CustomerService;
import dk.cit.fyp.service.HorseService;
import dk.cit.fyp.service.ImageService;
import dk.cit.fyp.service.RaceService;
import dk.cit.fyp.service.UserService;
import dk.cit.fyp.wrapper.RaceWrapper;

/**
 * Primary controller of Betting Application. Performs background processing,
 * orchestrates data, serves interfaces.
 * 
 * @author Dave Kavanagh
 *
 */
@Controller
public class MainController {
	
	@Autowired
	BetService betService;
	@Autowired
	RaceService raceService;
	@Autowired
	UserService userService;
	@Autowired 
	ImageService imgService;
	@Autowired
	HorseService horseService;
	@Autowired
	AmazonS3Client s3;
	@Autowired
	UserBetBean userBetBean;
	
	@Value("${cloud.aws.bucket.name}")
    private String bucketName;
	private final static Logger logger = Logger.getLogger(MainController.class);
	
	/**
	 * Display login page.
	 * 
	 * @return Login page.
	 */
	@RequestMapping(value={"/login"}, method=RequestMethod.GET)
	public String showLoginPage() {
		logger.info("GET request to '/login'");
		return "login";
	}
	
	/**
	 * Handle failed login, display login page with error.
	 * 
	 * @param attributes RedirectAttributes object used to pass error message. 
	 * @return Login page.
	 */
	@RequestMapping(value={"/login-error"}, method=RequestMethod.GET)
	public String failedLogin(RedirectAttributes attributes) {
		logger.info("GET request to '/login-error'");
		attributes.addFlashAttribute("loginError", true);
		return "redirect:login";
	}
	
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
		
	/**
	 * Display interface for uploading image.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @return Upload interface.
	 */
	@RequestMapping(value={"/upload"}, method=RequestMethod.GET)
	public String showUploadPage(Model model, Principal principal) {
		logger.info("GET request to '/upload'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		return "upload";
	}
	
	/**
	 * This function retrieves an image from a POST, saves the image to file-system and 
	 * returns the encoded image to be displayed on the front-end. The image is then saved 
	 * to the database as a new bet.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param file Multipart file object used to retrieve selected file.
	 * @param session HTTPSession object.
	 * @param attributes RedirectAttributes object used to pass error message. 
	 * @return
	 */
	@RequestMapping(value={"/upload"}, method=RequestMethod.POST)
	public String uploadImage(Model model, Principal principal, @RequestParam MultipartFile file,HttpSession session, 
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
        	throw new MultipartException(fileName);
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
        String imgSrc = imgService.getImageSource(bytes);
		model.addAttribute("bet", new Bet());
		model.addAttribute("imgSrc", imgSrc);

		//pass image for storage
        String filePath = path  + fileName;
		model.addAttribute("filePath", filePath);
		
        return "upload";  
	}
	
	/**
	 * Used to process uploaded image, saving new bet to database, and storing image in S3.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param bet New Bet object to be added and saved to database.
	 * @param bindingResult BindingResult object used to validate errors.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to upload page.
	 */
	@RequestMapping(value={"/confirmUpload"}, method=RequestMethod.POST)
	public String confirmUpload(Model model, Principal principal, Bet bet, BindingResult bindingResult, RedirectAttributes attributes, 
								HttpServletRequest request) {		
		if (bindingResult.hasErrors())
			return "redirect:/upload";
		
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		
		String filePath = request.getParameter("filePath");
		
		String key = "betting-slip-" + UUID.randomUUID();
		s3.putObject(new PutObjectRequest(bucketName, key, new File(filePath)).withCannedAcl(CannedAccessControlList.PublicRead));
		String url = s3.getResourceUrl(bucketName, key);
		
		bet.setImagePath(url);				
		betService.save(bet);
		
		attributes.addFlashAttribute("uploadSuccess", "Bet added to queue.");
        return "redirect:upload";  
	}
	
	

	/**
	 * Display admin interface, only available to admin users.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @return Admin interface.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/admin"}, method=RequestMethod.GET)
	public String showAdminPage(Model model, Principal principal) {
		logger.info("GET request to '/admin'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("adminPage", true);
		
		// Domain objects necessary for addition of user/races and settling of races.
		model.addAttribute("user", new User());
		model.addAttribute("tempRace", new Race());
		model.addAttribute("allRaces", raceService.findAll());
		model.addAttribute("allUsers", userService.findAll());
		
		return "admin";
	}			
}
