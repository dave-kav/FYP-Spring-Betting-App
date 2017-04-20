package dk.cit.fyp.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Race;
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
		String sql = "INSERT INTO Bets (Stake, Online_bet, Image) "
				+ "VALUES (?, ?, ?)";
		
		jdbcTemplate.update(sql, new Object[] {bet.getStake(), 
				bet.isOnlineBet(), bet.getImagePath()});
		
		logger.info("Added bet to database.");
	}
	
	private void update(Bet bet) {
		String sql = "UPDATE Bets SET Selection_id = ?, Race_id = ?, Stake = ?, Translated = ?, "
				+ "Translated_by = ?, On_screen = ?, Online_bet = ?, Winnings = ?, Image = ?, Customer_id = ?, "
				+ "Each_way = ?, Odds_numerator = ?, Odds_denominator = ?, Status = ?, Paid = ? WHERE Bet_id = ?";
		
		String odds = bet.getOdds();
		String[] parts = odds.split("/");
		int numerator = Integer.parseInt(parts[0]);
		int denominator = Integer.parseInt(parts[1]);
				
		jdbcTemplate.update(sql, new Object[] {bet.getSelection(), bet.getRaceID(), bet.getStake(), bet.isTranslated(), 
				bet.getTranslatedBy(), false, bet.isOnlineBet(), bet.getWinnings(), bet.getImagePath(), bet.getCustomerID(), 
				bet.isEachWay(), numerator, denominator, bet.getStatus().toString(), bet.isPaid(), bet.getBetID()});
	}
	
	@Override
	public long saveRest(Bet bet) {
		String sql = "INSERT INTO Bets (Stake, Online_bet, Image, Selection_id, Race_id, Translated, Customer_id, "
				+ "Each_way, Odds_numerator, Odds_denominator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String odds = bet.getOdds();
		String[] parts = odds.split("/");
		int numerator = Integer.parseInt(parts[0]);
		int denominator = Integer.parseInt(parts[1]);
		
		// place bet and retrieve placed bet id
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setDouble(1, bet.getStake());
				ps.setBoolean(2, true);
				ps.setString(3, "null");
				ps.setString(4, bet.getSelection());
				ps.setInt(5, bet.getRaceID());
				ps.setBoolean(6, true);
				ps.setString(7, bet.getCustomerID());
				ps.setBoolean(8, Boolean.valueOf(bet.isEachWay()));
				ps.setInt(9, numerator);
				ps.setInt(10, denominator);
				return ps;
			}
		}, holder);
		
		Long id = holder.getKey().longValue();
		return id;
	}
	
	@Override
	public List<Bet> top() {
		String sql 	= "SELECT * FROM "
					+ "Bets WHERE Translated=0 AND On_screen=0 "
					+ "ORDER BY Bet_id "
					+ "LIMIT 1;";
		return jdbcTemplate.query(sql, new BetRowMapper());
	}
	
	@Override
	public int getNumUntranslated() {
		String sql = "SELECT COUNT(*) FROM Bets "
					+ "WHERE Translated=0 AND On_screen=0";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	@Override
	public List<Bet> findAll() {
		String sql = "SELECT * FROM Bets ORDER BY Bet_id DESC";		
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
	
	@Override 
	public void onScreen(Bet bet) {
		String sql = "UPDATE Bets SET On_screen = true WHERE Bet_id = ?";
		
		jdbcTemplate.update(sql, new Object[] {bet.getBetID()});
	}
	
	@Override 
	public void offScreen(Bet bet) {
		String sql = "UPDATE Bets SET On_screen = false WHERE Bet_id = ?";
		
		jdbcTemplate.update(sql, new Object[] {bet.getBetID()});
	}

	@Override
	public List<Bet> getWinBets(Race race) {
		String sql = "SELECT * FROM Bets Where Each_way = 0 AND Translated = 1 AND Status = 'OPEN' AND Race_id = ? AND Paid=0";
		return jdbcTemplate.query(sql, new Object[] {race.getRaceID()}, new BetRowMapper());
	}

	@Override
	public List<Bet> getEachWayBets(Race race) {
		String sql = "SELECT * FROM Bets Where Each_way = 1 AND Translated = 1 AND Status = 'OPEN' AND Race_id = ?";
		return jdbcTemplate.query(sql, new Object[] {race.getRaceID()}, new BetRowMapper());
	}
	
	@Override
	public List<Bet> getCustomerBets(String customerID) {
		String sql = "SELECT * FROM Bets WHERE Customer_id = ?";
		
		return jdbcTemplate.query(sql, new Object[] {customerID}, new BetRowMapper());
	}

	@Override
	public List<Bet> getAllUnpaid(int raceID) {
		String sql = "SELECT * FROM Bets WHERE Race_id=? AND Paid=0 "
				+ "UNION "
				+ "SELECT * FROM Bets WHERE Race_id=? AND Online_bet=1";
		
		return jdbcTemplate.query(sql, new Object[] {raceID, raceID}, new BetRowMapper());
	}
}
