package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Race;

public class RaceRowMapper implements RowMapper<Race> {

	@Override
	public Race mapRow(ResultSet rs, int rowNum) throws SQLException {
		Race r = new Race();
		
		r.setRaceID(rs.getInt("Race_id"));
		r.setTime(rs.getTime("Time").toString());
		r.setTrack(rs.getString("Racetrack"));
		r.setPlaces(rs.getInt("Places"));
		r.setTerms(rs.getDouble("Terms"));
		r.setRunners(rs.getInt("Runners"));
		
		return r;
	}
}
