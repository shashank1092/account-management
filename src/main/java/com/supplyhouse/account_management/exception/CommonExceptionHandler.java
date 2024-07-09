package com.supplyhouse.account_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CommonExceptionHandler {

    private static final String ERROR_MESSAGE = "errorMessage";
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFoundException(AccountNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({OperationNotAllowedException.class,})
    public ResponseEntity<Map<String, String>> handleOperationNotAllowedException(OperationNotAllowedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadCredentialsException.class,})
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({DuplicateAccountException.class,})
    public ResponseEntity<Map<String, String>> handleDuplicateAccountException(DuplicateAccountException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({InvitationExpiredException.class,})
    public ResponseEntity<Map<String, String>> handleInvitationExpiredException(InvitationExpiredException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
