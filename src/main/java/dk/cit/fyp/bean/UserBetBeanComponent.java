package dk.cit.fyp.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserBetBeanComponent {
	
	@Bean
	UserBetBean userBetBean() {
		return new UserBetBean();
	}

}
