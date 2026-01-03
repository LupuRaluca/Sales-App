package com.sia.salesapp.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CartItemResponse> cartItems,
        BigDecimal subtotal,  // <--- Preț produse
        BigDecimal tax,       // <--- TVA (ex: 19%)
        BigDecimal total      // <--- Total Final
) {}