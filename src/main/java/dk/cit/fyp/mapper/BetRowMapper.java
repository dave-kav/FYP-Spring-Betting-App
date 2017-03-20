package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Bet;
import dk.cit.fyp.domain.Status;

public class BetRowMapper implements RowMapper<Bet>{

	@Override
	public Bet mapRow(ResultSet rs, int rowNum) throws SQLException {
		Bet b = new Bet();
		
		b.setBetID(rs.getInt("Bet_id"));
		Timestamp date = rs.getTimestamp("Time_placed");
		String dateString = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(date);
		b.setTimePlaced(dateString);
		b.setSelection(rs.getString("Selection_id"));
		b.setRace_id(rs.getInt("Race_id"));
		b.setEachWay(rs.getBoolean("Each_way"));
		b.setStake(rs.getFloat("Stake"));
		b.setTranslated(rs.getBoolean("Translated"));
		b.setOnlineBet(rs.getBoolean("Online_bet"));
		b.setWinnings(rs.getFloat("Winnings"));
		b.setImagePath(rs.getString("Image"));
		b.setMonitoredCustomer(rs.getBoolean("Monitored"));
		b.setStatus(Status.valueOf(rs.getString("Status")));
		b.setOdds(rs.getInt("Odds_numerator") + "/" + rs.getInt("Odds_denominator"));
		
		return b;
	}
}
