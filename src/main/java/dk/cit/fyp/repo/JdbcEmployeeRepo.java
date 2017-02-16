package dk.cit.fyp.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dk.cit.fyp.domain.Employee;
import dk.cit.fyp.mapper.EmployeeRowMapper;

@Repository
public class JdbcEmployeeRepo implements EmployeeDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JdbcEmployeeRepo(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Employee getEmployee(String username) {
		String sql = "SELECT * FROM users WHERE username = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {username}, new EmployeeRowMapper());
	}	

	@Override
	public void save(Employee employee) {
		if (employee.getEmployeeID() != 0)
			updateEmployee(employee);
		else
			addEmployee(employee);
	}
	
	private void addEmployee(Employee employee) {
		String sql = "INSERT INTO users (Username, Password, Admin) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] {employee.getUsername(), 
				employee.getPassword(), employee.isAdmin()});
	}
	
	private void updateEmployee(Employee employee) {
		String sql = "UPDATE users SET Username = ?, Password = ?, Admin = ? WHERE Employee_id = ?";
		jdbcTemplate.update(sql, new Object[] {employee.getUsername(), 
				employee.getPassword(), employee.isAdmin(), employee.getEmployeeID()});
	}

	@Override
	public List<Employee> findAll() {
		String sql = "SELECT * FROM users";		
		return jdbcTemplate.query(sql, new EmployeeRowMapper());
	}

}
