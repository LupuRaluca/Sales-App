package com.sia.salesapp.web.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record PaymentRequest(
        LocalDate paymentDate,
        BigDecimal amount,
        String method,
        Long orderId
) {
}
