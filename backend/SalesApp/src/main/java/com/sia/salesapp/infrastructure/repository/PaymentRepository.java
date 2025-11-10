package com.sia.salesapp.infrastructure.repository;

import com.sia.salesapp.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> { }
