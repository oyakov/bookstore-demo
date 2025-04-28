package com.oyakov.bookstore.domain.discount;

import com.oyakov.bookstore.config.DiscountConfig;
import com.oyakov.bookstore.enums.BookType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DiscountStrategyFactory {

    private final DiscountConfig discountConfig;

    public DiscountStrategy getDiscountStrategy() {
        return DiscountStrategy.builder()
                .discountApplicationStrategy(DiscountStrategy.CHEAPEST_FIRST)
                .discountRules(
                        Map.of(
                                BookType.NEW_RELEASE, ((price, _) -> price),
                                BookType.REGULAR, ((price, count) -> {
                                    if (count >= discountConfig.getRegularMultibuyThreshold())
                                        return price.multiply(discountConfig.getRegularMultibuyDiscount());
                                    return price;
                                }),
                                BookType.OLD_EDITION, ((price, count) -> {
                                    price = price.multiply(discountConfig.getRegularMultibuyDiscount());
                                    if (count >= discountConfig.getOldEditionMultibuyThreshold()) {
                                        return price.multiply(discountConfig.getOldEditionMultibuyDiscount());
                                    }
                                    return price;
                                }))
                ).build();
    }
}
