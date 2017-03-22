package dk.cit.fyp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.repo.HorseDAO;

@Service
public class HorseServiceImpl implements HorseService {

	private HorseDAO horseRepo;
	
	@Override
	public Horse getById(int selectionID) {
		return horseRepo.getByID(selectionID);
	}
	
	@Autowired
	public HorseServiceImpl(HorseDAO horseRepo) {
		this.horseRepo = horseRepo;
	}
	
	@Override
	public List<Horse> get(String name) {
		return horseRepo.get(name);
	}

	@Override
	public void save(Horse horse) {
		horseRepo.save(horse);
	}

	@Override
	public List<Horse> findAll() {
		return horseRepo.findAll();
	}

	@Override
	public List<Horse> getHorsesInRace(int raceID) {
		return horseRepo.getHorsesInRace(raceID);
	}

	@Override
	public List<Horse> getRaceRunners(int runners) {
		List<Horse> horses = new ArrayList<Horse>();
		for (int i= 0; i < runners; i++) {
			Horse horse = new Horse();
			horse.setNumber(i + 1);
			horses.add(horse);
		}

		return horses;
	}

	@Override
	public List<Horse> getHorses() {
		return horseRepo.getHorses();
	}
}
