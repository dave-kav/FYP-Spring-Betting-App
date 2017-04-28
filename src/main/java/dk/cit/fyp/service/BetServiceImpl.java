package dk.cit.fyp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.domain.Status;
import dk.cit.fyp.repo.BetDAO;

@Service
public class BetServiceImpl implements BetService {
	
	private final static Logger logger = Logger.getLogger(BetServiceImpl.class);
	@Autowired
	private BetDAO betRepo;
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	public BetServiceImpl(BetDAO betRepo, CustomerService customerService) {
		this.betRepo = betRepo;
		this.customerService = customerService;
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
	public long saveRest(Bet bet) {
		return betRepo.saveRest(bet);
	}

	@Override
	public List<Bet> top() {
		return betRepo.top();
	}
	
	@Override
	public int getNumUntranslated() {
		return betRepo.getNumUntranslated();
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
	
	@Override
	public void onScreen(Bet bet) {
		logger.info("onScreen:" + bet.toString());
		betRepo.onScreen(bet);
	}
	
	@Override
	public void offScreen(Bet bet) {
		logger.info("offScreen:" + bet.toString());
		betRepo.offScreen(bet);
	}
	
	@Override
	public List<Bet> getWinBets(Race race) {
		return betRepo.getWinBets(race);
	}

	@Override
	public List<Bet> getEachWayBets(Race race) {
		return betRepo.getEachWayBets(race);
	}

	/**
	 * Settle all bets on a given race
	 */
	@Override
	public void settleBets(Race race) {
		Horse winner = race.getWinner();
		List<Bet> winBets = getWinBets(race);
		
		if (winBets.size() > 0) {
			for (Bet b: winBets) 
				settleWin(b, winner);
		}
		
		if (race.getPlaces() > 1) {
			
			List<Bet> placeBets = getEachWayBets(race);
			
			List<Horse> winAndPlace = new ArrayList<>();
			winAndPlace.add(race.getWinner());
			for (Horse h: race.getPlacedHorses())
				winAndPlace.add(h);
			
			if (placeBets.size() > 0) {
				for (Bet b: placeBets) {
					settleEachWay(b, race, winAndPlace);
				}
			}
		}
	}
	
	@Override
	public double getWinBasicFactor(String oddString) {
		String odds[] = oddString.split("/");
		double oddVals[] = {Double.parseDouble(odds[0]), Double.parseDouble(odds[1])};
		return (oddVals[0] / oddVals[1]) + 1;
	}
	
	@Override
	public double getEachWayBasicFactor(String oddString, double terms) {
		String odds[] = oddString.split("/");
		double oddVals[] = {Double.parseDouble(odds[0]), Double.parseDouble(odds[1])};
		
		return ((oddVals[0] / oddVals[1]) * terms) + 1 ;
	}
	
	@Override
	public double winSettle(String odds, double stake) {
		double winnings = 0;
				
		double basicFactor = getWinBasicFactor(odds); 
		winnings =  basicFactor * stake;  
		winnings = Math.round(winnings * 100.0) / 100.0;
		
		return winnings;
	}
	
	@Override
	public double eachWaySettle(String odds, double stake, double terms) {
		double winnings = 0;
		
		stake = stake / 2;			
		double basicFactor = getEachWayBasicFactor(odds, terms);
				
		winnings =  basicFactor * stake;  
		winnings = Math.round(winnings * 100.0) / 100.0;
		
		return winnings;
	}
	
	/**
	 * Settles 'win only' bets. Formula used to calculate applies business logic of 'basic factors'
	 * @param bet The bet on which to calculate winnings if appropriate and set status
	 * @param winner winning horse in race to which bet applies
	 */
	@Async
	protected void settleWin(Bet bet, Horse winner) {					
		//check horse on which bet has been placed is the winner of the race 
		if (Integer.parseInt(bet.getSelection()) == winner.getSelectionID()) {
			//business logic of calculating winnings using basic factor
			double winnings = winSettle(bet.getOdds(), bet.getStake());
			bet.setWinnings(winnings);
			bet.setStatus(Status.WINNER);
			if (!bet.getCustomerID().equals("0")) {
				Customer c = customerService.get(bet.getCustomerID()).get(0);
				c.setCredit(c.getCredit() + bet.getWinnings());
				customerService.save(c);
				bet.setPaid(true);
				logger.info("settleWin: " + bet.toString());
			}
			betRepo.save(bet);
		}
		else {
			bet.setStatus(Status.LOSER);
			betRepo.save(bet);
		}
	}
	
	/**
	 * Settle each way bets. An each way bet consists of two individual bets - a win bet and a place bet, with half
	 * the stake being applied to each. Checks if the win part has of the bet has been successfully and settles that 
	 * portion of the bet using win logic. The each way part is calculated using different business logic.
	 * @param bet The bet on which to calculate winnings if appropriate and set status
	 * @param winner The bet on which to calculate win part of each way bet
	 * @param placedHorses Horses that placed in the race, used to settle place part of each way bet
	 */
	@Async
	protected void settleEachWay(Bet bet, Race race, List<Horse> winAndPlace) {
		logger.info("bet: " + bet.getBetID());
		double winnings = 0;
		boolean placed = false;
		
		//calculate place winnings
		for (Horse h: winAndPlace) {
			if (Integer.parseInt(bet.getSelection()) == h.getSelectionID()) {

				winnings = eachWaySettle(bet.getOdds(), bet.getStake(), race.getTerms());
				logger.info("place winnings: " + winnings);
				logger.info("rounded: " + winnings);
				bet.setWinnings(winnings);
				bet.setStatus(Status.PLACED);
				logger.info("settleEachWay - placed: " + bet.toString());
				placed = true;
				break;
			}
		}
		
		//calculate win part if applicable
		if (Integer.parseInt(bet.getSelection()) == race.getWinner().getSelectionID()) {
			winnings += winSettle(bet.getOdds(), bet.getStake()/2);
			logger.info("win winnings: " + winnings);
			bet.setWinnings(winnings);
			logger.info("rounded: " + winnings);
			bet.setStatus(Status.WINNER);
		}
		else {
			if (!placed) {
				bet.setStatus(Status.LOSER);				
			}
		}
		
		if (!bet.getCustomerID().equals("0") && bet.getStatus() != Status.LOSER) {
			Customer c = customerService.get(bet.getCustomerID()).get(0);
			c.setCredit(c.getCredit() + bet.getWinnings());
			customerService.save(c);
			bet.setPaid(true);
		}
		betRepo.save(bet);
	}

	@Override
	public List<Bet> getCustomerBets(String customerID) {
		return betRepo.getCustomerBets(customerID);
	}
	
	@Override
	@Async
	public void unsettleBets(int raceID) {
		List<Bet> bets = betRepo.getAllUnpaid(raceID);
		Customer customer;
		
		for (Bet bet: bets) {			
			if (bet.getCustomerID() != null && !bet.getCustomerID().equals("0")) {
				customer = customerService.get(bet.getCustomerID()).get(0);
				logger.info(customer.toString());
				customer.setCredit(customer.getCredit() - bet.getWinnings());
				customerService.save(customer);
				logger.info(customer.toString());
			}
			
			logger.info(bet.toString());
			bet.setStatus(Status.OPEN);
			bet.setWinnings(0.00);
			save(bet);
			logger.info(bet.toString());
		}
	}
}
