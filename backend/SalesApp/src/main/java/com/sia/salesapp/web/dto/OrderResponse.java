package com.sia.salesapp.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderResponse(Long id,
                            LocalDate orderDate,
                            String status,
                            BigDecimal totalAmount) {
}
