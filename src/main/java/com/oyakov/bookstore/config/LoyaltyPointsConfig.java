package com.oyakov.bookstore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bookstore.loyalty")
@Data
public class LoyaltyPointsConfig {
    private Integer discountThreshold = 10;
}
