package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.Bet;

public interface BetService {

	Bet get(int id);
	
	void save(Bet bet);
	
	List<Bet> top();
	
	List<Bet> findAll();
	
	List<Bet> findAllOpen();
	
	List<Bet> findAllPaid();
	
	List<Bet> findAllUnpaid();	
	
}
