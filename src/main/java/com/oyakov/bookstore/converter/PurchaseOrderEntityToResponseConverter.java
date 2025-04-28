package com.oyakov.bookstore.converter;

import com.oyakov.bookstore.entity.PurchaseOrderEntity;
import com.oyakov.bookstore.model.PurchaseResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderEntityToResponseConverter implements Converter<PurchaseOrderEntity, PurchaseResponse> {
    @Override
    public PurchaseResponse convert(PurchaseOrderEntity source) {
        PurchaseResponse response = new PurchaseResponse();
        response.setOrderId(source.getId());
        response.setTotalPrice(source.getTotalPrice());
        return response;
    }
}
