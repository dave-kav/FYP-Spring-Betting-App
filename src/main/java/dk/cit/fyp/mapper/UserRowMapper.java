package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.User;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User e = new User();
		
		e.setUsername(rs.getString("Username"));
		e.setPassword(rs.getString("Password"));
		
		boolean admin = rs.getString("authority").equals("ADMIN") ? true : false;
		e.setAdmin(admin);
		
		return e;
	}

}
