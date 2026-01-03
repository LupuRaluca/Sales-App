package com.sia.salesapp.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Instant date,
        String status,
        BigDecimal total,
        List<OrderItemResponse> items,
        String shippingAddress
) {}