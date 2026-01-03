package com.sia.salesapp.application.extendedServices;

import com.sia.salesapp.domain.entity.*;
import com.sia.salesapp.domain.enums.OrderStatus;
import com.sia.salesapp.domain.enums.PaymentStatus;
import com.sia.salesapp.infrastructure.repository.CartRepository;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepo;
    private final OrderRepository orderRepo;

    private final ProductWorkflowService productWorkflowService;
    private final AuditService auditService;

    @Transactional
    public Long placeOrderFromCart(Long userId, String shippingAddress, String shippingName, String shippingPhone) {

        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Nu exista coș pentru userul " + userId));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Coșul este gol!");
        }

        for (CartItem ci : cart.getCartItems()) {
            Product p = ci.getProduct();
            int requested = ci.getQuantity();
            int available = (p.getInventory() != null) ? p.getInventory().getQuantityAvailable() : 0;

            if (available < requested) {
                throw new IllegalArgumentException("Stoc insuficient pentru: " + p.getName());
            }
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.INITIATED)
                .shippingFullName(shippingName)
                .shippingAddress(shippingAddress)
                .shippingPhone(shippingPhone)
                .currency("RON")
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        for (CartItem ci : cart.getCartItems()) {
            BigDecimal vatRate = ci.getProduct().getVatRate();
            if (vatRate == null) {
                vatRate = new BigDecimal("19.00");
            }

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice())
                    .vatRate(vatRate)
                    .build();
            order.getOrderItems().add(oi);

            // Calcule per linie
            BigDecimal lineTotal = ci.getUnitPrice().multiply(new BigDecimal(ci.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            // Calcul TVA
            BigDecimal vatPercent = vatRate.divide(new BigDecimal("100"));
            BigDecimal lineTax = lineTotal.multiply(vatPercent);
            taxTotal = taxTotal.add(lineTax);
        }

        order.setSubtotal(subtotal);
        order.setTaxTotal(taxTotal);
        order.setShippingFee(BigDecimal.ZERO);
        order.setGrandTotal(subtotal.add(taxTotal));

        order = orderRepo.save(order);

        for (OrderItem oi : order.getOrderItems()) {
            productWorkflowService.adjustStock(oi.getProduct().getId(), -oi.getQuantity());
        }

        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepo.save(cart);

        auditService.logAction("CHECKOUT", "Order", order.getId(), "Created from Cart ID: " + cart.getId());

        return order.getId();
    }
}