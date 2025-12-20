package com.example.demo.service.impl;

import org.springframework.ai.chat.client.ChatClient;
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
import com.example.demo.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final HttpServiceEngine httpServiceEngine;
	
	private final ObjectMapper objectMapper;
	
	private final JsonUtil jsonUtil;
	
	private final CreatePaymentHelper createPaymentHelper;
	
	private final ChatClient  chatClient;
	
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
		
		PaymentResponse paymentResponse = processResponse(httpServiceResponse);
		
//		PaymentResponse paymentResponse = jsonUtil.convertJsonToObject
//				(httpServiceResponse.getBody(), PaymentResponse.class);

		
		log.info("Final payment Imp| paymentResponse:{}", paymentResponse);
		
		return paymentResponse;
	}

	private PaymentResponse processResponse(ResponseEntity<String> httpServiceResponse) {
		
		
		if (httpServiceResponse.getStatusCode().is2xxSuccessful()) {
			
			log.info("Processing successful HTTP response");
			
			PaymentResponse paymentResponse = jsonUtil.convertJsonToObject
					(httpServiceResponse.getBody(), PaymentResponse.class);
			log.info("Converted PaymentResponse: {}", paymentResponse);
					if (paymentResponse != null
							&& paymentResponse.getId() != null
							&& paymentResponse.getUrl() != null) {
						log.info("PaymentResponse is valid and contains necessary fields: {}", paymentResponse);
						return paymentResponse;
					}
		} 
		
		// if error code is 4xx or 5xx need to throw payment creation failed exception
		// but here need to check what to pass in error code and error message
		
		String errorMessage = prepareErrorSummaryMessage(httpServiceResponse);
		log.info("Prepared error summary message: {}", errorMessage);
		
		if (httpServiceResponse.getStatusCode().is4xxClientError()
				|| httpServiceResponse.getStatusCode().is5xxServerError()) {
			log.error("Received error HTTP response: {}", httpServiceResponse);
			throw new StripeProviderException(
					ErrorCodeEnum.STRIPE_ERROR.getErrorCode(),
					errorMessage,
					HttpStatus.valueOf(httpServiceResponse.getStatusCode().value()));
		}
		
		// if we reach here, something went wrong we need to throw exception
		throw new StripeProviderException(
				ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorCode(),
				ErrorCodeEnum.PAYMENT_CREATION_FAILED.getErrorMessage(),
				HttpStatus.SERVICE_UNAVAILABLE);
	}

	private String prepareErrorSummaryMessage(ResponseEntity<String> httpServiceResponse) {
		String promptTemplate = """
				Given the following json message from a third-party API, read the entire JSON, and summarize in 1 line:
				Instructions:
				1. Put a short, simple summary. Which exactly represents what error happened.
				2. Max length of summary less than 200 characters.
				3. Keep the output clear and concise.
				4. Summarize as message that we can send in API response to the client.
				5. Dont point any info to read external documentation or link.
				{error_json}
				""";
		
		String errorJson = httpServiceResponse.getBody();
		
		String response = chatClient.prompt()
				.system("You are an technical analyst. which just retunrs 1 line summary of the json error")
				.user(promptUserSpec -> promptUserSpec
						.text(promptTemplate)
						.param("error_json", errorJson))
				.call()
				.content();
		
		log.info("AI Generated error summary message: {}", response);
		
		return response;
	}

	@PostConstruct
	public String onInit() {
		String promptTemplate = """
				Given the following json message from a third-party API, read the entire JSON, and summarize in 1 line:
				Instructions:
				1. Put a short, simple summary. Which exactly represents what error happened.
				2. Max length of summary less than 200 characters.
				3. Keep the output clear and concise.
				4. Summarize as message that we can send in API response to the client.
				5. Dont point any info to read external documentation or link.
				{error_json}
				""";
		
		String errorJson = "{\n" +
				"  \"error\": {\n" +
				"    \"code\": \"parameter_missing\",\n" +
				"    \"doc_url\": \"https://stripe.com/docs/error-codes/parameter-missing\",\n" +
				"    \"message\": \"Missing required param: line_items[0][price_data][currency].\",\n" +
				"    \"param\": \"line_items[0][price_data][currency]\",\n" +
				"    \"request_log_url\": \"https://dashboard.stripe.com/test/logs/req_VSdomoD7qM6H1H?t=1756279898\",\n" +
				"    \"type\": \"invalid_request_error\"\n" +
				"  }\n" +
				"}";
		
		String response = chatClient.prompt()
				.system("You are an technical analyst. which just retunrs 1 line summary of the json error")
				.user(promptUserSpec -> promptUserSpec
						.text(promptTemplate)
						.param("error_json", errorJson))
				.call()
				.content();
		return "Initialized";
	}
	
}
