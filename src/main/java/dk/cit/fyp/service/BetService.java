package dk.cit.fyp.service;

import java.util.List;

import org.springframework.ui.Model;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Race;

public interface BetService {

	Bet get(int id);
	
	void save(Bet bet);
	
	List<Bet> top();
	
	int getNumUntranslated();
	
	Model getNext(Model model);
	
	List<Bet> findAll();
	
	List<Bet> findAllOpen();
	
	List<Bet> findAllPaid();
	
	List<Bet> findAllUnpaid();	

	void onScreen(Bet bet);

	void offScreen(Bet bet);

	void settleBets(Race race);
	
	List<Bet> getWinBets(Race race);
	
	List<Bet> getEachWayBets(Race race);	
}
