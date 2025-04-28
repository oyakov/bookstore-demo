package com.oyakov.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "purchase_orders_books")
public class PurchaseOrderBooksEntity {

    @EmbeddedId
    private PurchaseOrderBookId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId("purchaseOrderId")
    private PurchaseOrderEntity purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId("bookId")
    private BookEntity book;

    @Column(nullable = false)
    private Integer quantity;
}

