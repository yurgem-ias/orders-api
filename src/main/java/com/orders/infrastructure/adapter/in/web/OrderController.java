package com.orders.infrastructure.adapter.in.web;

import com.orders.domain.exception.OrderNotFoundException;
import com.orders.domain.model.Order;
import com.orders.domain.port.in.CreateOrderUseCase;
import com.orders.domain.port.in.GetOrderUseCase;
import com.orders.domain.port.in.GetOrdersByCustomerUseCase;
import com.orders.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.orders.infrastructure.adapter.in.web.dto.ErrorResponse;
import com.orders.infrastructure.adapter.in.web.dto.OrderResponse;
import com.orders.infrastructure.adapter.in.web.dto.ValidationErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints to management purchase orders")
@Validated
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrdersByCustomerUseCase getOrdersByCustomerUseCase;
    private final com.orders.domain.port.in.GetOrdersByCustomerReactiveUseCase getOrdersByCustomerReactiveUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "ordersByCustomer", key = "#request.customer.documentNumber")
    @Operation(
        summary = "Create new order",
        description = "initiates the process of creating a controlled and validated order"
    )
    @ApiResponses( value = {
        @ApiResponse(
            responseCode = "201",
            description = "Purchase order generated",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "invalid input data",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order orderToCreate = OrderWebMapper.toDomain(request);
        Order createdOrder = createOrderUseCase.createOrder(orderToCreate);
        return OrderWebMapper.toResponse(createdOrder);
    }

    @GetMapping("/{id}")
        @Operation(
        summary = "View a purchase order by ID",
        description = "Search and return the details of a purchase order based on an ID"
    )
    @ApiResponses( value = {
        @ApiResponse(
            responseCode = "204",
            description = "purchase order found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "purchase order not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public OrderResponse getOrderById(@PathVariable String id) {
        return getOrderUseCase.getOrderById(id)
                .map(OrderWebMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found"));
    }

    @GetMapping("/customer/{documentNumber}")
    @Cacheable(value = "ordersByCustomer", key = "#documentNumber")
    @Operation(
        summary = "Check orders by document number",
        description = "Searches and returns the list of orders associated with a document number. Validates the document format and uses caching."
    )
        @ApiResponses( value = {
        @ApiResponse(
            responseCode = "200",
            description = "Purchase order found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "invalid document format",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public List<OrderResponse> getOrderByCustomerDocument(
        @PathVariable
        @Pattern(regexp = "^\\d{5,12}$", message = "Document number must be numeric and between 5 and 12 digits")
        String documentNumber
    ){
        return getOrdersByCustomerUseCase.getOrdersByCustomerDocument(documentNumber)
                .stream()
                .map(OrderWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/customer/{documentNumber}/reactive")
    @Operation(
        summary = "Check orders reactively by document number",
        description = "Searches and returns a reactive flow of orders associated with a document number. Validates the document format."
    )
        @ApiResponses( value = {
        @ApiResponse(
            responseCode = "200",
            description = "Purchase orders stream",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "invalid document format",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "customer not found or has no orders",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public reactor.core.publisher.Mono<List<OrderResponse>> getOrderByCustomerDocumentReactive(
        @PathVariable
        @Pattern(regexp = "^\\d{5,12}$", message = "Document number must be numeric and between 5 and 12 digits" )
        String documentNumber
    ){
        return getOrdersByCustomerReactiveUseCase.getOrdersByCustomerDocumentReactive(documentNumber)
                    .map(OrderWebMapper::toResponse)
                    .collectList();
    }
}
