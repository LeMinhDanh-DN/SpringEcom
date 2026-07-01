package com.example.springecom.config;

import com.example.springecom.model.Product;
import com.example.springecom.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Configuration
public class AiToolConfig {

    public record ProductSearchRequest(String query){}

    public record ProductSearchResponse(List <ProductInfo> products){}

    public record ProductInfo(Integer id, String name, String brand, BigDecimal price, String category, String description, int stock) {}

    @Bean
    @Description("Search for products in the e-commerce store catalog by a query keyword")
    public Function<ProductSearchRequest, ProductSearchResponse> searchProduct(ProductService service){
        //return a function
        return (ProductSearchRequest request)->{
            List<Product> products = service.searchProduct(request.query());
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
        };
    }
}
