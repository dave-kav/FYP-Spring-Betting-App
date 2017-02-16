package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.User;

public interface EmployeeService {
	
	User get(String username);
	
	void save(User user);
	
	List<User> findAll();

}
