package dk.cit.fyp.repo;

import java.util.List;

import dk.cit.fyp.domain.Customer;

public interface CustomerDAO {

	List<Customer> get(String username);

	void save(Customer customer);

	List<Customer> findAll();

}
