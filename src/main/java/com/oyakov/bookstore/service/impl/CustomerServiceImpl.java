package com.oyakov.bookstore.service.impl;

import com.oyakov.bookstore.config.LoyaltyPointsConfig;
import com.oyakov.bookstore.entity.CustomerEntity;
import com.oyakov.bookstore.exception.NotFoundException;
import com.oyakov.bookstore.repository.CustomerRepository;
import com.oyakov.bookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final LoyaltyPointsConfig lpConfig;

    @Override
    public List<CustomerEntity> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public CustomerEntity getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: %d".formatted(id)));
    }

    @Override
    @Transactional
    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        customerEntity.setLoyaltyPoints(0);
        return customerRepository.save(customerEntity);
    }

    @Override
    @Transactional
    public CustomerEntity updateCustomer(Long id, CustomerEntity updatedCustomerEntity) {
        CustomerEntity existing = getCustomerById(id);
        existing.setName(updatedCustomerEntity.getName());
        return customerRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found: %d".formatted(id));
        }
        customerRepository.deleteById(id);
    }

    @Override
    public int getLoyaltyPoints(Long id) {
        CustomerEntity customerEntity = getCustomerById(id);
        return customerEntity.getLoyaltyPoints();
    }

    @Override
    @Transactional
    public void addLoyaltyPoints(Long id, Integer amount) {
        CustomerEntity customer = getCustomerById(id);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + amount);
        customerRepository.save(customer);
    }


    private boolean isEligibleForDiscount(CustomerEntity customer) {
        return customer.getLoyaltyPoints() >= lpConfig.getDiscountThreshold();
    }

    @Override
    @Transactional
    public boolean tryApplyDiscount(Long id) {
        CustomerEntity customer = getCustomerById(id);
        if (isEligibleForDiscount(customer)) {
            customer.setLoyaltyPoints(0);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }
}

