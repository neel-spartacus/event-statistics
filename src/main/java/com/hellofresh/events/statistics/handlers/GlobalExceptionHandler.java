package com.hellofresh.events.statistics.handlers;


import com.hellofresh.events.statistics.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.DateTimeException;

@EnableWebMvc
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ValidationException.class,
            DateTimeException.class})
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorMessage> handleServerExceptions(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleExceptions(HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorMessage(ex.getMessage()));
    }
}
