package com.oyakov.bookstore.converter;

import com.oyakov.bookstore.entity.CustomerEntity;
import com.oyakov.bookstore.model.CustomerRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerRequestToEntityConverter implements Converter<CustomerRequest, CustomerEntity> {

    @Override
    public CustomerEntity convert(CustomerRequest source) {
        CustomerEntity customer = new CustomerEntity();
        customer.setName(source.getName());
        return customer;
    }
}
