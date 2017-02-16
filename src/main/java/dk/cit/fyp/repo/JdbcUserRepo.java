package dk.cit.fyp.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dk.cit.fyp.domain.User;
import dk.cit.fyp.mapper.UserRowMapper;

@Repository
public class JdbcUserRepo implements UserDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JdbcUserRepo(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public User getEmployee(String username) {
		String sql = "SELECT * FROM users WHERE username = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {username}, new UserRowMapper());
	}	

	@Override
	public void save(User user) {
		if (user.getEmployeeID() != 0)
			updateEmployee(user);
		else
			addEmployee(user);
	}
	
	private void addEmployee(User user) {
		String sql = "INSERT INTO users (Username, Password, Admin) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] {user.getUsername(), 
				user.getPassword(), user.isAdmin()});
	}
	
	private void updateEmployee(User user) {
		String sql = "UPDATE users SET Username = ?, Password = ?, Admin = ? WHERE Employee_id = ?";
		jdbcTemplate.update(sql, new Object[] {user.getUsername(), 
				user.getPassword(), user.isAdmin(), user.getEmployeeID()});
	}

	@Override
	public List<User> findAll() {
		String sql = "SELECT * FROM users";		
		return jdbcTemplate.query(sql, new UserRowMapper());
	}

}
