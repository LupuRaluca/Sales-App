package com.sia.salesapp.web.dto;

import jakarta.validation.constraints.NotBlank;

public record BrandRequest(
        @NotBlank String name,
        @NotBlank String description
) { }
