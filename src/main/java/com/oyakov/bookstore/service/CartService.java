package com.oyakov.bookstore.service;

import com.oyakov.bookstore.entity.CartEntity;
import com.oyakov.bookstore.entity.PurchaseOrderEntity;

public interface CartService {
    CartEntity addToCart(Long customerId, Long bookId);
    CartEntity getCart(Long customerId);
    PurchaseOrderEntity checkoutCart(Long customerId);
}

