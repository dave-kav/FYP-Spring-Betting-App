package dk.cit.fyp.repo;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.mapper.BetRowMapper;

@Repository
public class JdbcBetRepo implements BetDAO {
	
	private JdbcTemplate jdbcTemplate;
	private final static Logger logger = Logger.getLogger(JdbcBetRepo.class);
	
	@Autowired
	public JdbcBetRepo(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Bet get(int id) {
		String sql = "SELECT * FROM Bets WHERE Bet_id = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {id}, new BetRowMapper()); 
	}

	@Override
	public void save(Bet bet) {
		if (bet.getBetID() != 0)
			update(bet);
		else
			add(bet);
	}
	
	private void add(Bet bet) {
		String sql = "INSERT INTO Bets (Stake, Online_bet, Image, Translated) "
				+ "VALUES (?, ?, ?, ?)";
		
		jdbcTemplate.update(sql, new Object[] {bet.getStake(), 
				bet.isOnlineBet(), bet.getImage(), bet.isTranslated()});
		
		logger.info("Added bet to database.");
	}
	
	private void update(Bet bet) {
		String sql = "UPDATE Bets SET Selection = ?, Race_id = ?, Stake = ?, Winner = ?,"
				+ "Translated = ?, Online_bet = ?, Winnings = ?,"
				+ "Image = ?, Monitored = ?, Open = ?, Each_way =? WHERE Bet_id = ?";
		
		jdbcTemplate.update(sql, new Object[] {bet.getSelection(), bet.getRaceID(), bet.getStake(), 
				bet.isWinner(), bet.isTranslated(), bet.isOnlineBet(), bet.getWinnings(), bet.getImage(), 
				bet.isMonitoredCustomer(), bet.isOpen(), bet.isEachWay(), bet.getBetID()});
	}
	
	@Override
	public List<Bet> top() {
		String sql 	= "SELECT * FROM "
					+ "Bets WHERE Translated=0 "
					+ "ORDER BY Bet_id "
					+ "LIMIT 1;";
		return jdbcTemplate.query(sql, new BetRowMapper());
	}
	
	@Override
	public int getNumUntranslated() {
		String sql = "SELECT COUNT(*) FROM Bets "
					+ "WHERE Translated=0";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	@Override
	public List<Bet> findAll() {
		String sql = "SELECT * FROM Bets";		
		return jdbcTemplate.query(sql, new BetRowMapper());
	}

	@Override
	public List<Bet> findAllOpen() {
		String sql = "SELECT * FROM Bets WHERE Open == 1";
		return jdbcTemplate.query(sql, new BetRowMapper());
	}

	@Override
	public List<Bet> findAllPaid() {
		String sql = "SELECT * FROM Bets WHERE Winner == 1 AND Paid == 1";
		return jdbcTemplate.query(sql, new BetRowMapper());
	}

	@Override
	public List<Bet> findAllUnpaid() {
		String sql = "SELECT * FROM Bets WHERE Winner == 1 AND Paid == 0";
		return jdbcTemplate.query(sql, new BetRowMapper());
	}

}
