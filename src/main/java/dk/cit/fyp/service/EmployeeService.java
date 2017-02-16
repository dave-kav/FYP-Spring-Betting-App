package dk.cit.fyp.service;

import java.util.List;

import dk.cit.fyp.domain.Employee;

public interface EmployeeService {
	
	Employee get(String username);
	
	void save(Employee employee);
	
	List<Employee> findAll();

}
