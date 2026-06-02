package com.orders.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ProductCatalogTest {

    @Test
    void shouldFilterAvailableProducts(){
        Product product1 = Product.builder().id("1").available(true).build();
        Product product2 = Product.builder().id("2").available(false).build();
        Product product3 = Product.builder().id("3").available(true).build();

        ProductCatalog catalog = new ProductCatalog(List.of(product1,product2,product3));
        List<Product> available = catalog.getAvailableProducts();

        assertEquals(2, available.size());
        assertTrue(available.contains(product1));
        assertTrue(available.contains(product3));
    }

        @Test
    void shouldGroupProductsByCategory(){
        Product product1 = Product.builder().id("1").category("Electronics").build();
        Product product2 = Product.builder().id("2").category("Books").build();
        Product product3 = Product.builder().id("3").category("Electronics").build();

        ProductCatalog catalog = new ProductCatalog(List.of(product1,product2,product3));
        Map<String,List<Product>> grouped = catalog.groupProductsByCategory();

        assertEquals(2, grouped.size());
        assertEquals(2,grouped.get("Electronics").size());
        assertEquals(1,grouped.get("Books").size());
    }

}
