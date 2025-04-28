package com.oyakov.bookstore.service;

import com.oyakov.bookstore.domain.PriceCalculator;
import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.entity.CustomerEntity;
import com.oyakov.bookstore.entity.PurchaseOrderEntity;
import com.oyakov.bookstore.enums.BookType;
import com.oyakov.bookstore.exception.NotFoundException;
import com.oyakov.bookstore.exception.OutOfStockException;
import com.oyakov.bookstore.repository.BookRepository;
import com.oyakov.bookstore.repository.CustomerRepository;
import com.oyakov.bookstore.repository.PurchaseOrderRepository;
import com.oyakov.bookstore.service.impl.PurchaseServiceImpl;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
class PurchaseServiceTest {

    private BookRepository bookRepository;
    private CustomerRepository customerRepository;
    private CustomerService customerService;
    private PurchaseOrderRepository purchaseOrderRepository;
    private PurchaseServiceImpl purchaseService;

    @Autowired
    private PriceCalculator priceCalculator;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        customerRepository = mock(CustomerRepository.class);
        customerService = mock(CustomerService.class);
        purchaseOrderRepository = mock(PurchaseOrderRepository.class);
        purchaseService = new PurchaseServiceImpl(bookRepository, purchaseOrderRepository, customerService, priceCalculator);
    }

    @Test
    void purchaseBooks_shouldProcessOrderSuccessfully() {
        Long customerId = 1L;
        Long bookId = 2L;

        CustomerEntity customer = new CustomerEntity();
        customer.setId(customerId);
        customer.setLoyaltyPoints(5);

        BookEntity book = new BookEntity();
        book.setId(bookId);
        book.setTitle("Book Title");
        book.setType(BookType.REGULAR);
        book.setQuantity(3);
        book.setBasePrice(BigDecimal.TEN);

        when(customerService.getCustomerById(customerId)).thenReturn(customer);
        when(customerService.tryApplyDiscount(customerId)).thenReturn(false);

        when(bookRepository.lockBookById(bookId)).thenReturn(Optional.of(book));

        when(purchaseOrderRepository.save(any(PurchaseOrderEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(purchaseOrderRepository.saveAndFlush(any(PurchaseOrderEntity.class)))
                .thenAnswer(invocation -> {
                    PurchaseOrderEntity savedOrder = invocation.getArgument(0);
                    savedOrder.setId(100L);
                    return savedOrder;
                });

        doNothing().when(customerService).addLoyaltyPoints(eq(customerId), anyInt());

        PurchaseOrderEntity order = purchaseService.purchaseBooks(customerId, List.of(bookId));

        assertEquals(1, order.getOrderBooks().size());
        assertEquals(customer, order.getCustomer());
        assertNotNull(order.getTotalPrice());
        assertEquals(2, book.getQuantity()); // stock decreased

        verify(customerService).addLoyaltyPoints(eq(customerId), eq(1));
        verify(bookRepository).flush();
        verify(purchaseOrderRepository).save(order);
    }

    @Test
    void purchaseBooks_shouldThrowIfCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> purchaseService.purchaseBooks(1L, List.of(2L)));
    }

    @Test
    void purchaseBooks_shouldThrowIfBookNotFound() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.lockBookById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> purchaseService.purchaseBooks(1L, List.of(2L)));
    }

    @Test
    void purchaseBooks_shouldThrowIfBookOutOfStock() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        BookEntity book = new BookEntity();
        book.setId(2L);
        book.setTitle("Book Title");
        book.setQuantity(0); // Out of stock

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.lockBookById(2L)).thenReturn(Optional.of(book));

        assertThrows(OutOfStockException.class, () -> purchaseService.purchaseBooks(1L, List.of(2L)));
    }
}

