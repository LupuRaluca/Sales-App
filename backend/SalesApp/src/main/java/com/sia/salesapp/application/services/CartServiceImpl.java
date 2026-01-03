package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.CartService;
import com.sia.salesapp.domain.entity.Cart;
import com.sia.salesapp.domain.entity.User;
import com.sia.salesapp.infrastructure.repository.CartRepository;
import com.sia.salesapp.infrastructure.repository.ProductRepository;
import com.sia.salesapp.infrastructure.repository.UserRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    @Override @Transactional
    public CartResponse create(CartRequest req) {
        User user = userRepo.findById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("User inexistent"));
        Cart c = cartRepo.save(Cart.builder()
                .user(user)
                .createdAt(req.createdAt())
                .updatedAt(req.updatedAt())
                .totalPrice(java.math.BigDecimal.ZERO)
                .build());
        return new CartResponse(c.getId(), c.getUser().getId(), c.getCreatedAt(), c.getUpdatedAt());
    }

    @Override @Transactional
    public CartResponse update(Long id, CartRequest req) {
        Cart c = cartRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart inexistent"));
        if (req.userId() != null) {
            User user = userRepo.findById(req.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User inexistent"));
            c.setUser(user);
        }
        c.setCreatedAt(req.createdAt());
        c.setUpdatedAt(req.updatedAt());
        c = cartRepo.save(c);
        return new CartResponse(c.getId(), c.getUser().getId(), c.getCreatedAt(), c.getUpdatedAt());
    }

    @Override @Transactional
    public void delete(Long id) { cartRepo.deleteById(id); }

    @Override
    public CartResponse get(Long id) {
        Cart c = cartRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart inexistent"));
        return new CartResponse(c.getId(), c.getUser().getId(), c.getCreatedAt(), c.getUpdatedAt());
    }

    @Override
    public List<CartResponse> list() {
        return cartRepo.findAll().stream()
                .map(c -> new CartResponse(c.getId(),
                        c.getUser() != null ? c.getUser().getId() : null,
                        c.getCreatedAt(),
                        c.getUpdatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void addItem(Long userId, Long productId, int quantity) {

        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    // Dacă nu există, creăm unul nou
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User inexistent"));

                    return cartRepo.save(Cart.builder()
                            .user(user)
                            .totalPrice(BigDecimal.ZERO)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .cartItems(new ArrayList<>())
                            .build());
                });

        var product = productRepo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produs inexistent"));

        if (cart.getCartItems() == null) {
            cart.setCartItems(new java.util.ArrayList<>());
        }

        var existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Produsul exista, crestem cantitatea
            var item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Produs nou in cos
            var newItem = com.sia.salesapp.domain.entity.CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice()) // Snapshot pret
                    .build();
            cart.getCartItems().add(newItem);
        }

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (var item : cart.getCartItems()) {
            total = total.add(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())));
        }
        cart.setTotalPrice(total);

        cartRepo.save(cart);
    }

    @Override
    public CartResponse getByUserId(Long userId) {
        // Folosim metoda noua din repository
        Cart c = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Nu există un coș activ pentru userul " + userId));

        return new CartResponse(
                c.getId(),
                c.getUser().getId(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
