package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StripeProviderException extends RuntimeException {

	private static final long serialVersionUID = -5995541864700527794L;
	
	private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    public StripeProviderException(String errorCode, String errorMessage, HttpStatus httpStatus) {
        super(errorMessage); // keep message in RuntimeException as well
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

}
