package dk.cit.fyp.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MultipartException.class)
	public String handleMaxFileUpload(MultipartException e, RedirectAttributes attributes) {
		
		attributes.addFlashAttribute("wrongFile", "Please select a '.jpg' file.");
		
		return "redirect:upload";
	}
	
}
