package com.example.demo.calcservice.impl;

import org.springframework.stereotype.Service;

@Service
public class CalculationImpl implements CalculationService {

	@Override
	public int add(int num1, int num2) {
		int retult = num1 + num2;
		
		return retult;
	}

}
