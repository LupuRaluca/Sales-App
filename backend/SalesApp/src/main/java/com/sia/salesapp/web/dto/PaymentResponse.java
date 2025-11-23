
// src/main/java/com/sia/salesapp/web/dto/PaymentResponse.java
package com.sia.salesapp.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        String provider,
        String status,
        BigDecimal amount,
        String currency,
        String transactionRef,
        Instant createdAt,
        Long orderId
) {}
