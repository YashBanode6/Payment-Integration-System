package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.constant.Constants;
import com.example.demo.pojo.CreatePayment;
import com.example.demo.pojo.PaymentResponse;
import com.example.demo.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping(Constants.PAYMENTS)
@Slf4j
@RequiredArgsConstructor
public class PaymentsController {
	
	
	private final PaymentService paymentService;

	@PostMapping
	public PaymentResponse createPayment(@RequestBody CreatePayment createPayment) {
		
		log.info("Create a new Payment || createPayment :{}", createPayment);
		
		PaymentResponse response = paymentService.createPayment(createPayment);
		
		log.info("Creating payment controller || response :{}");
		
		return response;
	}
}
