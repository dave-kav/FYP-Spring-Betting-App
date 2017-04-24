package dk.cit.fyp.repo;

import java.util.List;

import dk.cit.fyp.domain.Race;

public interface RaceDAO {

	Race get(int raceID);
	
	List<Race> find(String time);

	void save(Race race);

	List<Race> findAll();

	List<String> getTracks();

	List<Race> getRaceTimes();
	
	List<String> getTimesByTrack(String track);
}
