package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.PaymentService;
import com.sia.salesapp.domain.entity.Payment;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.infrastructure.repository.PaymentRepository;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repo;
    private final OrderRepository orderRepo;

    @Override @Transactional
    public PaymentResponse create(PaymentRequest req) {
        Order order = orderRepo.findById(req.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
        Payment p = repo.save(Payment.builder()
                .paymentDate(req.paymentDate())
                .amount(req.amount())
                .method(req.method())
                .order(order)
                .build());
        return new PaymentResponse(p.getId(), p.getPaymentDate(), p.getAmount(), p.getMethod(), p.getOrder().getId());
    }

    @Override @Transactional
    public PaymentResponse update(Long id, PaymentRequest req) {
        Payment p = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Payment inexistent"));
        p.setPaymentDate(req.paymentDate());
        p.setAmount(req.amount());
        p.setMethod(req.method());
        if (req.orderId() != null) {
            Order order = orderRepo.findById(req.orderId())
                    .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
            p.setOrder(order);
        }
        p = repo.save(p);
        return new PaymentResponse(p.getId(), p.getPaymentDate(), p.getAmount(), p.getMethod(), p.getOrder().getId());
    }

    @Override @Transactional
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public PaymentResponse get(Long id) {
        Payment p = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Payment inexistent"));
        return new PaymentResponse(p.getId(), p.getPaymentDate(), p.getAmount(), p.getMethod(), p.getOrder().getId());
    }

    @Override
    public List<PaymentResponse> list() {
        return repo.findAll().stream()
                .map(p -> new PaymentResponse(p.getId(), p.getPaymentDate(), p.getAmount(), p.getMethod(),
                        p.getOrder() != null ? p.getOrder().getId() : null))
                .toList();
    }
}
