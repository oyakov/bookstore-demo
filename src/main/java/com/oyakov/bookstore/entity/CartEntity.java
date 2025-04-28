package com.oyakov.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "carts")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private CustomerEntity customer;

    @ManyToMany
    private List<BookEntity> books;

    private boolean checkedOut = false; // indicates if this cart has been purchased
}

