package dk.cit.fyp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import dk.cit.fyp.bean.UserBetBean;
import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.User;
import dk.cit.fyp.service.BetService;

@Component
public class ReleaseImageInterceptor implements HandlerInterceptor {

	private final static Logger logger = Logger.getLogger(ReleaseImageInterceptor.class);

	@Autowired
	BetService betService;
	@Autowired
	UserBetBean userBetBean;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username;
		Bet bet = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails)principal).getUsername();
		} else {
			username = principal.toString();
		}
		logger.info(username);
		

		if (userBetBean.getUserBetMap().containsKey(username)) {
			bet = userBetBean.getUserBetMap().get(username);
		} else {
			logger.info(username + " not in userbetmap");
		}

		if (bet != null) {
			betService.offScreen(bet);
			userBetBean.getUserBetMap().remove(username);
			logger.info("setting bet off screen");
		} else {
			logger.info("null bet");
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
