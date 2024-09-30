package com.mabotalb.book_network_api.handler;

import com.mabotalb.book_network_api.exception.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.mabotalb.book_network_api.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException ex) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException ex) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException ex) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error(BAD_CREDENTIALS.getDescription())
                                .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException ex) {
        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            var errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidTokenException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ExceptionResponse> handleException(ExpiredTokenException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(NotEqualPasswordsException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotEqualPasswordsException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(CONFLICT)
                .body(
                        ExceptionResponse.builder()
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        // Log the exception
        ex.printStackTrace();
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal Server Error, contact the admin")
                                .error(ex.getMessage())
                                .build()
                );
    }
}
