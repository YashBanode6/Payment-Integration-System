package com.example.demo.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.constant.ErrorCodeEnum;
import com.example.demo.exception.StripeProviderException;
import com.example.demo.http.HttpRequest;
import com.example.demo.http.HttpServiceEngine;
import com.example.demo.pojo.CreatePayment;
import com.example.demo.pojo.PaymentResponse;
import com.example.demo.service.PaymentService;
import com.example.demo.service.helper.CreatePaymentHelper;
import com.example.demo.service.helper.GetPaymentHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final HttpServiceEngine httpServiceEngine;
	
	private final ObjectMapper objectMapper;
	
	private final CreatePaymentHelper createPaymentHelper;
	
	private final GetPaymentHelper getPaymentHelper;
	
	@Override
	public PaymentResponse createPayment(CreatePayment createPayment) {
		log.info("Creating payment Imp| createPayment:{}", createPayment);
		
		// If createpaymentRequest 1st list Item quantity is 0 or empty then throw exception
		if (createPayment.getListItems().get(0).getQuantity() <= 0) {
			  throw new StripeProviderException(
	                    ErrorCodeEnum.INVALID_QUANTITY.getErrorCode(),
	                    ErrorCodeEnum.INVALID_QUANTITY.getErrorMessage(),
	                    HttpStatus.SERVICE_UNAVAILABLE
	            );
		}

		
		HttpRequest httpRequest = createPaymentHelper.prepareHttpRequest(createPayment);
		
		ResponseEntity<String> httpServiceResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP Service Response: {}", httpServiceResponse);
		
		PaymentResponse paymentResponse = createPaymentHelper.processResponse(httpServiceResponse);
		
		log.info("Final payment Imp| paymentResponse:{}", paymentResponse);
		
		return paymentResponse;
	}

	@Override
	public String getPaymentStatus(String paymentId) {
		log.info("Getting payment status| paymentId:{}", paymentId);
		HttpRequest httpRequest = getPaymentHelper.prepareHttpRequest(paymentId);
		
		ResponseEntity<String> httpServiceResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP Service Response: {}", httpServiceResponse);
		
//		PaymentResponse paymentResponse = createPaymentHelper.processResponse(httpServiceResponse);
//		
//		log.info("Final payment Imp| paymentResponse:{}", paymentResponse);
		
		return " From Service";
	}

	
	
	
}
