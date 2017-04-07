package dk.cit.fyp;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dk.cit.fyp.service.CustomerService;
import dk.cit.fyp.service.UserService;
/**
 * Main driver of Spring Boot Betting Application.
 * 
 * @author Dave Kavanagh
 *
 */
@SpringBootApplication
@EnableAsync
public class BettingApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {
	
	private final static Logger logger = Logger.getLogger(BettingApplication.class);

	@Override
	public void run(String... arg0) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(BettingApplication.class);
	}
}