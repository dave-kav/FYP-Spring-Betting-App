package dk.cit.fyp;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
/**
 * Main driver of Spring Boot Betting Application.
 * 
 * @author Dave Kavanagh
 *
 */
@SpringBootApplication
@EnableAsync
public class BettingApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {

	@Override
	public void run(String... arg0) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(BettingApplication.class);
	}

}