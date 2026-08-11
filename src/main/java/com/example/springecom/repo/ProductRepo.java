package com.example.springecom.repo;

import com.example.springecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :key, '%'))")
    List<Product> searchProducts(@Param("key") String key);

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity, " +
           "p.productAvailable = CASE WHEN (p.stockQuantity - :quantity) = 0 THEN false ELSE p.productAvailable END " +
           "WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int decreaseStock(@Param("id") Integer id, @Param("quantity") int quantity);
}
