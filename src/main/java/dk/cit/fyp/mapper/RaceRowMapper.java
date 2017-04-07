package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Race;

/**
 * Convert jdbc result set into Race objects.
 * 
 * @author Dave Kavanagh
 *
 */
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
		r.setWinnerID(rs.getInt("Winner"));
		List<Integer> places = new ArrayList<>();
		places.add(rs.getInt("Place1"));
		places.add(rs.getInt("Place2"));
		places.add(rs.getInt("Place3"));
		r.setPlacedHorseIDs(places);
		return r;
	}
}
