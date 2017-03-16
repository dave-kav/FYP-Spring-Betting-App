package dk.cit.fyp.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.CustomerService;
import dk.cit.fyp.service.HorseService;
import dk.cit.fyp.service.ImageService;
import dk.cit.fyp.service.RaceService;
import dk.cit.fyp.service.UserService;
import dk.cit.fyp.wrapper.RaceWrapper;

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
	CustomerService customerService;
	
	private final static Logger logger = Logger.getLogger(MainController.class);
	
	@RequestMapping(value={"/login"}, method=RequestMethod.GET)
	public String showLoginPage() {
		logger.info("GET request to '/login'");
		return "login";
	}
	
	@RequestMapping(value={"/login-error"}, method=RequestMethod.GET)
	public String failedLogin(Model model, RedirectAttributes attributes) {
		logger.info("GET request to '/login-error'");
		attributes.addFlashAttribute("loginError", true);
		return "redirect:login";
	}
	
	@RequestMapping(value={"/", "/translate", "/home"}, method=RequestMethod.GET)
	public String showTranslatePage(Model model, Principal principal) {
		logger.info("GET request to '/translate'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("translatePage", true);
		
		model = betService.getNext(model);
		
		return "translate";
	}
	
	@RequestMapping(value={"/translate"}, method=RequestMethod.POST)
	public String translate(Model model, Principal principal, Bet tempBet, 
							Race tempRace, BindingResult bindingResult) {
	
		if (bindingResult.hasErrors()) {
			//TODO Add flash attributes
			return "redirect:/translate";
		}
		
		logger.info("POST request to '/translate'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("translatePage", true);
		
		Bet bet = betService.get(tempBet.getBetID());
		betService.onScreen(bet);
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

		//temporary store for display 
        String filePath = path  + fileName;
        logger.info(filePath);
		imgService.storeLastImgPath(filePath); 
		
        return "upload";  
	}
	
	@RequestMapping(value={"/confirmUpload"}, method=RequestMethod.POST)
	public String confirmUpload(Model model, Principal principal, Bet bet, 
			BindingResult bindingResult, RedirectAttributes attributes) {		
		if (bindingResult.hasErrors())
			return "redirect:/upload";
		
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		
		logger.info(bet.getStake());
		String imagePath = imgService.getLastImagePath();
		logger.info(imagePath);
		
		bet.setImagePath(imagePath);
		betService.save(bet);
		
		attributes.addFlashAttribute("uploadSuccess", "Bet added to queue.");
        return "redirect:upload";  
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
		logger.info("GET request to '/bets/'" + betID);
		model.addAttribute("userName", principal.getName());
		model.addAttribute("reviewPage", true);
		
		int betIDint = Integer.parseInt(betID);
		Bet bet = betService.get(betIDint);

		byte[] bytes = imgService.getBytes(bet.getImagePath());
		String imgSrc = imgService.getImageSource(bytes);
		
		model.addAttribute("imgSrc", imgSrc);
		model.addAttribute("bet", bet);
		model.addAttribute("race", new Race());
		
		return "editBet";
	}
	
	@RequestMapping(value={"/bets/{betID}"}, method=RequestMethod.POST)
	public String updateBet(Bet bet) {
		logger.info("POST to /bets/'" + bet.getBetID() + "'");
		logger.info(bet.toString());
		
		betService.save(bet);
		
		return "redirect:/bets/all";
	}
	
	@RequestMapping(value={"/customers"}, method=RequestMethod.GET)
	public String showCustomerPage(Model model, Principal principal) {
		logger.info("GET request to '/customers'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("customerPage", true);
		
		model.addAttribute("customers", customerService.findAll());
		model.addAttribute("newCustomer", new Customer());
		return "customers";
	}
	
	@RequestMapping(value={"/customers"}, method=RequestMethod.POST)
	public String addCustomer(@Valid Customer customer, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			for (ObjectError e: bindingResult.getAllErrors()) {
				logger.info(e.toString());
			}
			
			return "redirect:/customers";
		}
		//TODO add flash attributes
		
		logger.info("POST request to '/customers'");
		logger.info(customer.toString());
		customerService.save(customer);
				
		return "redirect:customers";
		
	}
	
	@RequestMapping(value={"/customers/{username}"}, method=RequestMethod.GET)
	public String editCustomer(Model model, Principal principal, @PathVariable(value="username") String username) {
		logger.info("GET request to '/customers'");
		model.addAttribute("userName", principal.getName());
		
		model.addAttribute("customer", customerService.get(username).get(0));
		
		return "editCustomer";
	}
	
	@RequestMapping(value={"/customers/{username}"}, method=RequestMethod.POST)
	public String updateCustomer(Customer customer) {
		logger.info("POST request to '/customers/'" + customer.getUsername());
		
		customerService.save(customer);
		
		return "redirect:/customers";
	}
	
	@RequestMapping(value={"/balance/{username}"}, method=RequestMethod.POST)
	public String updateBalance(HttpServletRequest request, @PathVariable(value="username") String username, 
			RedirectAttributes attributes) {
		Customer customer = customerService.get(username).get(0);
		logger.info("POST request to '/balance/" + customer.getUsername() + "'");
		
		String amountString = request.getParameter("amount");
		double amount = Double.parseDouble(amountString);
		logger.info(amount);
		
		logger.info(request.getParameter("deposit"));
		logger.info(request.getParameter("withdraw"));
		if (request.getParameter("deposit") != null) {
			customer.setCredit(customer.getCredit() + amount);
			customerService.save(customer);
			attributes.addFlashAttribute("successMessage", "New account balance: " + customer.getCredit());
			return "redirect:/customers/" + username;
		}
		else {
			if (customer.getCredit() >= amount) {
				customer.setCredit(customer.getCredit() - amount);
				customerService.save(customer);
				attributes.addFlashAttribute("successMessage", "New account balance: " + customer.getCredit());
				return "redirect:/customers/" + username;
			}
			else {
				attributes.addFlashAttribute("errorMessage", "Insufficient Credit - Max withdrawal: " + customer.getCredit());
				return "redirect:/customers/" + username;
			}
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/admin"}, method=RequestMethod.GET)
	public String showAdminPage(Model model, Principal principal) {
		logger.info("GET request to '/admin'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("adminPage", true);
		
		model.addAttribute("user", new User());
		model.addAttribute("tempRace", new Race());
		model.addAttribute("allRaces", raceService.findAll());
		
		return "admin";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/users"}, method=RequestMethod.POST)
	public String addUser(User user) {
		logger.info("POST request to '/users'");
		
		logger.info("Adding user...");
		userService.save(user);
		logger.info("User added!");
		logger.info(" Redirecting to /admin");
		
		return "redirect:/admin";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races"}, method=RequestMethod.POST)
	public String addHorses(Model model, Principal principal, @Valid Race tempRace, BindingResult bindingResult) {
		logger.info("POST request to '/races'");
		if (bindingResult.hasErrors()) {
			//TODO Add flash attributes
			//TODO Add active tab attribute to html
			logger.info("Error: redirecting");
			return "redirect:/admin";
		}
		model.addAttribute("userName", principal.getName());

		List<Horse> horses = horseService.getRaceRunners(tempRace.getRunners());
		RaceWrapper wrapper = new RaceWrapper();
		wrapper.setHorseList((ArrayList<Horse>) horses);
		wrapper.setRace(tempRace);
		model.addAttribute("wrapper", wrapper);
		return "horses";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races/add"}, method=RequestMethod.POST)
	public String addRace(RaceWrapper wrapper) {
		logger.info("POST request to '/races/add'");
		logger.info(wrapper.getHorseList().get(0).getName());
		logger.info(wrapper.getRace().getTrack());
		
		Race race = wrapper.getRace();
		raceService.save(race);
		int raceID = raceService.find(race.getTime()).get(0).getRaceID();		
		
		int number = 1;
		for (Horse h: wrapper.getHorseList()) {
			h.setRaceID(raceID);
			h.setNumber(number++);
			horseService.save(h);
		}
				
		return "redirect:/admin";
	}
	
	/**
	 * Load individual race in order to settle. Save settled race details in database.
	 * Trigger settling of all bets related to the race on separate thread.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races/{raceID}"}, method=RequestMethod.GET)
	public String viewRace(Model model, Principal principal, @PathVariable(value="raceID") int raceID) {
		logger.info("GET request to '/races/" + raceID + "'");
		model.addAttribute("userName", principal.getName());
		
		Race race = raceService.get(raceID);		
		model.addAttribute("race", race);
		model.addAttribute("horses", horseService.getHorsesInRace(raceID));
		logger.info(horseService.getHorsesInRace(raceID).size());
		
		logger.info(race);
		
		return "race";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/races/{raceID}"}, method=RequestMethod.POST)
	public String settleRace(@PathVariable(value="raceID") int raceID, List<Horse> horses) {
		logger.info("POST request to '/races/" + raceID + "'");
		logger.info(horses.get(0).getName());
	
		return "redirect:/admin";
	}
}
