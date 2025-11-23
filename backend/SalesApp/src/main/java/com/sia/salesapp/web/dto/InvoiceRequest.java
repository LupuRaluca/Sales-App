package com.sia.salesapp.web.dto;

public record InvoiceRequest(
        String invoiceNumber,
        Long orderId
) {}

