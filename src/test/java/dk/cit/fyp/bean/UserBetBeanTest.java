package dk.cit.fyp.bean;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import dk.cit.fyp.domain.Bet;

public class UserBetBeanTest {
	
	private Map<String,Bet> userBetMap = new HashMap<>();
	Bet b = new Bet();
	String userA = "kav87";
	String userB = "dkav87";
	
	@Before
	public void setup() {
		userBetMap.put(userA, b);
		userBetMap.put(userB, b);
	}	

	@Test
	public void testContains() {
		assertNotNull(userBetMap.containsKey(userA));
		assertFalse(userBetMap.containsKey("no_user"));
	}

	@Test
	public void testGetBet() {
		assertNotNull(userBetMap.get(userA));
		assertNull(userBetMap.get("no_user"));
	}

	@Test
	public void testRemove() {
		userBetMap.remove(userB);
		assertNull(userBetMap.get(userB));
	}
}
