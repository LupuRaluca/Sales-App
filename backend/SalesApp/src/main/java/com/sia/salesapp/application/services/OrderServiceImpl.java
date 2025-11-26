
package com.sia.salesapp.application.services;

import com.sia.salesapp.application.extendedServices.OrderComputationService;
import com.sia.salesapp.application.iServices.OrderService;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.domain.entity.OrderItem;
import com.sia.salesapp.domain.entity.Product;
import com.sia.salesapp.domain.enums.OrderStatus;
import com.sia.salesapp.domain.enums.PaymentStatus;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.infrastructure.repository.ProductRepository;
import com.sia.salesapp.web.dto.OrderItemRequest;
import com.sia.salesapp.web.dto.OrderRequest;
import com.sia.salesapp.web.dto.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo;
    private final OrderComputationService computationService;
    private final ProductRepository productRepo;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest req) {
        Order o = Order.builder()
                .status(OrderStatus.valueOf(req.status()))
                .currency(req.currency() != null ? req.currency() : "RON")
                .shippingFullName(req.shippingFullName())
                .paymentStatus(PaymentStatus.INITIATED)
                .shippingPhone(req.shippingPhone())
                .shippingAddress(req.shippingAddress())
                .orderItems(new ArrayList<>()) // Initializam lista
                .shippingFee(req.shippingFee() != null ? req.shippingFee() : BigDecimal.ZERO)
                .build();

        if (req.items() != null) {
            for (OrderItemRequest itemReq : req.items()) {
                Product p = productRepo.findById(itemReq.productId())
                        .orElseThrow(() -> new EntityNotFoundException("Produs inexistent: " + itemReq.productId()));

                OrderItem orderItem = OrderItem.builder()
                        .order(o)
                        .product(p)
                        .quantity(itemReq.quantity())
                        .unitPrice(p.getPrice())
                        .vatRate(p.getVatRate())
                        .build();

                o.getOrderItems().add(orderItem);
            }
        }

        computationService.computeTotals(o);

        o = repo.save(o);

        return mapToResponse(o);
    }

    @Override
    @Transactional
    public OrderResponse update(Long id, OrderRequest req) {
        Order o = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order inexistent"));

        // Actualizăm câmpurile editabile
        if (req.status() != null) {
            o.setStatus(OrderStatus.valueOf(req.status()));
        }

        // Dacă se schimbă taxa de livrare
        if (req.shippingFee() != null) {
            o.setShippingFee(req.shippingFee());
        }
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
