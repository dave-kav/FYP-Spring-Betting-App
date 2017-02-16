package dk.cit.fyp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.EmployeeService;
import dk.cit.fyp.service.UserService;

@SpringBootApplication
public class BettingApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {
	
	@Autowired 
	UserService userRepo;
	@Autowired
	EmployeeService employeeRepo;

	@Override
	public void run(String... arg0) throws Exception {

		List<User> empList = employeeRepo.findAll();
		for (User e: empList)
			System.out.println(e.getUsername());
	}

	public static void main(String[] args) {
		SpringApplication.run(BettingApplication.class);
	}
}