package dk.cit.fyp.repo;

import java.util.List;

import dk.cit.fyp.domain.Horse;

public interface HorseDAO {
	
	Horse get(String name);
	
	void save(Horse horse);
	
	List<Horse> findAll();

}
