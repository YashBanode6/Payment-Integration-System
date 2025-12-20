package com.example.demo.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.example.demo.constant.ErrorCodeEnum;
import com.example.demo.exception.StripeProviderException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpServiceEngine {

	private RestClient restClient;
	
	public HttpServiceEngine(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}
	
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {	
		log.info("Making HTTP call from httpServiceEngine");

		
		try {
		// Method Chaining for making HTTP Call
		ResponseEntity<String> httpResponse = restClient.method(httpRequest.getHttpMethod())
		      .uri(httpRequest.getUrl())
		      .headers(t -> t.addAll(httpRequest.getHttpHeaders()))
		      .body(httpRequest.getRequestBody())
		      .retrieve()
		      .toEntity(String.class);
		
		log.info("HTTP call httpResponse: {}", httpResponse);
		
		return httpResponse;
		}catch(HttpClientErrorException | HttpServerErrorException e) {
			 // valid error response from Stripe
			log.error("HTTP error during call: {}", e.getMessage(), e);
			
			// Gateway timeout 504 or Service Unavailable 503 need to be handled as exceptions
			if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT
					|| e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
				throw new StripeProviderException(ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
						ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
						HttpStatus.SERVICE_UNAVAILABLE);
			}
			
			// create ResponseEntity with statuc code and body from exception and return
			return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		}
		catch (Exception e) {// unable to connect
			log.error("Error during HTTP call: {}", e.getMessage());
			
			throw new StripeProviderException(
                    ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorCode(),
                    ErrorCodeEnum.UNABLE_TO_CONNECT_TO_STRIPE.getErrorMessage(),
                    HttpStatus.BAD_REQUEST
            );
		}
		
	}
	
	@PostConstruct
	public void init() {
		log.info("HttpServiceEngine initialized RestClient: {}", restClient);
	}
}
