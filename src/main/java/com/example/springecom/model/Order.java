package com.example.springecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(unique = true)
    private String orderId;
    private String customerName;
    private String email;
    private String status;
    private LocalDate orderDate;

    @ManyToOne
    private User user;


    //cascade
    //khi cap nhat order thi toan bo item cung cap nhat
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItem> items;
}
