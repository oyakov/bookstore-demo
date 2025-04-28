package com.oyakov.bookstore.repository;

import com.oyakov.bookstore.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByCustomerIdAndCheckedOutFalse(Long customerId);
}

