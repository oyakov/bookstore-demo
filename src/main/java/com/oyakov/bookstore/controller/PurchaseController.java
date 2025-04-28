package com.oyakov.bookstore.controller;

import com.oyakov.bookstore.api.PurchaseApi;
import com.oyakov.bookstore.entity.PurchaseOrderEntity;
import com.oyakov.bookstore.exception.DuplicateEntityException;
import com.oyakov.bookstore.exception.OutOfStockException;
import com.oyakov.bookstore.model.PurchaseResponse;
import com.oyakov.bookstore.model.PurchaseRequest;

import com.oyakov.bookstore.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PurchaseController implements PurchaseApi {

    private final PurchaseService purchaseService;

    private final ConversionService conversionService;

    @Override
    public ResponseEntity<PurchaseResponse> purchaseBooks(PurchaseRequest request) {
        PurchaseOrderEntity order =  purchaseService.purchaseBooks(request.getCustomerId(), request.getBookIds());
        PurchaseResponse result = conversionService.convert(order, PurchaseResponse.class);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<String> handleDuplicateEntityException(OutOfStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("%s".formatted(ex.getMessage()));
    }
}

