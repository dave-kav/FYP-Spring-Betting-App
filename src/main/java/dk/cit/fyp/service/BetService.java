package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Race;

public interface BetService {

	Bet get(int id);
	
	void save(Bet bet);
	
	long saveRest(Bet bet);
	
	List<Bet> top();
	
	int getNumUntranslated();
	
	List<Bet> findAll();
	
	List<Bet> findAllOpen();
	
	List<Bet> findAllPaid();
	
	List<Bet> findAllUnpaid();	

	void onScreen(Bet bet);

	void offScreen(Bet bet);

	void settleBets(Race race);
	
	public double getWinBasicFactor(String oddString);
	
	public double getEachWayBasicFactor(String oddString, double terms);
	
	public double winSettle(String odds, double stake);
	
	public double eachWaySettle(String odds, double stake, double terms);
	
	List<Bet> getWinBets(Race race);
	
	List<Bet> getEachWayBets(Race race);
	
	List<Bet> getCustomerBets(String customerID);

	void unsettleBets(int raceID);
}
