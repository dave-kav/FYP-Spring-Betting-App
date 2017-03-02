package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.Customer;

public interface CustomerService {
	
List<Customer> get(String username);
	
	void save(Customer customer);
	
	List<Customer> findAll();

}
