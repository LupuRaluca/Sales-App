package com.sia.salesapp.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        String status,
        String currency,
        BigDecimal shippingFee,
        String shippingFullName,
        String shippingPhone,
        String shippingAddress,
        List<OrderItemRequest> items
) {}