package com.sia.salesapp.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull Long brandId,
        @NotNull Long categoryId,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotNull @DecimalMin("0.00") @DecimalMax("99.99") BigDecimal vatRate
) { }
