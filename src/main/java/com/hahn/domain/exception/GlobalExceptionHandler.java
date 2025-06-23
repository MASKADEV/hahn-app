package com.hahn.domain.exception;

import com.hahn.application.dto.ApiResponse;
import com.hahn.application.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ErrorDto errorDto = ErrorDto.of(ex.getStatus(), ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(errorDto),
                ex.getStatus()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorDto errorDto = ErrorDto.of(HttpStatus.BAD_REQUEST, "Validation error", errors);
        return new ResponseEntity<>(
                ApiResponse.error(errorDto),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        ErrorDto errorDto = ErrorDto.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(errorDto),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorDto errorDto = ErrorDto.of(HttpStatus.FORBIDDEN, "Access denied");
        return new ResponseEntity<>(
                ApiResponse.error(errorDto),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        ErrorDto errorDto = ErrorDto.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(
                ApiResponse.error(errorDto),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}