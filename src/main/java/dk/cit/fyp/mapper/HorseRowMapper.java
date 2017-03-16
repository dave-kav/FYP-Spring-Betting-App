package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Horse;

public class HorseRowMapper implements RowMapper<Horse> {

	@Override
	public Horse mapRow(ResultSet rs, int rowNum) throws SQLException {
		Horse h =  new Horse();
		
		h.setName(rs.getString("Name"));
		h.setRaceID(rs.getInt("Race_id"));
		h.setOddsDenominator(rs.getInt("odds_denominator"));
		h.setOddsEnumerator(rs.getInt("odds_numerator"));
		h.setNumber(rs.getInt("number"));
		
		return h;
	}
}
