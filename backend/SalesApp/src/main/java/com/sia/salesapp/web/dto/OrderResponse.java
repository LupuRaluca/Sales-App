
// OrderResponse.java
package com.sia.salesapp.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long id,
        String status,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal taxTotal,
        BigDecimal grandTotal,
        String currency,
        String shippingFullName,
        String shippingPhone,
        String shippingAddress,
        Instant createdAt
) {}
