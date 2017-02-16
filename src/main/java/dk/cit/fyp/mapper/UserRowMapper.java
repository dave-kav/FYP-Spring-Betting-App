package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.User;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int numRow) throws SQLException {
		User u = new User();
		
		u.setUsername(rs.getString("username"));
		u.setPassword(rs.getString("Password"));
		u.setFirstName(rs.getString("First_name"));
		u.setLastName(rs.getString("Last_name"));
		u.setDOB(rs.getDate("DOB"));
		u.setCredit(rs.getFloat("Credit"));
		
		return u;
	}

}
