package com.sia.salesapp.web.dto;
import java.time.LocalDateTime;

public record CartRequest( Long userId,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) { }
