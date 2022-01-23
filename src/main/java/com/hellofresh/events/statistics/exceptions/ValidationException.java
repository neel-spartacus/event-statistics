package com.hellofresh.events.statistics.exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

}
