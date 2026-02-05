package com.example.todos.simpletodoservice.exception;

import com.example.todos.simpletodoservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request){
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(PastDueModificationException.class)
    public ResponseEntity<ErrorResponse> handlePastDueModification(PastDueModificationException ex, HttpServletRequest request){
        return buildErrorResponse(HttpStatus.CONFLICT,
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request){
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request);
    }

    /* @Valid body validation errors */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){

        String message = ex.getBindingResult().
                getFieldErrors().
                stream().
                map(err -> err.getField() + ": " + err.getDefaultMessage()).
                collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                message,
                request);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String message = "Invalid value for parameter '" + ex.getName() + "': " + ex.getValue();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        logger.warn("Invalid request body. uri={}", request.getRequestURI());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request format",
                request
        );
    }

    /* fallback */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error while processing request. method={}, uri={}",
                request.getMethod(),
                request.getRequestURI(),
                ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request){
        ErrorResponse body = new ErrorResponse(Instant.now(),
                status.value(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status.value()).body(body);
    }
}
