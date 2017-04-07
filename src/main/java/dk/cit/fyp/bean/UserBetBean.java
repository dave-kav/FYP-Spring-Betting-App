package dk.cit.fyp.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.User;

/**
 * This class is used to manage the queue of untranslated bets, so that multiple users are not 
 * presented with the same bet simultaneously. On page load, the logged in user is assigned a bet
 * and the mapping of username -> bet is stored in this map. A flag is set on the bet object in the 
 * DB so that it cannot be assigned to another user. If the user leaves the page then the flag is 
 * reset and the mapping is removed and the bet returns to the queue. 
 * 
 * @author Dave Kavanagh
 *
 */
public class UserBetBean {
	
	private final static Logger logger = Logger.getLogger(UserBetBean.class);
	
	private Map<String,Bet> userBetMap = new HashMap<>();
	
	/**
	 * Add a User - Bet mapping to the queue management map.
	 * @param username String value representing the logged in user's username.
	 * @param bet Bet object that the logged in user is currently translating.
	 */
	public void setBet(String username, Bet bet) {
		userBetMap.put(username, bet);
	}

	/**
	 * Check if there is a mapping in the queue for a logged in user.
	 * @param username  String value representing the logged in user's username.
	 * @return True if user entry assigned to queue, false if not.
	 */
	public boolean contains(String username) {
		return userBetMap.containsKey(username);
	}
	
	/**
	 * Retrieve the bet that the logged in user is translating.
	 * @param username  String value representing the logged in user's username.
	 * @return Bet object being translated.
	 */
	public Bet getBet(String username) {
		return userBetMap.get(username);
	}
	
	/**
	 * Remove a mapping for a user in the queue if user leaves translate page
	 * @param username  String value representing the logged in user's username
	 */
	public void remove(String username) {
		userBetMap.remove(username);
	}
}
