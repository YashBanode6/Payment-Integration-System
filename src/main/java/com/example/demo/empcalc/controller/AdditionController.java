package com.example.demo.empcalc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.calcservice.impl.CalculationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/addition")
@Slf4j

public class AdditionController {

	@Autowired
	private CalculationService calculationService;
	
	@PostMapping("/add")
	public int add(@RequestParam int a,@RequestParam int b) {
	
		int result = calculationService.add(a, b);
		log.info("Addition result A1: {}", result);
		return result;
		
	}
}
