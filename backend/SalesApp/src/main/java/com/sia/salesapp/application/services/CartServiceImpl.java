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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    private CartResponse mapToResponse(Cart c) {
        List<CartItemResponse> items = new ArrayList<>();

        // Variabile pentru totaluri
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        if (c.getCartItems() != null) {
            items = c.getCartItems().stream()
                    .map(item -> {
                        // Verificăm stocul real al produsului pentru UI
                        Integer stock = (item.getProduct().getInventory() != null)
                                ? item.getProduct().getInventory().getQuantityAvailable()
                                : 0;

                        return new CartItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                stock
                        );
                    })
                    .collect(Collectors.toList());

            // --- CALCUL DINAMIC TVA ---
            for (var item : c.getCartItems()) {
                BigDecimal lineValue = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));

                // Adăugăm la subtotal
                subtotal = subtotal.add(lineValue);

                BigDecimal productVatRate = item.getProduct().getVatRate();

                if (productVatRate != null) {
                    BigDecimal vatDecimal = productVatRate.divide(new BigDecimal("100"));

                    BigDecimal lineTax = lineValue.multiply(vatDecimal);

                    totalTax = totalTax.add(lineTax);
                }
            }
        }

        // Totalul final este Subtotal + Taxele calculate dinamic
        BigDecimal total = subtotal.add(totalTax);

        return new CartResponse(
                c.getId(),
                c.getUser().getId(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                items,
                subtotal,  // Suma prețurilor nete
                totalTax,  // Suma taxelor reale din DB
                total      // Total de plată
        );
    }

    @Override @Transactional
    public CartResponse create(CartRequest req) {
        User user = userRepo.findById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("User inexistent"));
        Cart c = cartRepo.save(Cart.builder()
                .user(user)
                .createdAt(req.createdAt())
                .updatedAt(req.updatedAt())
                .totalPrice(BigDecimal.ZERO)
                .cartItems(new ArrayList<>())
                .build());
        return mapToResponse(c);
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
        return mapToResponse(c);
    }

    @Override @Transactional
    public void delete(Long id) { cartRepo.deleteById(id); }

    @Override @Transactional // Important pentru a încărca produsele (Lazy Loading)
    public CartResponse get(Long id) {
        Cart c = cartRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart inexistent"));
        return mapToResponse(c);
    }

    @Override @Transactional
    public List<CartResponse> list() {
        return cartRepo.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void addItem(Long userId, Long productId, int quantity) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
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
            cart.setCartItems(new ArrayList<>());
        }

        var existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            var item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            var newItem = com.sia.salesapp.domain.entity.CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();
            cart.getCartItems().add(newItem);
        }

        // Recalculare total
        BigDecimal total = BigDecimal.ZERO;
        for (var item : cart.getCartItems()) {
            total = total.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        cart.setTotalPrice(total);

        cartRepo.save(cart);
    }

    @Override
    @Transactional
    public CartResponse getByUserId(Long userId) {
        Cart c = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Nu există un coș activ pentru userul " + userId));

        // Acum folosim metoda helper care populează TOATE datele
        return mapToResponse(c);
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Coș inexistent"));

        if (cart.getCartItems() != null) {
            // Ștergem itemul care are productId-ul respectiv
            boolean removed = cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));

            if (removed) {
                recalculateTotal(cart);
                cartRepo.save(cart);
            }
        }
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long userId, Long productId, int newQuantity) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Coș inexistent"));

        if (cart.getCartItems() != null) {
            var itemOpt = cart.getCartItems().stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst();

            if (itemOpt.isPresent()) {
                var item = itemOpt.get();
                if (newQuantity <= 0) {
                    // Dacă cantitatea e 0 sau mai mică, ștergem produsul de tot
                    cart.getCartItems().remove(item);
                } else {
                    // Altfel, actualizăm cantitatea
                    item.setQuantity(newQuantity);
                }
                recalculateTotal(cart);
                cartRepo.save(cart);
            }
        }
    }

    // Helper method ca să nu duplicăm codul de calcul total
    private void recalculateTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (var item : cart.getCartItems()) {
            total = total.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        cart.setTotalPrice(total);
    }
}