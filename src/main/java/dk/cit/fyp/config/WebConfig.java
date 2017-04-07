package dk.cit.fyp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dk.cit.fyp.interceptor.ReleaseImageInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(releaseImageInterceptor()).addPathPatterns("/**").excludePathPatterns("/login");
	}
	
	@Bean
	public ReleaseImageInterceptor releaseImageInterceptor() {
		return new ReleaseImageInterceptor();
	}
}
