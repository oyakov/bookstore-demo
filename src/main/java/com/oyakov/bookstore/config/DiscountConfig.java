package com.oyakov.bookstore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "bookstore.discount")
@Data
public class DiscountConfig {
    private Integer regularMultibuyThreshold = 3;
    private BigDecimal regularMultibuyDiscount = BigDecimal.valueOf(0.90);
    private BigDecimal oldEditionRegularDiscount = BigDecimal.valueOf(0.80);
    private Integer oldEditionMultibuyThreshold = 3;
    private BigDecimal oldEditionMultibuyDiscount = BigDecimal.valueOf(0.95);
}
