package dk.cit.fyp.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class RaceServiceImplTest {
	
	RaceServiceImpl raceService = new RaceServiceImpl(null);
	
	@Test
	public void testGetPlaces() {
		assertEquals(raceService.getPlaces(3), 1, 0);
		assertEquals(raceService.getPlaces(8), 3, 0);
		assertEquals(raceService.getPlaces(20), 4, 0);
		
		assertNotEquals(raceService.getPlaces(8), 4, 0);
		assertNotEquals(raceService.getPlaces(20), 24, 0);
		assertNotEquals(raceService.getPlaces(11), 2, 0);
		
	}

	@Test
	public void testGetTerms() {
		assertEquals(raceService.getTerms(3), 0, 0);
		assertEquals(raceService.getTerms(7), 0.25, 0);
		assertEquals(raceService.getTerms(20), 0.25, 0);
		
		assertNotEquals(raceService.getTerms(3), 0.2, 0);
		assertNotEquals(raceService.getTerms(20), 0.2, 0);
		assertNotEquals(raceService.getTerms(4), 0.2, 0);
	}
}
