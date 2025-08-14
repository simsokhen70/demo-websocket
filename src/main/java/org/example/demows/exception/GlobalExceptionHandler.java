package org.example.demows.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ApiResponse;
import org.example.demows.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for centralized error handling
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String traceId = generateTraceId();
        log.error("Resource not found [TraceId: {}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .suggestion("Please check the resource ID and try again")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Resource not found", errorResponse));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        String traceId = generateTraceId();
        log.error("Resource already exists [TraceId: {}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .suggestion("Please use a different identifier or update the existing resource")
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Resource already exists", errorResponse));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadCredentialsException(BadCredentialsException ex) {
        String traceId = generateTraceId();
        log.error("Bad credentials [TraceId: {}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Invalid username or password")
                .suggestion("Please check your credentials and try again")
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication failed", errorResponse));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        String traceId = generateTraceId();
        log.error("Username not found [TraceId: {}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("User not found")
                .suggestion("Please check the username or register a new account")
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found", errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String traceId = generateTraceId();
        log.error("Validation error [TraceId: {}]: {}", traceId, ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation failed")
                .details(errors)
                .suggestion("Please check the field values and try again")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGenericException(Exception ex) {
        String traceId = generateTraceId();
        log.error("Unexpected error [TraceId: {}]: {}", traceId, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .suggestion("Please try again later or contact support with trace ID: " + traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", errorResponse));
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
