package dao;

import entity.Employee;

public interface EmployeeDAO {
	 Employee findByIdAndPassword(String empId, String empPw);
	 void updateLoginTime(String empId);
}
