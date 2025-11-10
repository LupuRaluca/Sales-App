package com.sia.salesapp.application.services;
import com.sia.salesapp.application.iServices.InvoiceService;
import com.sia.salesapp.domain.entity.Invoice;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.infrastructure.repository.InvoiceRepository;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.web.dto.*;
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

    @Override @Transactional
    public InvoiceResponse create(InvoiceRequest req) {
        Order order = orderRepo.findById(req.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
        Invoice i = repo.save(Invoice.builder()
                .invoiceNumber(req.invoiceNumber())
                .issuedDate(req.issuedDate())
                .order(order)
                .build());
        return new InvoiceResponse(i.getId(), i.getInvoiceNumber(), i.getIssuedDate(), i.getOrder().getId());
    }

    @Override @Transactional
    public InvoiceResponse update(Long id, InvoiceRequest req) {
        Invoice i = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Invoice inexistent"));
        i.setInvoiceNumber(req.invoiceNumber());
        i.setIssuedDate(req.issuedDate());
        if (req.orderId() != null) {
            Order order = orderRepo.findById(req.orderId())
                    .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
            i.setOrder(order);
        }
        i = repo.save(i);
        return new InvoiceResponse(i.getId(), i.getInvoiceNumber(), i.getIssuedDate(), i.getOrder().getId());
    }

    @Override @Transactional
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public InvoiceResponse get(Long id) {
        Invoice i = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Invoice inexistent"));
        return new InvoiceResponse(i.getId(), i.getInvoiceNumber(), i.getIssuedDate(), i.getOrder().getId());
    }

    @Override
    public List<InvoiceResponse> list() {
        return repo.findAll().stream()
                .map(i -> new InvoiceResponse(i.getId(), i.getInvoiceNumber(), i.getIssuedDate(),
                        i.getOrder() != null ? i.getOrder().getId() : null))
                .toList();
    }
}
