package com.sia.salesapp.web.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryName,
        BigDecimal price,
        String currency,
        BigDecimal vatRate
) { }