package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Race;
import dk.cit.fyp.repo.RaceDAO;

@Service
public class RaceServiceImpl implements RaceService {
	
	private RaceDAO raceRepo;
	
	@Autowired
	public RaceServiceImpl(RaceDAO raceRepo) {
		this.raceRepo = raceRepo;
	}

	@Override
	public Race get(int raceID) {
		return raceRepo.get(raceID);
	}

	@Override
	public void save(Race race) {
		raceRepo.save(race);
	}

	@Override
	public List<Race> find(String time) {
		return raceRepo.find(time);
	}
	
	@Override
	public List<Race> findAll() {
		return raceRepo.findAll();
	}
}
