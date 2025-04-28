package com.oyakov.bookstore.service;

import com.oyakov.bookstore.config.LoyaltyPointsConfig;
import com.oyakov.bookstore.entity.CustomerEntity;
import com.oyakov.bookstore.exception.NotFoundException;
import com.oyakov.bookstore.repository.CustomerRepository;
import com.oyakov.bookstore.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository customerRepository;
    private CustomerService customerService;
    private LoyaltyPointsConfig loyaltyPointsConfig;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerServiceImpl(customerRepository, loyaltyPointsConfig);
    }

    @Test
    void getAllCustomers_shouldReturnList() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(new CustomerEntity(), new CustomerEntity()));
        assertEquals(2, customerService.getAllCustomers().size());
        verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById_shouldReturnCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        CustomerEntity result = customerService.getCustomerById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCustomerById_shouldThrowNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> customerService.getCustomerById(1L));
    }

    @Test
    void createCustomer_shouldSetLoyaltyPointsAndSave() {
        CustomerEntity customer = new CustomerEntity();
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CustomerEntity result = customerService.createCustomer(customer);
        assertEquals(0, result.getLoyaltyPoints());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_shouldUpdateName() {
        CustomerEntity existing = new CustomerEntity();
        existing.setId(1L);
        existing.setName("Old Name");

        CustomerEntity updated = new CustomerEntity();
        updated.setName("New Name");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerEntity result = customerService.updateCustomer(1L, updated);
        assertEquals("New Name", result.getName());
    }

    @Test
    void deleteCustomer_shouldDeleteIfExists() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        customerService.deleteCustomer(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_shouldThrowIfNotExists() {
        when(customerRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> customerService.deleteCustomer(1L));
    }

    @Test
    void getLoyaltyPoints_shouldReturnPoints() {
        CustomerEntity customer = new CustomerEntity();
        customer.setLoyaltyPoints(10);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        assertEquals(10, customerService.getLoyaltyPoints(1L));
    }
}
