package com.sia.salesapp.infrastructure.repository;

import com.sia.salesapp.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> { }
