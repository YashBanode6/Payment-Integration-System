package com.example.demo.empservice.impl;

import java.util.List;

import com.example.demo.model.EmployeeRequest;

public interface EmployeeRequestService {

	List<String> fetchEmployeeRequests(List<EmployeeRequest> e);
}
