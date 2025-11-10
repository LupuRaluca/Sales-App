package com.sia.salesapp.infrastructure.repository;


import com.sia.salesapp.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> { }
