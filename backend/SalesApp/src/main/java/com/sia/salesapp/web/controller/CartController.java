package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.extendedServices.CheckoutService;
import com.sia.salesapp.application.iServices.CartService;
import com.sia.salesapp.web.dto.CartRequest;
import com.sia.salesapp.web.dto.CartResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CheckoutService checkoutService;
    private final CartService service;

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<CartResponse> create(@RequestBody @Valid CartRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> update(@PathVariable Long id, @RequestBody @Valid CartRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<String> checkout(@PathVariable Long userId,
                                           @RequestParam String address,
                                           @RequestParam String name,
                                           @RequestParam String phone) {
        Long orderId = checkoutService.placeOrderFromCart(userId, address, name, phone);
        return ResponseEntity.ok("Comanda plasata cu succes! ID: " + orderId);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addItem(@PathVariable Long userId,
                                        @RequestParam Long productId,
                                        @RequestParam int quantity) {
        service.addItem(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }
}