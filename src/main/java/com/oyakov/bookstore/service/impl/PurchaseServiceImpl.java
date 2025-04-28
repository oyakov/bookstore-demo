package com.oyakov.bookstore.service.impl;

import com.oyakov.bookstore.domain.PriceCalculator;
import com.oyakov.bookstore.entity.*;
import com.oyakov.bookstore.exception.NotFoundException;
import com.oyakov.bookstore.exception.OutOfStockException;
import com.oyakov.bookstore.exception.PaymentFailedException;
import com.oyakov.bookstore.repository.BookRepository;
import com.oyakov.bookstore.repository.PurchaseOrderRepository;
import com.oyakov.bookstore.service.CustomerService;
import com.oyakov.bookstore.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final BookRepository bookRepo;
    private final PurchaseOrderRepository orderRepo;
    private final CustomerService customerService;
    private final PriceCalculator priceCalculator;

    @Transactional
    @Override
    public PurchaseOrderEntity purchaseBooks(Long customerId, List<Long> bookIds) {
        CustomerEntity customerEntity = customerService.getCustomerById(customerId);

        List<BookEntity> bookEntities = new ArrayList<>();
        for (Long bookId : bookIds) {
            BookEntity bookEntity = bookRepo.lockBookById(bookId)
                    .orElseThrow(() -> new NotFoundException("Book not found: %d".formatted(bookId)));
            if (bookEntity.getQuantity() == null || bookEntity.getQuantity() <= 0) {
                throw new OutOfStockException("Book out of stock: %s".formatted(bookEntity.getTitle()));
            }
            bookEntity.setQuantity(bookEntity.getQuantity() - 1);
            bookEntities.add(bookEntity);
        }

        boolean discountApplied;
        BigDecimal totalPrice;
        try {
            discountApplied = customerService.tryApplyDiscount(customerId);
            totalPrice =
                    priceCalculator.calculateTotal(
                            bookEntities,
                            discountApplied);
            // payment processing request can go here
            // put like this for simplicity
            // it is better not to handle payment processing inside the transaction
            // because transaction can roll back, but the use still might be charged
        } catch (Exception e) {
            // If payment fails throw an exception to roll back the changes in stock
            throw new PaymentFailedException("Payment processing failed");
        }

        // Add loyalty points for purchased books
        customerService.addLoyaltyPoints(customerId, bookEntities.size());

        bookRepo.flush();

        Map<BookEntity, Long> bookCounts = bookEntities.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        PurchaseOrderEntity order = new PurchaseOrderEntity();
        order.setCustomer(customerEntity);
        order.setTotalPrice(totalPrice);

        // Persist order to generate ID
        order = orderRepo.saveAndFlush(order);  // ID is now assigned

        // Add order books
        for (Map.Entry<BookEntity, Long> entry : bookCounts.entrySet()) {
            PurchaseOrderBooksEntity orderBook = new PurchaseOrderBooksEntity();
            PurchaseOrderBookId compositeId = new PurchaseOrderBookId();
            compositeId.setBookId(entry.getKey().getId());
            compositeId.setPurchaseOrderId(order.getId());
            orderBook.setId(compositeId);
            orderBook.setPurchaseOrder(order);
            orderBook.setBook(entry.getKey());
            orderBook.setQuantity(entry.getValue().intValue());
            order.getOrderBooks().add(orderBook);
        }
        return orderRepo.save(order);
    }
}

