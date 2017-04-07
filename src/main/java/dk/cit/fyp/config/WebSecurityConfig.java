package dk.cit.fyp.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import dk.cit.fyp.bean.UserBetBean;
import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.interceptor.ReleaseImageInterceptor;
import dk.cit.fyp.service.BetService;

/**
 * Configuration class for Spring Security.
 * 
 * @author Dave Kavanagh
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final static Logger logger = Logger.getLogger(ReleaseImageInterceptor.class);
	
	@Autowired
	BetService betService;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private UserBetBean userBetBean;
	
	/**
	 * Adds configuration on pages to authorize/restrict.
	 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()       
		        .antMatchers("/styles/**").permitAll()
		        .antMatchers("/api/**").permitAll()
		        .anyRequest().authenticated()
		        .and()
        	.formLogin()
	            .loginPage("/login")
	            .failureUrl("/login-error")
	            .successHandler(new AuthenticationSuccessHandler() {	
					@Override
					public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
							Authentication auth) throws IOException, ServletException {
						res.sendRedirect("translate");
					}
				})	
	            .permitAll()
	            .and()
	        .logout()
	            .permitAll()
	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).addLogoutHandler(new LogoutHandler() {
					//release bet and return to queue if user logs out while translating 
					@Override
					public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
						Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
						String username;
						Bet bet = null;
						if (principal instanceof UserDetails) {
							username = ((UserDetails)principal).getUsername();
						} else {
							username = principal.toString();
						}
						logger.info(username);
						
						if (userBetBean.contains(username)) {
							bet = userBetBean.getBet(username);
						} else {
							logger.info(username + " not in userbetmap");
						}

						if (bet != null) {
							betService.offScreen(bet);
							userBetBean.remove(username);
							logger.info("setting bet off screen");
						} else {
							logger.info("null bet");
						}
						
					}
				}).logoutSuccessUrl("/login")
            .and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth
    		.jdbcAuthentication()
    		.dataSource(dataSource);
    }
}