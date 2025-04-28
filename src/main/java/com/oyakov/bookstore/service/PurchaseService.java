package com.oyakov.bookstore.service;

import com.oyakov.bookstore.entity.PurchaseOrderEntity;

import java.util.List;

public interface PurchaseService {
    PurchaseOrderEntity purchaseBooks(Long customerId, List<Long> bookIds);
}

