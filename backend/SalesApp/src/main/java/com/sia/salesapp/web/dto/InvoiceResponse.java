package com.sia.salesapp.web.dto;

import java.time.Instant;
import java.time.LocalDate;


public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        Instant createdAt,
        Long orderId
) {}
