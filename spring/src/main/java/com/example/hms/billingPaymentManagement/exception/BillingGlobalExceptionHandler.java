package com.example.hms.billingPaymentManagement.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BillingGlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException e) { return response(HttpStatus.NOT_FOUND, e.getMessage()); }
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ErrorResponse> conflict(ConflictException e) { return response(HttpStatus.CONFLICT, e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message);
    }
    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(),
                status.getReasonPhrase(), message));
    }
}
