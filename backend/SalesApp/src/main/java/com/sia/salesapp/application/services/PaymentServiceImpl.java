
package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.PaymentService;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.domain.entity.Payment;
import com.sia.salesapp.domain.enums.PaymentProvider;
import com.sia.salesapp.domain.enums.PaymentStatus;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.infrastructure.repository.PaymentRepository;
import com.sia.salesapp.web.dto.PaymentRequest;
import com.sia.salesapp.web.dto.PaymentResponse;
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

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest req) {
        Order order = orderRepo.findById(req.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));

        Payment payment = Payment.builder()
                .order(order)
                .provider(parseProvider(req.provider()))
                .status(parseStatus(req.status()))
                .amount(req.amount())
                .currency(normalizeCurrency(req.currency()))
                .transactionRef(req.transactionRef())
                .build();

        payment = repo.save(payment);

        return mapToResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse update(Long id, PaymentRequest req) {
        Payment p = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment inexistent"));

        if (req.orderId() != null) {
            Order order = orderRepo.findById(req.orderId())
                    .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
            p.setOrder(order);
        }

        if (req.provider() != null) {
            p.setProvider(parseProvider(req.provider()));
        }
        if (req.status() != null) {
            p.setStatus(parseStatus(req.status()));
        }
        if (req.amount() != null) {
            p.setAmount(req.amount());
        }
        if (req.currency() != null) {
            p.setCurrency(normalizeCurrency(req.currency()));
        }
        p.setTransactionRef(req.transactionRef());

        p = repo.save(p);

        return mapToResponse(p);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public PaymentResponse get(Long id) {
        Payment p = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment inexistent"));
        return mapToResponse(p);
    }

    @Override
    public List<PaymentResponse> list() {
        return repo.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ---------- helpers ----------

    private PaymentResponse mapToResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getProvider().name(),
                p.getStatus().name(),
                p.getAmount(),
                p.getCurrency(),
                p.getTransactionRef(),
                p.getCreatedAt(),
                p.getOrder() != null ? p.getOrder().getId() : null
        );
    }

    private PaymentProvider parseProvider(String value) {
        try {
            return PaymentProvider.valueOf(normalizeEnum(value));
        } catch (Exception e) {
            throw new IllegalArgumentException("PaymentProvider invalid: " + value);
        }
    }

    private PaymentStatus parseStatus(String value) {
        try {
            return PaymentStatus.valueOf(normalizeEnum(value));
        } catch (Exception e) {
            throw new IllegalArgumentException("PaymentStatus invalid: " + value);
        }
    }

    private String normalizeEnum(String s) {
        return s == null ? null : s.trim().toUpperCase();
    }

    private String normalizeCurrency(String ccy) {
        String v = ccy == null ? "RON" : ccy.trim();
        return v.isEmpty() ? "RON" : v.toUpperCase();
    }
}
