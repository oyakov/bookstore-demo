package com.oyakov.bookstore.service;

import com.oyakov.bookstore.entity.CustomerEntity;

import java.util.List;

public interface CustomerService {
    List<CustomerEntity> getAllCustomers();
    CustomerEntity getCustomerById(Long id);
    CustomerEntity createCustomer(CustomerEntity customerEntity);
    CustomerEntity updateCustomer(Long id, CustomerEntity customerEntity);
    void deleteCustomer(Long id);
    int getLoyaltyPoints(Long id);
    void addLoyaltyPoints(Long id, Integer amount);
    boolean tryApplyDiscount(Long id);
}
