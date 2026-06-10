package com.orders.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.domain.exception.OrderNotFoundException;
import com.orders.infrastructure.adapter.in.web.dto.ErrorResponse;
import com.orders.infrastructure.adapter.in.web.dto.ValidationErrorResponse;
import com.orders.infrastructure.adapter.in.web.dto.ValidationErrorResponse.FieldErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Object body;

        if (ex instanceof OrderNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            body = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value())
                    .build();
            log.info("Order not found exception on path {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            body = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value())
                    .build();
            log.warn("Illegal argument on path {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());
        } else if (ex instanceof RequestValidationException validationEx) {
            status = HttpStatus.BAD_REQUEST;

            List<FieldErrorDto> validationErrors = validationEx.getErrors().getFieldErrors()
                    .stream()
                    .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            body = ValidationErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message("Validation failed for the request payload")
                    .path(exchange.getRequest().getPath().value())
                    .validationErrors(validationErrors)
                    .build();
            log.warn("Validation error on path {}: {}", exchange.getRequest().getPath().value(), validationErrors);
        } else if (ex instanceof ResponseStatusException statusException) {
            status = HttpStatus.valueOf(statusException.getStatusCode().value());
            body = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(statusException.getReason())
                    .path(exchange.getRequest().getPath().value())
                    .build();
        } else {
            log.error("Internal Server Error occurred on path {}", exchange.getRequest().getPath().value(), ex);
            body = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message("An unexpected internal error occurred. Please contact support.")
                    .path(exchange.getRequest().getPath().value())
                    .build();
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error writing exception response", e);
            return Mono.error(e);
        }
    }
}