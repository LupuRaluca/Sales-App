package com.sia.salesapp.web.dto;

import java.time.LocalDate;

public record InvoiceRequest(String invoiceNumber,
                             LocalDate issuedDate,
                             Long orderId) {
}
