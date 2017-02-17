package dk.cit.fyp;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.UserService;
import dk.cit.fyp.service.CustomerService;

@SpringBootApplication
public class BettingApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {
	
	@Autowired 
	CustomerService userRepo;
	@Autowired
	UserService employeeRepo;
	
	private final static Logger logger = Logger.getLogger(BettingApplication.class);

	@Override
	public void run(String... arg0) throws Exception {

		List<User> userList = employeeRepo.findAll();
		logger.info("Active Users:");
		for (User u: userList)
			logger.info(u.toString());
	}

	public static void main(String[] args) {
		SpringApplication.run(BettingApplication.class);
	}
}