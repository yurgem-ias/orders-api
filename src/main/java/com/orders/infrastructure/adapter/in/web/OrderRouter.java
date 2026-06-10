package com.orders.infrastructure.adapter.in.web;

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
            beanMethod = "createOrder"
        ),
        @RouterOperation(
            path = "/api/v1/orders/{id}",
            method = RequestMethod.GET,
            beanClass = OrderHandler.class,
            beanMethod = "getOrderById"
        ),
        @RouterOperation(
            path = "/api/v1/orders/customer/{documentNumber}",
            method = RequestMethod.GET,
            beanClass = OrderHandler.class,
            beanMethod = "getOrderByCustomerDocument"
        )
    })
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler handler) {
        return RouterFunctions.route(POST("/api/v1/orders").and(accept(MediaType.APPLICATION_JSON)), handler::createOrder)
                .andRoute(GET("/api/v1/orders/{id}"), handler::getOrderById)
                .andRoute(GET("/api/v1/orders/customer/{documentNumber}"), handler::getOrderByCustomerDocument);
    }
}