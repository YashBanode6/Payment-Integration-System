package com.example.demo.service;

import com.example.demo.pojo.CreatePayment;
import com.example.demo.pojo.PaymentResponse;

public interface PaymentService {
	PaymentResponse createPayment(CreatePayment createPayment);
}
