/*package dk.cit.fyp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {
	
	private final static Logger logger = Logger.getLogger(ErrorController.class);
	
	@RequestMapping(value = "error", method = RequestMethod.GET)
    public String renderErrorPage(HttpServletRequest httpRequest, Model model) {
         
        String error = "";
        int httpErrorCode = getErrorCode(httpRequest);
        logger.info(httpErrorCode);
 
        switch (httpErrorCode) {
            case 400: {
                error = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                error = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                error = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                error = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }
        model.addAttribute("customError", error);
        return "error";
    }
     
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
          .getAttribute("javax.servlet.error.status_code");
    }

}

*/