package dk.cit.fyp.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Exception handler used for validating correct file-type and permitted size upload, 
 * and for null pointer on release image.
 * 
 * @author davyk
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private final static Logger logger = Logger.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(MultipartException.class)
	public String handleMaxFileUpload(MultipartException e, RedirectAttributes attributes) {
		
		attributes.addFlashAttribute("wrongFile", "Please select a '.jpg' file.");
		
		return "redirect:upload";
	}
	
	@ExceptionHandler(NullPointerException.class)
	public String handleReleaseImageException(NullPointerException e) {
		e.printStackTrace();
		return "error";
	}
}
