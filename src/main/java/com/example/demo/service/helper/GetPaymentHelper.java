package com.example.demo.service.helper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.constant.Constants;
import com.example.demo.constant.ErrorCodeEnum;
import com.example.demo.exception.StripeProviderException;
import com.example.demo.http.HttpRequest;
import com.example.demo.pojo.PaymentResponse;
import com.example.demo.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetPaymentHelper {
	@Value("${stripe.api.key}")
	public String stripeApiKey;
	
	@Value("${stripe.get-session.url}")
    public String getPaymentSessionTemplate;
	
	private final JsonUtil jsonUtil;
	
	private final ChatClient chatClient;

	
	public HttpRequest prepareHttpRequest(String paymentId) {
		HttpHeaders headers = new HttpHeaders();
		
		headers.setBasicAuth(stripeApiKey, Constants.EMPTYSTRING);
		
		
		String getPaymentSessionUrl = getPaymentSessionTemplate.replace("{id}", paymentId);
		log.info("Constructed getPaymentSessionUrl: {}", getPaymentSessionUrl);
		log.info("Using getPaymentSessionTemplate: {}", getPaymentSessionTemplate);
	        
	     HttpRequest httpRequest = new HttpRequest();
	     httpRequest.setHttpMethod(HttpMethod.GET);
		 httpRequest.setUrl(getPaymentSessionUrl);
	     httpRequest.setHttpHeaders(headers);
	     httpRequest.setRequestBody("");
		return httpRequest;
	}
	
public PaymentResponse processResponse(ResponseEntity<String> httpServiceResponse) {
		
		
		if (httpServiceResponse.getStatusCode().is2xxSuccessful()) {
			
			log.info("Processing successful HTTP response");
			
			PaymentResponse paymentResponse = jsonUtil.convertJsonToObject
					(httpServiceResponse.getBody(), PaymentResponse.class);
			log.info("Converted PaymentResponse: {}", paymentResponse);
					if (paymentResponse != null && paymentResponse.getId() != null) {
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
				ErrorCodeEnum.GET_PAYMENT_FAILED.getErrorCode(),
				ErrorCodeEnum.GET_PAYMENT_FAILED.getErrorMessage(),
				HttpStatus.SERVICE_UNAVAILABLE);
	}

private String prepareErrorSummaryMessage(ResponseEntity<String> httpServiceResponse) {
	
	if(true) {
		return httpServiceResponse.getBody();
	}
	
	
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


}
