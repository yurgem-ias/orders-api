package com.orders.infrastructure.adapter.in.web;

import com.orders.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.orders.infrastructure.adapter.in.web.dto.ErrorResponse;
import com.orders.infrastructure.adapter.in.web.dto.OrderResponse;
import com.orders.infrastructure.adapter.in.web.dto.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class OrderRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/orders",
            method = RequestMethod.POST,
            beanClass = OrderHandler.class,
            beanMethod = "createOrder",
            operation = @Operation(
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
        ),
        @RouterOperation(
            path = "/api/v1/orders/{id}",
            method = RequestMethod.GET,
            beanClass = OrderHandler.class,
            beanMethod = "getOrderById",
            operation = @Operation(
                summary = "View a purchase order by ID",
                description = "Search and return the details of a purchase order based on an ID",
                parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Order ID", required = true)
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
        ),
        @RouterOperation(
            path = "/api/v1/orders/customer/{documentNumber}",
            method = RequestMethod.GET,
            beanClass = OrderHandler.class,
            beanMethod = "getOrderByCustomerDocument",
            operation = @Operation(
                summary = "Check orders by document number",
                description = "Searches and returns the list of orders associated with a document number.",
                parameters = {
                    @Parameter(name = "documentNumber", in = ParameterIn.PATH, description = "Customer document number (5 to 12 digits)", required = true)
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
        )
    })
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler handler) {
        return RouterFunctions.route(POST("/api/v1/orders").and(accept(MediaType.APPLICATION_JSON)), handler::createOrder)
                .andRoute(GET("/api/v1/orders/{id}"), handler::getOrderById)
                .andRoute(GET("/api/v1/orders/customer/{documentNumber}"), handler::getOrderByCustomerDocument);
    }
}