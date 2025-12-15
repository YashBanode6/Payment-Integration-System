package com.example.demo.empservice.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.EmployeeRequest;


@Service
public class EmployeeRequestServiceImpl implements EmployeeRequestService {

	@Override
	public List<String> fetchEmployeeRequests(List<EmployeeRequest> e) {
		
		return e.stream().map(emp -> "Name" + emp.getName() + 
				", Id" + emp.getId() +
				", Salary" + emp.getSalary()).toList();
	}

}
