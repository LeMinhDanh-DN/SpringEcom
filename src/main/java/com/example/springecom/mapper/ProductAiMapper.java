package com.example.springecom.mapper;

import com.example.springecom.model.Product;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class ProductAiMapper {

    public Document toAiDocument(Product product){
        String content = String.format(
                "Product: %s. Description: %s. Price: %.2f. Category: %s, Stock Quantity: %s",
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStockQuantity()
        );

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("productId", product.getId());
        metadata.put("price", product.getPrice());
        metadata.put("category", product.getCategory());
        metadata.put("brand", product.getBrand());
        metadata.put("stockQuantity", product.getStockQuantity());

        return new Document(content,metadata);
    }
}
