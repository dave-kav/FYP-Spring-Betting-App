package dk.cit.fyp.repo;

import java.util.List;

import dk.cit.fyp.domain.User;

public interface UserDAO {
	
	User getEmployee(String username);
	
	void save(User user);
	
	List<User> findAll();
	
}
