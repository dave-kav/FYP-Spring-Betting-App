package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.User;
import dk.cit.fyp.repo.UserDAO;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	UserDAO employeeRepo;
	
	@Autowired
	public EmployeeServiceImpl(UserDAO employeeRepo) {
		this.employeeRepo = employeeRepo;
	}

	@Override
	public User get(String username) {
		return employeeRepo.getEmployee(username);
	}

	@Override
	public void save(User user) {
		employeeRepo.save(user);
	}

	@Override
	public List<User> findAll() {
		return employeeRepo.findAll();
	}

}
