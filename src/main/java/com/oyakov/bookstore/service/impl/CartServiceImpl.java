package com.oyakov.bookstore.service.impl;

import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.entity.CartEntity;
import com.oyakov.bookstore.entity.CustomerEntity;
import com.oyakov.bookstore.entity.PurchaseOrderEntity;
import com.oyakov.bookstore.exception.NotFoundException;
import com.oyakov.bookstore.repository.CartRepository;
import com.oyakov.bookstore.repository.BookRepository;
import com.oyakov.bookstore.repository.CustomerRepository;
import com.oyakov.bookstore.repository.PurchaseOrderRepository;
import com.oyakov.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
/*
 * This is a draft implementation of Cart service, that might be used application to handle
 * more complex purchasing logic
 */
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public CartEntity addToCart(Long customerId, Long bookId) {
        CartEntity cart = cartRepository.findByCustomerIdAndCheckedOutFalse(customerId)
                .orElseGet(() -> {
                    CustomerEntity customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new NotFoundException("Customer not found: %d".formatted(customerId)));
                    CartEntity newCart = new CartEntity();
                    newCart.setCustomer(customer);
                    return newCart;
                });

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found: %d".formatted(bookId)));
        cart.getBooks().add(book);
        return cartRepository.save(cart);
    }

    @Override
    public CartEntity getCart(Long customerId) {
        return cartRepository.findByCustomerIdAndCheckedOutFalse(customerId)
                .orElseThrow(() -> new NotFoundException("No active cart found for customer: %d".formatted(customerId)));
    }

    @Transactional
    @Override
    public PurchaseOrderEntity checkoutCart(Long customerId) {
        CartEntity cart = getCart(customerId);
        // Reuse existing service logic here
        // After checkout:
        cart.setCheckedOut(true);
        cartRepository.save(cart);
        // Return PurchaseOrderEntity after processing
        return null; // stubbed
    }
}

