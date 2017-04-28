package dk.cit.fyp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class BetServiceImplTest {
	
	
	BetService betService = new BetServiceImpl(null, null);
	
	String oddsA = "2/1";
	String oddsB = "5/1";
	
	double termsA = 0.2;
	double termsB = 0.25;
	
	double stakeA = 10;
	double stakeB = 25;

	@Test
	public void testGetWinBasicFactor() {
		double calculatedOdds = betService.getWinBasicFactor(oddsA);
		assertEquals(calculatedOdds, 3.0, 0);
		assertNotEquals(calculatedOdds, 4.0, 0);
		
		calculatedOdds = betService.getWinBasicFactor(oddsB);
		assertEquals(calculatedOdds, 6.0, 0);
		assertNotEquals(calculatedOdds, 4.0, 0);		
	}

	@Test
	public void testGetEachWayBasicFactor() {
		double calculatedOdds = betService.getEachWayBasicFactor(oddsA, termsA);
		assertEquals(calculatedOdds, 1.4, 0);
		assertNotEquals(calculatedOdds, 4.1, 0);
		
		calculatedOdds = betService.getEachWayBasicFactor(oddsB, termsB);
		assertEquals(calculatedOdds, 2.25, 0);
		assertNotEquals(calculatedOdds, 5.1, 0);
	}

	@Test
	public void testWinSettle() {
		double winnings = betService.winSettle(oddsA, 10);
		assertEquals(30, winnings, 0);
		assertNotEquals(50, winnings, 0);

		winnings = betService.winSettle(oddsB, 25);
		assertEquals(150, winnings, 0);
		assertNotEquals(50, winnings, 0);
	}

	@Test
	public void testEachWaySettle() {
		double winnings = betService.eachWaySettle(oddsA, stakeA, termsA);
		assertEquals(7, winnings, 0);
		assertNotEquals(50, winnings, 0);

		winnings = betService.eachWaySettle(oddsB, stakeB, termsB);
		assertEquals(28.13, winnings, 0);
		assertNotEquals(50, winnings, 0);
	}

}
