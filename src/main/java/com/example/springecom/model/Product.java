package com.example.springecom.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private String category;
    private int stockQuantity;

    private LocalDate releaseDate;
    private boolean productAvailable;


    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

}
