package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.User;
import dk.cit.fyp.repo.UserDAO;

@Service
public class UserServiceImpl implements UserService {
	
	UserDAO userRepo;
	
	@Autowired
	public UserServiceImpl(UserDAO employeeRepo) {
		this.userRepo = employeeRepo;
	}

	@Override
	public List<User> get(String username) {
		return userRepo.get(username);
	}

	@Override
	public void save(User user) {
		userRepo.save(user);
	}

	@Override
	public List<User> findAll() {
		return userRepo.findAll();
	}

	@Override
	public void delete(String username) {
		userRepo.delete(username);
	}
}
