package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.Race;

public interface RaceService {
	
	Race get(int raceID);
	
	void save(Race race);
	
	List<Race> findAll();

}
