package com.orders.infrastructure.adapter.in.web;

import com.orders.domain.port.in.CreateOrderUseCase;
import com.orders.domain.port.in.GetOrderUseCase;
import com.orders.domain.port.in.GetOrdersByCustomerUseCase;
import com.orders.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrdersByCustomerUseCase getOrdersByCustomerUseCase;
    private final Validator validator;

    private static final Pattern DOCUMENT_PATTERN = Pattern.compile("^\\d{5,12}$");

    public Mono<ServerResponse> createOrder(ServerRequest request) {
        return request.bodyToMono(CreateOrderRequest.class)
                .flatMap(body -> {
                    Errors errors = new BeanPropertyBindingResult(body, "createOrderRequest");
                    validator.validate(body, errors);
                    if (errors.hasErrors()) {
                        return Mono.error(new RequestValidationException(errors));
                    }
                    return createOrderUseCase.createOrder(OrderWebMapper.toDomain(body))
                            .map(OrderWebMapper::toResponse)
                            .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(response));
                });
    }

    public Mono<ServerResponse> getOrderById(ServerRequest request) {
        String id = request.pathVariable("id");
        return getOrderUseCase.getOrderById(id)
                .map(OrderWebMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .switchIfEmpty(Mono.error(new com.orders.domain.exception.OrderNotFoundException("Order with ID " + id + " not found")));
    }

    public Mono<ServerResponse> getOrderByCustomerDocument(ServerRequest request) {
        String documentNumber = request.pathVariable("documentNumber");
        if (!DOCUMENT_PATTERN.matcher(documentNumber).matches()) {
            return Mono.error(new IllegalArgumentException("Customer document number must be numeric and between 5 and 12 digits"));
        }
        return getOrdersByCustomerUseCase.getOrdersByCustomerDocument(documentNumber)
                .map(OrderWebMapper::toResponse)
                .collectList()
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}