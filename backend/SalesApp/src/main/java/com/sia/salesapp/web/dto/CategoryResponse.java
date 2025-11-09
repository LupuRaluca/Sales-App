package com.sia.salesapp.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryResponse(Long id, String name, String description) { }
