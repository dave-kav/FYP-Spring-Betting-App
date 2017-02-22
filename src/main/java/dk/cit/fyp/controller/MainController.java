package dk.cit.fyp.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.Base64;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import dk.cit.fyp.service.ImageService;

@Controller
public class MainController {
	
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
			@RequestParam MultipartFile file,HttpSession session) {
		
		logger.info("POST request to '/upload'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("uploadPage", true);
		
		// save image to file system
		String path = session.getServletContext().getRealPath("/");  
        String fileName = file.getOriginalFilename();  
          
        try{  
        	byte barr[] = file.getBytes();  
	          
	        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path+"/"+fileName));  
	        bout.write(barr);  
	        bout.flush();  
	        bout.close();  
	          
        } catch(Exception e) {
        	System.out.println(e);
        }
        
        String filePath = path  + fileName;
        logger.info(filePath);
        
        byte[] fileBytes =  imgService.getBytes(filePath);
        byte[] encoded=Base64.getEncoder().encode(fileBytes);
        String encodedString = new String(encoded);

        String imgSrc = "data:image/jpeg;base64," + encodedString;
        
        model.addAttribute("img", true);
		model.addAttribute("imgSrc", imgSrc);
		
        return "upload";  
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
	
	@RequestMapping(value={"/review"}, method=RequestMethod.GET)
	public String showReviewPage(Model model, Principal principal) {
		logger.info("GET request to '/review'");
		model.addAttribute("userName", principal.getName());
		model.addAttribute("reviewPage", true);
		return "review";
	}

}
