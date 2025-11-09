package com.sia.salesapp.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotBlank String description,
        Long brandId,
        Long categoryId,
        @PositiveOrZero BigDecimal price,
        @NotBlank String currency,
        @DecimalMin("0.00") @DecimalMax("99.99") BigDecimal vatRate
) { }
