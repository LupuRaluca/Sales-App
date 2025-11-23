
// OrderRequest.java
package com.sia.salesapp.web.dto;

import java.math.BigDecimal;

public record OrderRequest(
        String status,          // va fi convertit în enum
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal taxTotal,
        BigDecimal grandTotal,
        String currency,
        String shippingFullName,
        String shippingPhone,
        String shippingAddress
) {}
