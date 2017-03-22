package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.Horse;

public interface HorseService {
	
	Horse getById(int selectionID);
	
	List<Horse> get(String name);
	
	void save(Horse horse);
	
	List<Horse> findAll();

	List<Horse> getHorsesInRace(int raceID);
	
	List<Horse> getRaceRunners(int runners);

	List<Horse> getHorses();

}
