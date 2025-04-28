package com.oyakov.bookstore.domain;

import com.oyakov.bookstore.domain.discount.DiscountStrategy;
import com.oyakov.bookstore.domain.discount.DiscountStrategyFactory;
import com.oyakov.bookstore.entity.BookEntity;
import com.oyakov.bookstore.enums.BookType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.oyakov.bookstore.enums.BookType.OLD_EDITION;
import static com.oyakov.bookstore.enums.BookType.REGULAR;

@Component
@RequiredArgsConstructor
public class PriceCalculator {

    private final DiscountStrategyFactory discountStrategyFactory;

    private static final Set<BookType> ELIGIBLE_FOR_DISCOUNT = Set.of(OLD_EDITION, REGULAR);

    public BigDecimal calculateTotal(List<BookEntity> bookEntities,
                                            boolean applyLoyaltyDiscount) {
        BigDecimal total = BigDecimal.ZERO;
        DiscountStrategy discountStrategy = discountStrategyFactory.getDiscountStrategy();

        Map<BookType, Long> typeCount = bookEntities.stream()
                .collect(Collectors.groupingBy(BookEntity::getType, Collectors.counting()));

        for (BookEntity bookEntity : bookEntities) {
            BigDecimal price = bookEntity.getBasePrice();
            var rule = discountStrategy.getDiscountRules().get(bookEntity.getType());
            price = rule.apply(price, typeCount.get(bookEntity.getType()));
            total = total.add(price);
        }

        if (applyLoyaltyDiscount) {
            Optional<BookEntity> freeBook = bookEntities.stream()
                    .filter(b -> ELIGIBLE_FOR_DISCOUNT.contains(b.getType()))
                    .min(discountStrategy.getDiscountApplicationStrategy());
            if (freeBook.isPresent()) {
                total = total.subtract(freeBook.get().getBasePrice());
            }
        }
        return total;
    }
}

