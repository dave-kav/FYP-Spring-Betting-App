package dk.cit.fyp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dk.cit.fyp.interceptor.ReleaseImageInterceptor;

/**
 * Configuration class to intercept HTTP requests. Used in management of queue of
 * untranslated bets.
 * 
 * @author Dave Kavanagh
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

	/**
	 * Adds interceptor to registry of interceptors which pre processes requests 
	 * on all mappings except the login page.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(releaseImageInterceptor()).addPathPatterns("/**").excludePathPatterns("/api/**");
	}
	
	/**
	 * Create instance of UserBetBean and assign it to Spring as a Bean.
	 * 
	 * @return ReleaseImageInterceptor instance.
	 */
	@Bean
	public ReleaseImageInterceptor releaseImageInterceptor() {
		return new ReleaseImageInterceptor();
	}
}
