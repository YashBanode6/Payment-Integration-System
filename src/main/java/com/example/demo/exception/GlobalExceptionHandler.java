package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.constant.ErrorCodeEnum;
import com.example.demo.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(StripeProviderException.class)
    public ResponseEntity<ErrorResponse> handleStripeProviderException(StripeProviderException ex) {
    	log.error("Handling StripeProviderException: {}", ex.toString());
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getErrorMessage()
        );
        log.error("ErrorResponse: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    	log.error("Handling StripeProviderException: {}", ex.toString());
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
                ErrorCodeEnum.GENERIC_ERROR.getErrorMessage()
        );
        log.error("ErrorResponse: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

