package dk.cit.fyp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import dk.cit.fyp.domain.Bet;
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
	private ImageService imgService;
	
	@Autowired
	public BetServiceImpl(BetDAO betRepo, ImageService imgService) {
		this.betRepo = betRepo;
		this.imgService = imgService;
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
	public List<Bet> top() {
		return betRepo.top();
	}
	
	@Override
	public int getNumUntranslated() {
		return betRepo.getNumUntranslated();
	}
	
	@Override
	public Model getNext(Model model) {
		List<Bet> bets = betRepo.top();
		if (bets.size() != 0) {
			Bet bet = bets.get(0);
			logger.info("Loading image for bet_id " + bet.getBetID());
			byte[] bytes = imgService.getBytes(bet.getImagePath());
			String imgSrc = imgService.getImageSource(bytes);
			
			model.addAttribute("imgSrc", imgSrc);
			model.addAttribute("img", true);
			model.addAttribute("bet", bet);
			model.addAttribute("race", new Race());
			model.addAttribute("queue", getNumUntranslated());
		}
		else {
			model.addAttribute("race", new Race());
			model.addAttribute("bet", new Bet());
		}
		return model;
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
		betRepo.onScreen(bet);
	}
	
	@Override
	public void offScreen(Bet bet) {
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
			if (placeBets.size() > 0) {
				for (Bet b: placeBets) {
					settleEachWay(b, race);
				}
			}
		}
	}
	
	/**
	 * Settles 'win only' bets. Formula used to calculate applies business logic of 'basic factors'
	 * @param bet The bet on which to calculate winnings if appropriate and set status
	 * @param winner winning horse in race to which bet applies
	 */
	@Async
	private double settleWin(Bet bet, Horse winner) {	
		logger.info("in settle win");
		double winnings = 0;
		double stake = bet.getStake();
		String odds[] = bet.getOdds().split("/");
		double oddVals[] = new double[2]; 
		oddVals[0] = Double.parseDouble(odds[0]);
		oddVals[1] = Double.parseDouble(odds[1]);
		
		//check horse on which bet has been placed is the winner of the race 
		if (Integer.parseInt(bet.getSelection()) == winner.getSelectionID()) {
			//business logic of calcualting winnings using basic factor
			winnings = ((oddVals[0] / oddVals[1]) + 1 ) * stake;  
			logger.info("Winnings calculated: " + stake + " @ " + bet.getOdds() + " = "  + winnings);
			
			bet.setWinnings(winnings);
			bet.setStatus(Status.WINNER);
			betRepo.save(bet);
		}
		else {
			logger.info("losing bet");
			bet.setStatus(Status.LOSER);
			betRepo.save(bet);
		}
		return winnings;
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
	private void settleEachWay(Bet bet, Race race) {
		double stake = bet.getStake() / 2;
		double winnings = 0;
		String odds[] = bet.getOdds().split("/");
		double oddVals[] = new double[2]; 
		oddVals[0] = Double.parseDouble(odds[0]);
		oddVals[1] = Double.parseDouble(odds[1]);
		double denom = oddVals[1] * race.getTerms();
		boolean placed = false;
		
		List<Horse> winAndPlace = new ArrayList<>();
		winAndPlace.add(race.getWinner());
		for (Horse h: race.getPlacedHorses())
			winAndPlace.add(h);
		
		//calculate place winnings
		for (Horse h: winAndPlace) {
			logger.info("selection: " +  bet.getSelection());
			logger.info("placed: " +  h.getSelectionID());
			if (Integer.parseInt(bet.getSelection()) == h.getSelectionID()) {

				winnings = ((oddVals[0] * denom) + 1 ) * stake;  
				logger.info("Place return calculated: " + stake + " @ " + bet.getOdds() + " = "  + winnings);
				
				bet.setWinnings(winnings);
				bet.setStatus(Status.PLACED);
				betRepo.save(bet);
				placed = true;
			}
		}
		
		logger.info("winnings after place calculation: " + winnings);
		//calculate win part if applicable
		if (Integer.parseInt(bet.getSelection()) == race.getWinner().getSelectionID()) {
			logger.info(stake);
			winnings += ((oddVals[0] / oddVals[1]) + 1 ) * stake;  
			logger.info("Each way Winnings calculated: " + stake + " @ " + bet.getOdds() + " = "  + winnings);
			bet.setWinnings(winnings);
			bet.setStatus(Status.WINNER);
			betRepo.save(bet);
		}
		else {
			if (!placed) {
				logger.info("loser place bet");
				bet.setStatus(Status.LOSER);
				betRepo.save(bet);
			}
		}
	}
}
