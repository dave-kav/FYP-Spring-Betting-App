package dk.cit.fyp.repo;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.mapper.CustomerRowMapper;

@Repository
public class JdbcCustomerRepo implements CustomerDAO {
	
	private JdbcTemplate jdbcTemplate;
	private final static Logger logger = Logger.getLogger(JdbcCustomerRepo.class);
	@Autowired
	public JdbcCustomerRepo(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Customer> get(String username) {
		String sql = "SELECT * FROM Customers WHERE username = ?";
		return jdbcTemplate.query(sql, new Object[] {username}, new CustomerRowMapper());
	}

	@Override
	public void save(Customer customer) {
		logger.info(get(customer.getUsername()).size());
		if ( get(customer.getUsername()).size() != 0 )
			update(customer);
		else
			add(customer);
	}
	
	private void add(Customer customer) {
		logger.info("in add");
		String sql = "INSERT INTO Customers (Username, Password, First_name, Last_name, DOB, Credit) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		jdbcTemplate.update(sql, new Object[] {customer.getUsername(), customer.getPassword(), customer.getFirstName(), 
				customer.getLastName(), customer.getDOB(), customer.getCredit()});
	}
	
	private void update(Customer customer) {
		String sql = "UPDATE Customers SET Password = ?, First_name = ?, Last_name = ?, DOB = ?, Credit = ?"
				+ "WHERE username = ?";
		
		jdbcTemplate.update(sql, new Object[] {customer.getPassword(), customer.getFirstName(), 
				customer.getLastName(), customer.getDOB(), customer.getCredit(), customer.getUsername()});
	}

	@Override
	public List<Customer> findAll() {
		String sql = "SELECT * FROM Customers";
		return jdbcTemplate.query(sql, new CustomerRowMapper());
	}
}
