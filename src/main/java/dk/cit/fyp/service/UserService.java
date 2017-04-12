package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.User;

public interface UserService {
	
	List<User> get(String username);
	
	void save(User user);
	
	List<User> findAll();
	
	void delete(String username);

}
