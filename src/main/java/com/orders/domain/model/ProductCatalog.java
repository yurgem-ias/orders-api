package com.orders.domain.model;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductCatalog {

    private final List<Product> products;

    public ProductCatalog(List<Product> products){
        this.products = List.copyOf(products);
    }

    public List<Product> getAvailableProducts(){
        Predicate<Product> isAvailable = Product::isAvailable;
        return filterProducts(isAvailable);
    }

    private List<Product> filterProducts(Predicate<Product> condition) {
        return products.stream()
            .filter(condition)
            .collect(Collectors.toList());
    }

    public Map<String, List<Product>> groupProductsByCategory(){
        return products.stream()
            .collect(Collectors.groupingBy(Product::getCategory));
    }
}
