package com.orders.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "orders.exchange";
    public static final String QUEUE = "orders.notifications.queue";
    public static final String ROUTING_KEY = "order.created";
    public static final String DLQ_EXCHANGE = "orders.dlq.exchange";
    public static final String DLQ_QUEUE = "orders.notifications.dlq.queue";
    public static final String DLQ_ROUTIN_KEY = "order.created.dql";

    @Bean
    public DirectExchange oDirectExchange(){
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue ordersQueue(){
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTIN_KEY)
                .build();
    }

    @Bean
    public Binding ordersBinding(Queue ordersQueue, @Qualifier("oDirectExchange") DirectExchange ordersExchange){
        return BindingBuilder.bind(ordersQueue).to(ordersExchange).with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange dlqExchange(){
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Queue dlqQueue(){
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding dlqBinding(Queue dlqQueue, @Qualifier("dlqExchange") DirectExchange dlqExchange){
        return BindingBuilder.bind(dlqQueue).to(dlqExchange).with(DLQ_ROUTIN_KEY);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
