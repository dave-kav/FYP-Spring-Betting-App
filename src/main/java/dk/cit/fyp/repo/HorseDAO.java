package dk.cit.fyp.repo;

import java.util.List;

import dk.cit.fyp.domain.Horse;

public interface HorseDAO {
	
	Horse getByID(int selectionID);
	
	List<Horse> get(String name);
	
	void save(Horse horse);
	
	List<Horse> findAll();

	List<Horse> getHorsesInRace(int raceID);

	List<Horse> getHorses();

}
