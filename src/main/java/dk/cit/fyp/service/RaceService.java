package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.Race;

public interface RaceService {
	
	Race get(int raceID);
	
	void save(Race race);
	
	List<Race> find(String time);
	
	List<Race> findAll();

	List<String> getTracks();
	
	List<Race> getRaceTimes();

	List<String> getTimesByTrack(String track);

}
