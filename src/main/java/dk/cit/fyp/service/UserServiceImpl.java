package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Customer;
import dk.cit.fyp.repo.CustomerDAO;

@Service
public class UserServiceImpl implements UserService {
	
	private CustomerDAO userRepo;
	
	@Autowired
	public UserServiceImpl(CustomerDAO userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public Customer get(String username) {
		return userRepo.get(username);
	}

	@Override
	public void save(Customer customer) {
		userRepo.save(customer);

	}

	@Override
	public List<Customer> findAll() {
		return userRepo.findAll();
	}

}
