package dk.cit.fyp.repo;

import java.util.List;

import dk.cit.fyp.domain.Bet;

public interface BetDAO {
	
	Bet get(int id);
	
	void save(Bet bet);
	
	List<Bet> top();
	
	int getNumUntranslated();
	
	List<Bet> findAll();
	
	List<Bet> findAllOpen();
	
	List<Bet> findAllPaid();
	
	List<Bet> findAllUnpaid();	
	
	void onScreen(Bet bet);

	void offScreen(Bet bet);
	
}
