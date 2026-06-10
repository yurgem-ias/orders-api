package com.orders.infrastructure.adapter.in.web;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.orders.domain.port.in.CreateOrderUseCase;
import com.orders.domain.port.in.GetOrderUseCase;
import com.orders.domain.port.in.GetOrdersByCustomerUseCase;
import com.orders.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.orders.infrastructure.adapter.in.web.dto.OrderResponse;
import com.orders.infrastructure.adapter.in.web.dto.ValidationErrorResponse;
import com.orders.infrastructure.adapter.in.web.dto.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrdersByCustomerUseCase getOrdersByCustomerUseCase;
    private final Validator validator;

    private static final Pattern DOCUMENT_PATTERN = Pattern.compile("^\\d{5,12}$");

    @Operation(
        summary = "Create new order",
        description = "Initiates the process of creating a controlled and validated order",
        requestBody = @RequestBody(
            content = @Content(schema = @Schema(implementation = CreateOrderRequest.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Purchase order generated",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
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

    @Operation(
        summary = "View a purchase order by ID",
        description = "Search and return the details of a purchase order based on an ID",
        parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, description = "Order ID", required = true, schema = @Schema(type = "string"))
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Purchase order found",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Purchase order not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public Mono<ServerResponse> getOrderById(ServerRequest request) {
        String id = request.pathVariable("id");
        return getOrderUseCase.getOrderById(id)
                .map(OrderWebMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .switchIfEmpty(Mono.error(new com.orders.domain.exception.OrderNotFoundException("Order with ID " + id + " not found")));
    }

    @Operation(
        summary = "Check orders by document number",
        description = "Searches and returns the list of orders associated with a document number.",
        parameters = {
            @Parameter(name = "documentNumber", in = ParameterIn.PATH, description = "Customer document number (5 to 12 digits)", required = true, schema = @Schema(type = "string"))
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Purchase orders found",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid document format",
                content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Customer has no orders",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
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