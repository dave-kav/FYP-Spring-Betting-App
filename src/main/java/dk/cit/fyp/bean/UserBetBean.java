package dk.cit.fyp.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.User;

public class UserBetBean {
	
	private final static Logger logger = Logger.getLogger(UserBetBean.class);
	
	private Map<String,Bet> userBetMap = new HashMap<>();

	public Map<String,Bet> getUserBetMap() {
		return userBetMap;
	}

	public void setUserBetMap(Map<String,Bet> userBetMap) {
		this.userBetMap = userBetMap;
	}
	
	public void addUser(User user) {
		userBetMap.get(user);
	}
	
	public void setBet(String user, Bet bet) {
		logger.info("setting user bet");
		logger.info(user.toString());
		logger.info(bet.toString());
		userBetMap.put(user, bet);
	}

}
