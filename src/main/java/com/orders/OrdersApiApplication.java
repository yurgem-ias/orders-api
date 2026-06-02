package com.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OrdersApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApiApplication.class, args);
	}

}
