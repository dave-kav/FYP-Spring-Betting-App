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
	public List<User> get(String username) {
		String sql = "SELECT * FROM users u INNER JOIN authorities a ON a.username = u.username WHERE u.username = ?";
		return jdbcTemplate.query(sql, new Object[] {username}, new UserRowMapper());
	}	

	@Override
	public void save(User user) {
		List<User> users = get(user.getUsername());
		if (users.size() > 0)
			updateEmployee(user);
		else
			addEmployee(user);
	}
	
	private void addEmployee(User user) {
		String role = user.isAdmin() ? "ADMIN" : "USER";
		
		String sql = "INSERT INTO users (Username, Password) VALUES (?, ?)";
		jdbcTemplate.update(sql, new Object[] {user.getUsername(), 
				user.getPassword()});
		
		sql = "INSERT INTO authorities (username, authority) VALUES (?, ?)";
		jdbcTemplate.update(sql, new Object[] {user.getUsername(), role});
		
	}
	
	private void updateEmployee(User user) {
		String role = user.isAdmin() ? "ADMIN" : "USER"; 	
		
		String sql = "UPDATE users SET Password = ? WHERE Username = ?";
		jdbcTemplate.update(sql, new Object[] {user.getPassword(), user.getUsername()});
		
		sql = "UPDATE authorities SET authority = ? WHERE username = ?";
		jdbcTemplate.update(sql, new Object[] {role, user.getUsername()});
	}

	@Override
	public List<User> findAll() {
		String sql = "SELECT * FROM users u INNER JOIN authorities a ON a.username = u.username";		
		return jdbcTemplate.query(sql, new UserRowMapper());
	}

	@Override
	public void delete(String username) {
		String sql = "DELETE u, a FROM users u INNER JOIN authorities a ON u.username = a.username WHERE a.username=?";
		jdbcTemplate.update(sql, new Object[] {username});
	}
}
