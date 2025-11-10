package com.sia.salesapp.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryCreateRequest(
        @NotNull Long productId,
        @NotNull @Min(0) Integer quantityAvailable
) { }
