package com.oyakov.bookstore.domain.discount;

import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.enums.BookType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiFunction;

@Builder
@Data
public class DiscountStrategy {

    // Strategies
    public static Comparator<BookEntity> CHEAPEST_FIRST = Comparator.comparing(BookEntity::getBasePrice);
    public static Comparator<BookEntity> MOST_EXPENSIVE_FIRST = Comparator.comparing(BookEntity::getBasePrice).reversed();

    private Map<BookType, BiFunction<BigDecimal, Long, BigDecimal>> discountRules;
    private Comparator<BookEntity> discountApplicationStrategy = CHEAPEST_FIRST;
}
