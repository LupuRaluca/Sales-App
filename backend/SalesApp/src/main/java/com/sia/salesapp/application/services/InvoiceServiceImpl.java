
package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.InvoiceService;
import com.sia.salesapp.domain.entity.Invoice;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.infrastructure.repository.InvoiceRepository;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.web.dto.InvoiceRequest;
import com.sia.salesapp.web.dto.InvoiceResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository repo;
    private final OrderRepository orderRepo;

    @Override
    @Transactional
    public InvoiceResponse create(InvoiceRequest req) {
        Order order = orderRepo.findById(req.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));

        Invoice invoice = Invoice.builder()
                .invoiceNumber(req.invoiceNumber())
                .order(order)
                .build();

        invoice = repo.save(invoice);

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getCreatedAt(), // folosim createdAt
                invoice.getOrder().getId()
        );
    }

    @Override
    @Transactional
    public InvoiceResponse update(Long id, InvoiceRequest req) {
        Invoice invoice = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice inexistent"));

        invoice.setInvoiceNumber(req.invoiceNumber());
        if (req.orderId() != null) {
            Order order = orderRepo.findById(req.orderId())
                    .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
            invoice.setOrder(order);
        }

        invoice = repo.save(invoice);

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getCreatedAt(), // rămâne createdAt
                invoice.getOrder().getId()
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public InvoiceResponse get(Long id) {
        Invoice invoice = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice inexistent"));

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getCreatedAt(),
                invoice.getOrder().getId()
        );
    }

    @Override
    public List<InvoiceResponse> list() {
        return repo.findAll().stream()
                .map(invoice -> new InvoiceResponse(
                        invoice.getId(),
                        invoice.getInvoiceNumber(),
                        invoice.getCreatedAt(),
                        invoice.getOrder() != null ? invoice.getOrder().getId() : null
                ))
                .toList();
    }
}
