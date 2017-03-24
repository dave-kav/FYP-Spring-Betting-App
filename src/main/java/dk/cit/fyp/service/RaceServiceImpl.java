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
		int runners = race.getRunners();
		int places;
		double terms;
		
		//business logic to calculate each way bet places and terms
		if (runners < 4) {
			places = 1;
			terms = 0;
		} else if (runners < 8) {
			places = 2;
			terms = 0.25;
		} else if (runners < 12) {
			places = 3;
			terms = 0.2;
		} else if (runners < 16) {
			places = 3; 
			terms = 0.25;
		} else {
			places = 4; 
			terms = 0.25;
		}	
		
		race.setPlaces(places);
		race.setTerms(terms);
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

	@Override
	public List<String> getTracks() {
		return raceRepo.getTracks();
	}
	
	@Override
	public List<String> getTimes() {
		return raceRepo.getTimes();
	}

	@Override
	public List<String> getTimesByTrack(String track) {
		return raceRepo.getTimesByTrack(track);
	}
}
