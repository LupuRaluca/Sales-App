
package com.sia.salesapp.application.services;

import com.sia.salesapp.application.extendedServices.OrderComputationService;
import com.sia.salesapp.application.iServices.OrderService;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.domain.enums.OrderStatus;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.web.dto.OrderRequest;
import com.sia.salesapp.web.dto.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo;
    private final OrderComputationService computationService;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest req) {
        Order o = Order.builder()
                .status(OrderStatus.valueOf(req.status()))
                .subtotal(req.subtotal())
                .shippingFee(req.shippingFee())
                .taxTotal(req.taxTotal())
                .grandTotal(req.grandTotal())
                .currency(req.currency())
                .shippingFullName(req.shippingFullName())
                .shippingPhone(req.shippingPhone())
                .shippingAddress(req.shippingAddress())
                .build();

        o = repo.save(o);

        return mapToResponse(o);
    }

    @Override
    @Transactional
    public OrderResponse update(Long id, OrderRequest req) {
        Order o = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));

        o.setStatus(OrderStatus.valueOf(req.status()));
        o.setShippingFee(req.shippingFee());
        o.setCurrency(req.currency());
        o.setShippingFullName(req.shippingFullName());
        o.setShippingPhone(req.shippingPhone());
        o.setShippingAddress(req.shippingAddress());
        computationService.computeTotals(o);
        o = repo.save(o);

        return mapToResponse(o);
    }

    @Transactional
    public void refreshOrderTotals(Long orderId) {
        Order o = repo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
        computationService.computeTotals(o);
        repo.save(o);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public OrderResponse get(Long id) {
        Order o = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
        return mapToResponse(o);
    }

    @Override
    public List<OrderResponse> list() {
        return repo.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getStatus().name(),
                o.getSubtotal(),
                o.getShippingFee(),
                o.getTaxTotal(),
                o.getGrandTotal(),
                o.getCurrency(),
                o.getShippingFullName(),
                o.getShippingPhone(),
                o.getShippingAddress(),
                o.getCreatedAt()
        );
    }
}
