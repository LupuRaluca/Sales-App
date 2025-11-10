package com.sia.salesapp.web.dto;
import java.time.LocalDateTime;
public record CartResponse(  Long id,
                             Long userId,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) { }
