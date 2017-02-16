package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.repo.HorseDAO;

@Service
public class HorseServiceImpl implements HorseService {

	private HorseDAO horseRepo;
	
	@Autowired
	public HorseServiceImpl(HorseDAO horseRepo) {
		this.horseRepo = horseRepo;
	}
	
	@Override
	public Horse get(String name) {
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

}
