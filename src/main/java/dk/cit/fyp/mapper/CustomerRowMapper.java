package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Customer;

/**
 * Convert jdbc result set into Customer objects.
 * 
 * @author Dave Kavanagh
 *
 */
public class CustomerRowMapper implements RowMapper<Customer> {

	@Override
	public Customer mapRow(ResultSet rs, int numRow) throws SQLException {
		Customer u = new Customer();
		
		u.setUsername(rs.getString("username"));
		u.setPassword(rs.getString("Password"));
		u.setFirstName(rs.getString("First_name"));
		u.setLastName(rs.getString("Last_name"));
		u.setDOB(rs.getDate("DOB"));
		u.setCredit(rs.getFloat("Credit"));
		
		return u;
	}
}
