package com.oyakov.bookstore.converter;

import com.oyakov.bookstore.entity.CustomerEntity;
import com.oyakov.bookstore.model.CustomerResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityToResponseConverter implements Converter<CustomerEntity, CustomerResponse> {
    @Override
    public CustomerResponse convert(CustomerEntity source) {
        CustomerResponse response = new CustomerResponse();
        response.setId(source.getId());
        response.setName(source.getName());
        response.setLoyaltyPoints(source.getLoyaltyPoints());
        return response;
    }
}
