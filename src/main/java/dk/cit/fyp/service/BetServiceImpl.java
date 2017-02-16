package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.repo.BetDAO;

@Service
public class BetServiceImpl implements BetService {
	
	private BetDAO betRepo;
	
	@Autowired
	public BetServiceImpl(BetDAO betRepo) {
		this.betRepo = betRepo;
	}

	@Override
	public Bet get(int id) {
		return betRepo.get(id);
	}

	@Override
	public void save(Bet bet) {
		betRepo.save(bet);
	}

	@Override
	public List<Bet> findAll() {
		return betRepo.findAll();
	}

	@Override
	public List<Bet> findAllOpen() {
		return betRepo.findAllOpen();
	}

	@Override
	public List<Bet> findAllPaid() {
		return betRepo.findAllPaid();
	}

	@Override
	public List<Bet> findAllUnpaid() {
		return betRepo.findAllUnpaid();
	}

}
