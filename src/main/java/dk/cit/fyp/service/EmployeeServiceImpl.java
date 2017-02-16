package dk.cit.fyp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.cit.fyp.domain.Employee;
import dk.cit.fyp.repo.EmployeeDAO;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	EmployeeDAO employeeRepo;
	
	@Autowired
	public EmployeeServiceImpl(EmployeeDAO employeeRepo) {
		this.employeeRepo = employeeRepo;
	}

	@Override
	public Employee get(String username) {
		return employeeRepo.getEmployee(username);
	}

	@Override
	public void save(Employee employee) {
		employeeRepo.save(employee);
	}

	@Override
	public List<Employee> findAll() {
		return employeeRepo.findAll();
	}

}
