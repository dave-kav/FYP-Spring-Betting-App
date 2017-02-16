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
	public User get(String username) {
		String sql = "SELECT * FROM Customers WHERE username = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {username}, new UserRowMapper());
	}

	@Override
	public void save(User user) {
		if ( get(user.getUsername()) == null )
			update(user);
		else
			add(user);
	}
	
	private void add(User user) {
		String sql = "INSERT INTO Customers (Username, Password, First_name, Last_name, DOB, Credit) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		jdbcTemplate.update(sql, new Object[] {user.getUsername(), user.getPassword(), user.getFirstName(), 
				user.getLastName(), user.getDOB(), user.getCredit()});
	}
	
	private void update(User user) {
		String sql = "UPDATE Customers SET Password = ?, First_name = ?, Last_name = ?, DOB = ?, Credit = ?"
				+ "WHERE username = ?";
		
		jdbcTemplate.update(sql, new Object[] {user.getPassword(), user.getFirstName(), 
				user.getLastName(), user.getDOB(), user.getCredit(), user.getUsername()});
	}

	@Override
	public List<User> findAll() {
		String sql = "SELECT * FROM Customers";
		return jdbcTemplate.query(sql, new UserRowMapper());
	}

}
