package com.example.demo.empcalc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.empservice.impl.EmployeeRequestServiceImpl;
import com.example.demo.model.EmployeeRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/employee/v1")
@RequiredArgsConstructor
public class EmployeeController {
	
	private final EmployeeRequestServiceImpl employeeRequestServiceImpl;
	
	@PostMapping("/employeeRequests")
	List<String> getEmployeeRequests(@RequestBody List<EmployeeRequest> e) {
		log.info("Fetching employee requests from controller e: {}", e);
		return employeeRequestServiceImpl.fetchEmployeeRequests(e);
	}
	
}
