package com.sb1.exceptions;

public class RetryableException extends RuntimeException {

    public RetryableException(String message) {
        super(message);
    }
}
