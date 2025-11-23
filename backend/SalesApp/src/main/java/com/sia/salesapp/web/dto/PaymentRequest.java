
// src/main/java/com/sia/salesapp/web/dto/PaymentRequest.java
package com.sia.salesapp.web.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String provider,       // ex: "STRIPE", "PAYPAL", "NETOPIA", "DUMMY"
        String status,         // ex: "INITIATED", "AUTHORIZED", "CAPTURED", "FAILED", "REFUNDED"
        BigDecimal amount,
        String currency,       // opțional; dacă e null sau gol → "RON"
        String transactionRef, // opțional
        Long orderId
) {}
