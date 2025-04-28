package com.oyakov.bookstore.repository;

import com.oyakov.bookstore.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {}
