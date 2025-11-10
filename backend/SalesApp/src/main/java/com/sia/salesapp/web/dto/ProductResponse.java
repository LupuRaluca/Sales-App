package com.sia.salesapp.web.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Long brandId,
        String brandName,     // util în listări/detalii
        Long categoryId,
        String categoryName,  // util în listări/detalii
        BigDecimal price,
        String currency,
        BigDecimal vatRate,
        Integer inventoryQuantityAvailable // poate fi null dacă nu există încă rând în inventory
) { }
