package com.orders.infrastructure.adapter.in.web;

import com.orders.domain.exception.OrderNotFoundException;
import com.orders.infrastructure.adapter.in.web.dto.ErrorResponse;
import com.orders.infrastructure.adapter.in.web.dto.ValidationErrorResponse;
import com.orders.infrastructure.adapter.in.web.dto.ValidationErrorResponse.FieldErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                List<FieldErrorDto> validationErrors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                                .collect(Collectors.toList());

                ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message("Validation failed for the request payload")
                                .path(request.getRequestURI())
                                .validationErrors(validationErrors)
                                .build();

                log.warn("Validation error on path {}: {}", request.getRequestURI(), validationErrors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(OrderNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
                        OrderNotFoundException ex, HttpServletRequest request) {

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                log.info("Order not found exception on path {}: {}", request.getRequestURI(), ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                log.warn("Illegal argument on path {}: {}", request.getRequestURI(), ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
        public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(
                        jakarta.validation.ConstraintViolationException ex,
                        HttpServletRequest request) {

                List<FieldErrorDto> validationErrors = ex.getConstraintViolations()
                                .stream()
                                .map(violation -> {
                                        String propertyPath = violation.getPropertyPath().toString();
                                        String parameterName = propertyPath.contains(".")
                                                        ? propertyPath.substring(propertyPath.lastIndexOf('.')+1)
                                                        : propertyPath;
                                        return new FieldErrorDto(parameterName, violation.getMessage());
                                })
                                .collect(Collectors.toList());

                ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message("Validation failed for the request paylod/parameters")
                                .path(request.getRequestURI())
                                .validationErrors(validationErrors)
                                .build();

                log.warn("Constraint validaion error on path {}: {}", request.getRequestURI(), validationErrors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

                @SuppressWarnings("removal")
                @ExceptionHandler(org.springframework.web.method.annotation.HandlerMethodValidationException.class)
        public ResponseEntity<ValidationErrorResponse> handleMethodValidationException(
                        org.springframework.web.method.annotation.HandlerMethodValidationException ex,
                        HttpServletRequest request) {

                List<FieldErrorDto> validationErrors = ex.getAllValidationResults()
                                .stream()
                                .map(result -> {
                                        String parameterName = result.getMethodParameter().getParameterName();
                                        String message = result.getResolvableErrors().stream()
                                                        .map(org.springframework.context.MessageSourceResolvable::getDefaultMessage)
                                                        .collect(Collectors.joining(", "));
                                        return new FieldErrorDto(parameterName, message);
                                })
                                .collect(Collectors.toList());

                ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message("Validation failed for the request parameters")
                                .path(request.getRequestURI())
                                .validationErrors(validationErrors)
                                .build();

                log.warn("Parameter validaion error on path {}: {}", request.getRequestURI(), validationErrors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleAllExceptions(
                        Exception ex, HttpServletRequest request) {

                log.error("Internal Server Error occurred on path {}", request.getRequestURI(), ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .message("An unexpected internal error occurred. Please contact support.")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
}
