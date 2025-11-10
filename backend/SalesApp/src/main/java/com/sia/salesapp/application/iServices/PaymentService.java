package com.sia.salesapp.application.iServices;
import com.sia.salesapp.web.dto.*;
import java.util.List;

public interface PaymentService {
    PaymentResponse create(PaymentRequest req);
    PaymentResponse update(Long id, PaymentRequest req);
    void delete(Long id);
    PaymentResponse get(Long id);
    List<PaymentResponse> list();
}
