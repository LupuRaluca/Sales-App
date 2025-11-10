package com.sia.salesapp.application.iServices;

import com.sia.salesapp.web.dto.*;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse create(InvoiceRequest req);
    InvoiceResponse update(Long id, InvoiceRequest req);
    void delete(Long id);
    InvoiceResponse get(Long id);
    List<InvoiceResponse> list();
}
