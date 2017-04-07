package dk.cit.fyp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import dk.cit.fyp.bean.UserBetBean;

/**
 * Class used to configure the UserBetBean class as a Spring Bean to enable 
 * Autowiring dependency injection.
 * 
 * @author Dave Kavanagh
 *
 */
@Component
public class UserBetBeanConfig {
	
	/**
	 * Create instance of UserBetBean and assign it to Spring as a Bean.
	 * 
	 * @return UserBetBean instance.
	 */
	@Bean
	UserBetBean userBetBean() {
		return new UserBetBean();
	}

}
