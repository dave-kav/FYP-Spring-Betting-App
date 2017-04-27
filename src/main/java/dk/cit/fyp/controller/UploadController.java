package dk.cit.fyp.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jgroups.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.service.BetService;
import dk.cit.fyp.service.ImageService;

@Controller
public class UploadController {
	
	private final static Logger logger = Logger.getLogger(UploadController.class);
	
	@Autowired
	AmazonS3Client s3;
	@Autowired 
	ImageService imgService;
	@Autowired
	BetService betService;
	
	@Value("${cloud.aws.bucket.name}")
    private String bucketName;

	
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
}
