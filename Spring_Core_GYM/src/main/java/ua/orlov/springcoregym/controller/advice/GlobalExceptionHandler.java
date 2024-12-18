package ua.orlov.springcoregym.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.exception.TooManyAttemptsException;

import java.util.*;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        logException("MissingServletRequestParameterException occurred", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        logException("AuthenticationException occurred", ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage());
    }

    @ExceptionHandler(TooManyAttemptsException.class)
    public ResponseEntity<?> handleTooManyAttempts(TooManyAttemptsException ex) {
        logException("TooManyAttemptsException occurred", ex);
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex) {
        logException("NoSuchElementException occurred", ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logException("HttpMessageNotReadableException occurred", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        logException("MethodArgumentTypeMismatchException occurred", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        logException("NoResourceFoundException occurred", ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        logException("AccessDeniedException occurred", ex);
        return buildErrorResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        logException("EntityNotFoundException occurred", ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logException("MethodArgumentNotValidException occurred", ex);
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        return buildValidationErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", errors);
    }

    @ExceptionHandler(BusinessLogicException.class)
    @ResponseBody
    public ResponseEntity<?> handleBusinessLogicException(BusinessLogicException ex) {
        logException("BusinessLogicException occurred", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "LOGIC_ERROR", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        logException("IllegalArgumentException occurred", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "An unexpected illegal argument was provided");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        logException("RuntimeException occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected runtime error occurred.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleException(Exception ex) {
        logException("Exception occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "GENERAL_ERROR", "An unexpected error occurred.");
    }

    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", error);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<?> buildValidationErrorResponse(HttpStatus status, String error, List<String> errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", error);
        response.put("errors", errors);
        return ResponseEntity.status(status).body(response);
    }

    private void logException(String message, Exception ex) {
        log.error(message, ex);
    }
}
