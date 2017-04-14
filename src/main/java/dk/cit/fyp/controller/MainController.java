package dk.cit.fyp.controller;

import java.io.BufferedOutputStream;
import java.io.File;
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
	CustomerService customerService;
	
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
		
		model = betService.getNext(model, user);
		
		model.addAttribute("tracks", raceService.getTracks());
		model.addAttribute("horses", horseService.getHorses());
		model.addAttribute("times", raceService.getTimes());
		
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

		//temporary store for display 
        String filePath = path  + fileName;
		imgService.storeLastImgPath(filePath); 
		
        return "upload";  
	}
	
	/**
	 * Used to process uploaded image, saving new bet to database.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param bet New Bet object to be added and saved to database.
	 * @param bindingResult BindingResult object used to validate errors.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to upload page.
	 */
	@RequestMapping(value={"/confirmUpload"}, method=RequestMethod.POST)
	public String confirmUpload(Model model, Principal principal, Bet bet, BindingResult bindingResult, RedirectAttributes attributes) {		
		if (bindingResult.hasErrors())
			return "redirect:/upload";
		
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		
		String imagePath = imgService.getLastImagePath();		
		bet.setImagePath(imagePath);
		betService.save(bet);
		
		attributes.addFlashAttribute("uploadSuccess", "Bet added to queue.");
        return "redirect:upload";  
	}
	
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
		try {
			byte[] bytes = imgService.getBytes(bet.getImagePath());
			imgSrc = imgService.getImageSource(bytes);	
			model.addAttribute("imgSrc", imgSrc);
		} catch (NullPointerException e) {
			logger.error("Image not found in server!");
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
		model.addAttribute("times", raceService.getTimes());
		
		return "editBet";
	}
	
	/**
	 * Process update bet request.
	 * @param request HttpServletRequest object to retrieve hidden input form values.
	 * @param bet Bet object to be updated.
	 * @return Redirect to 'review bets' page.
	 */
	@RequestMapping(value={"/bets/{betID}"}, method=RequestMethod.POST)
	public String updateBet(HttpServletRequest request, Principal principal, Bet bet) {
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
		
		return "redirect:/bets/all";
	}
	
	/**
	 * Display customer management interface.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @return Customer interface.
	 */
	@RequestMapping(value={"/customers"}, method=RequestMethod.GET)
	public String showCustomerPage(Model model, Principal principal) {
		logger.info("GET request to '/customers'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("customerPage", true);
		
		model.addAttribute("customers", customerService.findAll());
		model.addAttribute("newCustomer", new Customer());
		return "customers";
	}
	
	/**
	 * Add new customer record to the database.
	 * 
	 * @param customer Customer object to be added to database.
	 * @param bindingResult BindingResult object used to validate errors.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to customer interface.
	 */
	@RequestMapping(value={"/customers"}, method=RequestMethod.POST)
	public String addCustomer(Customer customer, BindingResult bindingResult, RedirectAttributes attributes) {
		if (bindingResult.hasErrors())
			return "redirect:/customers";
		
		List<Customer> customers = customerService.findAll();
		for (Customer c: customers) {
			if (c.getUsername().equals(customer.getUsername())) {
				attributes.addFlashAttribute("addCustomerError", "Username already taken!");
				return "redirect:/customers";
			}
		}
		
		logger.info("POST request to '/customers'");
		customerService.save(customer);
				
		return "redirect:customers";		
	}
	
	/**
	 * Display interface to edit customer account or update balance.
	 * 
	 * @param model Model object used to pass data to client side for display.
	 * @param principal Principal object user to obtain logged in user details.
	 * @param username String value representing the customer's username.
	 * @return editCustomer interface.
	 */
	@RequestMapping(value={"/customers/{customer}"}, method=RequestMethod.GET)
	public String editCustomer(Model model, Principal principal, @PathVariable(value="customer") String customer) {
		logger.info("GET request to '/customers/" + customer + "'");
		model.addAttribute("userName", principal.getName());
		
		model.addAttribute("customer", customerService.get(customer).get(0));
		
		return "editCustomer";
	}
	
	/**
	 * Process update to customer account info.
	 * 
	 * @param customer Customer object to be update and saved.
	 * @return Redirect to customer management interface.
	 */
	@RequestMapping(value={"/customers/{username}"}, method=RequestMethod.POST)
	public String updateCustomer(Customer customer) {
		logger.info("POST request to '/customers/'" + customer.getUsername());
		
		customerService.save(customer);
		
		return "redirect:/customers";
	}
	
	/**
	 * Update customer's account balance.
	 * 
	 * @param request HttpServletRequest object used to obtain hidden input fields.
	 * @param username String value representing the customer's username.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to edit customer details interface.
	 */
	@RequestMapping(value={"/balance/{username}"}, method=RequestMethod.POST)
	public String updateBalance(HttpServletRequest request, @PathVariable(value="username") String username, 
			RedirectAttributes attributes) {
		Customer customer = customerService.get(username).get(0);
		logger.info("POST request to '/balance/" + customer.getUsername() + "'");
		
		String amountString = request.getParameter("amount");
		double amount = Double.parseDouble(amountString);

		// process deposit
		if (request.getParameter("deposit") != null) {
			customer.setCredit(customer.getCredit() + amount);
			customerService.save(customer);
			attributes.addFlashAttribute("successMessage", "New account balance: " + customer.getCredit());
			return "redirect:/customers/" + username;
		}
		// process withdrawal
		else if (request.getParameter("withdraw") != null) {
			if (customer.getCredit() >= amount) {
				customer.setCredit(customer.getCredit() - amount);
				customerService.save(customer);
				attributes.addFlashAttribute("successMessage", "New account balance: " + customer.getCredit());
				return "redirect:/customers/" + username;
			}
			// not enough credit
			else {
				attributes.addFlashAttribute("errorMessage", "Insufficient Credit - Max withdrawal: " + customer.getCredit());
				return "redirect:/customers/" + username;
			}
		}
		// an error occurred
		else {
			logger.info("deposit/withdrawal not found");
			attributes.addFlashAttribute("errorMessage", "Sorry, an error occurred...");
			return "redirect:/customers/" + username;
		}
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
	
	/**
	 * Process addition of new system user and saves to database.
	 * 
	 * @param user User to be added to database.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @return Redirect to admin interface, tab 3.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/users"}, method=RequestMethod.POST)
	public String addUser(User user, RedirectAttributes attributes) {
		logger.info("POST request to '/users'");
		
		List<User> allUsers = userService.findAll();
		for (User u: allUsers) {
			if (u.getUsername().equals(user.getUsername())) {
				attributes.addFlashAttribute("addUserErrorMessage", "User with name " + user.getUsername() + " already exists!");
				return "redirect:/admin?tab=3";
			}
		}
		
		logger.info("Adding user...");
		userService.save(user);
		logger.info("User added!");
		logger.info(" Redirecting to /admin");
		
		attributes.addFlashAttribute("successMessage", "New User added!");
		return "redirect:/admin?tab=3";
	}
	
	/**
	 * Process deletion of user account.
	 * 
	 * @param principal Principal object user to obtain logged in user details.
	 * @param attributes RedirectAttributes object used to pass error message.
	 * @param username String represnting username of user to be deleted.
	 * @return Redirect to admin interface, tab 4.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value={"/users/delete/{username}"}, method=RequestMethod.POST)
	public String deleteUser(Principal principal, RedirectAttributes attributes, @PathVariable(value="username") String username) {
		logger.info("POST request to /users/delete/'" + username + "'");
		
		if (username.equals(principal.getName())) {
			attributes.addAttribute("errorDeleteMessage", "Cannot delete the logged in User!");
			return "redirect:/admin?tab=4";
		}
		
		userService.delete(username);
		attributes.addFlashAttribute("successDeleteMessage", "User deleted!");
		return "redirect:/admin?tab=4";
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
