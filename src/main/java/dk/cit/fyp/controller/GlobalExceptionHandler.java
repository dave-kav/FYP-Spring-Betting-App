package dk.cit.fyp.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Exception handler used for validating correct file-type and permitted size upload. 
 * 
 * @author davyk
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MultipartException.class)
	public String handleMaxFileUpload(MultipartException e, RedirectAttributes attributes) {
		
		attributes.addFlashAttribute("wrongFile", "Please select a '.jpg' file.");
		
		return "redirect:upload";
	}
}
