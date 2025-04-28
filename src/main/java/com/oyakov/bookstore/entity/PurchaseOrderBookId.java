package com.oyakov.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class PurchaseOrderBookId implements Serializable {

    @Column(name = "purchase_order_id")
    private Long purchaseOrderId;

    @Column(name = "books_id")
    private Long bookId;
}

