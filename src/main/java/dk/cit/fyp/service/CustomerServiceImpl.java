package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.repo.CustomerDAO;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	private CustomerDAO customerRepo;
	
	@Autowired
	public CustomerServiceImpl(CustomerDAO userRepo) {
		this.customerRepo = userRepo;
	}

	@Override
	public List<Customer> get(String username) {
		return customerRepo.get(username);
	}

	@Override
	public void save(Customer customer) {
		customerRepo.save(customer);

	}

	@Override
	public List<Customer> findAll() {
		return customerRepo.findAll();
	}
}
