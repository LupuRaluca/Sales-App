package com.sia.salesapp.web.dto;

public record InventoryResponse(
        Long id,
        Long productId,
        Integer quantityAvailable
) { }
