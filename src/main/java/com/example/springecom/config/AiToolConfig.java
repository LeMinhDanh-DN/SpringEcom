package com.example.springecom.config;

import com.example.springecom.model.Product;
import com.example.springecom.service.ProductService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class AiToolConfig {

    private final ProductService service;

    public AiToolConfig(ProductService service) {
        this.service = service;
    }

    public record ProductSearchRequest(String query){}
    public record ProductSearchResponse(List<ProductInfo> products){}
    public record ProductInfo(Integer id, String name, String brand, BigDecimal price, String category, String description, int stock) {}


    @Tool(description = "Search for products in the e-commerce store catalog by a query keyword")
    public ProductSearchResponse searchProduct(ProductSearchRequest request){

        List<Product> products = this.service.searchProduct(request.query());

        List<ProductInfo> productInfos = products.stream()
                .map(p -> new ProductInfo(
                        p.getId(),
                        p.getName(),
                        p.getBrand(),
                        p.getPrice(),
                        p.getCategory(),
                        p.getDescription(),
                        p.getStockQuantity()
                )).toList();

        return new ProductSearchResponse(productInfos);
    }
}