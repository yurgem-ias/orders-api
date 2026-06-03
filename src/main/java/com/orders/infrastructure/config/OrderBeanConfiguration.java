package com.orders.infrastructure.config;

import com.orders.domain.port.in.CreateOrderUseCase;
import com.orders.domain.port.in.GetOrderUseCase;
import com.orders.domain.port.in.GetOrdersByCustomerReactiveUseCase;
import com.orders.domain.port.in.GetOrdersByCustomerUseCase;
import com.orders.domain.port.out.OrderRepositoryPort;
import com.orders.domain.usecase.CreateOrderUseCaseImpl;
import com.orders.domain.usecase.GetOrderUseCaseImpl;
import com.orders.domain.usecase.GetOrdersByCustomerReactiveUseCaseImpl;
import com.orders.domain.usecase.GetOrdersByCustomerUseCaseImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderBeanConfiguration {

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new CreateOrderUseCaseImpl(orderRepositoryPort);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrderUseCaseImpl(orderRepositoryPort);
    }

    @Bean
    public GetOrdersByCustomerUseCase getOrdersByCustomerUseCase(OrderRepositoryPort orderRepositoryPort){
        return new GetOrdersByCustomerUseCaseImpl(orderRepositoryPort);
    }

    @Bean
    public GetOrdersByCustomerReactiveUseCase getOrdersByCustomerReactiveUseCase(OrderRepositoryPort orderRepositoryPort){
        return new GetOrdersByCustomerReactiveUseCaseImpl(orderRepositoryPort);
    }
}
