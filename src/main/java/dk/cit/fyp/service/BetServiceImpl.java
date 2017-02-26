package dk.cit.fyp.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Race;
import dk.cit.fyp.repo.BetDAO;

@Service
public class BetServiceImpl implements BetService {
	
	private final static Logger logger = Logger.getLogger(BetServiceImpl.class);
	private BetDAO betRepo;
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

}
