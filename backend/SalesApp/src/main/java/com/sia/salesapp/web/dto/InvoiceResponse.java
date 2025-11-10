package com.sia.salesapp.web.dto;

import java.time.LocalDate;

public record InvoiceResponse(Long id,
                              String invoiceNumber,
                              LocalDate issuedDate,
                              Long orderId) {
}
