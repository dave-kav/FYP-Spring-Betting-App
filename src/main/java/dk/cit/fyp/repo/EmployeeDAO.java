package dk.cit.fyp.repo;

import java.util.List;

import dk.cit.fyp.domain.Employee;

public interface EmployeeDAO {
	
	Employee getEmployee(String username);
	
	void save(Employee employee);
	
	List<Employee> findAll();
	
}
