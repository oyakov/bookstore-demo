package com.oyakov.bookstore.controller;

import com.oyakov.bookstore.api.CustomersApi;
import com.oyakov.bookstore.entity.CustomerEntity;
import com.oyakov.bookstore.model.CustomerRequest;
import com.oyakov.bookstore.model.CustomerResponse;
import com.oyakov.bookstore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    private final ConversionService conversionService;

    @Override
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerEntity> customers = customerService.getAllCustomers();
        List<CustomerResponse> responses = customers.stream()
                .map(customer -> conversionService.convert(customer, CustomerResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        CustomerEntity customer = customerService.getCustomerById(id);
        CustomerResponse response = conversionService.convert(customer, CustomerResponse.class);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        CustomerEntity entity = conversionService.convert(request, CustomerEntity.class);
        CustomerEntity created = customerService.createCustomer(entity);
        CustomerResponse response = conversionService.convert(created, CustomerResponse.class);
        return ResponseEntity.created(URI.create("/customers/%d".formatted(response.getId())))
                .body(response);
    }

    @Override
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @RequestBody CustomerRequest request) {
        CustomerEntity entity = conversionService.convert(request, CustomerEntity.class);
        CustomerEntity updated = customerService.updateCustomer(id, entity);
        CustomerResponse response = conversionService.convert(updated, CustomerResponse.class);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Integer> getLoyaltyPoints(@PathVariable Long id) {
        int points = customerService.getLoyaltyPoints(id);
        return ResponseEntity.ok(points);
    }
}


