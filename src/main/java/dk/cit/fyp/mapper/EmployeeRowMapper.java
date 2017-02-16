package dk.cit.fyp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.cit.fyp.domain.Employee;

public class EmployeeRowMapper implements RowMapper<Employee> {

	@Override
	public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
		Employee e = new Employee();
		
		e.setUsername(rs.getString("Username"));
		e.setPassword(rs.getString("Password"));
		e.setAdmin(rs.getBoolean("Admin"));
		
		return e;
	}

}
