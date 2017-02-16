package dk.cit.fyp.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	DataSource dataSource;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()       
		        .antMatchers("/styles/**").permitAll()
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
	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth
    		.jdbcAuthentication()
    		.dataSource(dataSource);
    }
}