
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
import com.sia.salesapp.web.dto.OrderItemResponse;
import com.sia.salesapp.web.dto.OrderRequest;
import com.sia.salesapp.web.dto.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo;
    private final OrderComputationService computationService;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepository;

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

    @Override
    @Transactional(readOnly = true) // Optimizare pentru citire
    public List<OrderResponse> getUserOrders(Long userId) {

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);


        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Helper pentru curățenie
    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus().name(),
                order.getGrandTotal(),
                items,
                order.getShippingAddress()
        );
    }
}
