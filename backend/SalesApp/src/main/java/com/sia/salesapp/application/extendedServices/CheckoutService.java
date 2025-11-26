package com.sia.salesapp.application.extendedServices;

import com.sia.salesapp.domain.entity.*;
import com.sia.salesapp.domain.enums.OrderStatus;
import com.sia.salesapp.domain.enums.PaymentStatus;
import com.sia.salesapp.infrastructure.repository.CartRepository;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.infrastructure.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo; // Pentru a lua datele proaspete despre stoc

    // Serviciile noastre existente
    private final OrderComputationService computationService;
    private final ProductWorkflowService productWorkflowService;
    private final AuditService auditService;

    @Transactional
    public Long placeOrderFromCart(Long userId, String shippingAddress, String shippingName, String shippingPhone) {

        Cart cart = cartRepo.findAll().stream()
                .filter(c -> c.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nu exista coș pentru userul " + userId));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Coșul este gol!");
        }

        for (CartItem ci : cart.getCartItems()) {
            Product p = ci.getProduct();
            int requested = ci.getQuantity();
            int available = (p.getInventory() != null) ? p.getInventory().getQuantityAvailable() : 0;

            if (available < requested) {
                throw new IllegalArgumentException("Stoc insuficient pentru produsul: " + p.getName() +
                        ". Cerut: " + requested + ", Disponibil: " + available);
            }
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.INITIATED) // Plata inca nu e facuta
                .shippingFullName(shippingName)
                .shippingAddress(shippingAddress)
                .shippingPhone(shippingPhone)
                .currency("RON")
                .orderItems(new ArrayList<>())
                .build();

        for (CartItem ci : cart.getCartItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice()) // Pretul inghetat din cos (sau il luam din produs proaspat)
                    .vatRate(ci.getProduct().getVatRate())
                    .build();
            order.getOrderItems().add(oi);
        }

        computationService.computeTotals(order);

        order = orderRepo.save(order);

        for (OrderItem oi : order.getOrderItems()) {
            // Folosim serviciul tau de workflow care are validari si audit
            // delta negativ = scadere stoc
            productWorkflowService.adjustStock(oi.getProduct().getId(), -oi.getQuantity());
        }

        cart.getCartItems().clear(); // OrphanRemoval=true va sterge liniile din DB
        cart.setTotalPrice(java.math.BigDecimal.ZERO);
        cartRepo.save(cart);

        auditService.logAction("CHECKOUT", "Order", order.getId(), "Created from Cart ID: " + cart.getId());

        return order.getId();
    }
}