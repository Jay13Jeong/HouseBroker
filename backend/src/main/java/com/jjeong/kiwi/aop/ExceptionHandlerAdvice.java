package com.jjeong.kiwi.aop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Void> handleRuntimeException(ResponseStatusException e) {
        return new ResponseEntity<>(null, e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException() {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}