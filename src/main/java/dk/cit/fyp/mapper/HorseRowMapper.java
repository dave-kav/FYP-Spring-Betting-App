package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Horse;

/**
 * Convert jdbc result set into Horse objects.
 * 
 * @author Dave Kavanagh
 *
 */
public class HorseRowMapper implements RowMapper<Horse> {

	@Override
	public Horse mapRow(ResultSet rs, int rowNum) throws SQLException {
		Horse h =  new Horse();
		
		h.setSelectionID(rs.getInt("Selection_id"));
		h.setName(rs.getString("Name"));
		h.setRaceID(rs.getInt("Race_id"));
		h.setNumber(rs.getInt("Number"));
		
		return h;
	}
}
