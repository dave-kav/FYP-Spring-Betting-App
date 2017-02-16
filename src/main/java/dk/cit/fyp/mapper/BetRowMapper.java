package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Bet;

public class BetRowMapper implements RowMapper<Bet>{

	@Override
	public Bet mapRow(ResultSet rs, int rowNum) throws SQLException {
		Bet b = new Bet();
		
		b.setBetID(rs.getInt("Bet_id"));
		b.setSelection(rs.getString("Selection"));
		b.setRaceID(rs.getInt("Race_id"));
		b.setStake(rs.getFloat("Stake"));
		b.setWinner(rs.getBoolean("Winner"));
		b.setTranslated(rs.getBoolean("Translated"));
		b.setTranslatedManually(rs.getBoolean("Manual_translated"));
		b.setOnlineBet(rs.getBoolean("Online_bet"));
		b.setWinnings(rs.getFloat("Winnings"));
		b.setImage(rs.getString("Image"));
		b.setMonitoredCustomer(rs.getBoolean("Monitored"));
		
		return b;
	}

}
