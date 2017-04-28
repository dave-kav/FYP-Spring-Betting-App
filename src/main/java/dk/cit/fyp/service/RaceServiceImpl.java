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
	
	/**
	 * Business logic to calculate each way places
	 * @param runners
	 * @return
	 */
	public int getPlaces(int runners) {
		if (runners < 4) {
			return 1;
		} else if (runners < 8) {
			return 2;
		} else if (runners < 12) {
			return 3;
		} else if (runners < 16) {
			return 3; 
		} else {
			return 4; 
		}	
	}
	/**
	 * Business logic to calculate each way bet terms
	 * @param runners - runners in race
	 * @return each way terms
	 */
	public double getTerms(int runners) {
		if (runners < 4) {
			return  0;
		} else if (runners < 8) {
			return  0.25;
		} else if (runners < 12) {
			return  0.2;
		} else if (runners < 16) {
			return  0.25;
		} else {
			return 0.25;
		}	
	}

	@Override
	public void save(Race race) {
		int runners = race.getRunners();
		int places = getPlaces(runners);
		double terms = getTerms(runners);
				
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
	public List<Race> getRaceTimes() {
		return raceRepo.getRaceTimes();
	}

	@Override
	public List<String> getTimesByTrack(String track) {
		return raceRepo.getTimesByTrack(track);
	}
}
