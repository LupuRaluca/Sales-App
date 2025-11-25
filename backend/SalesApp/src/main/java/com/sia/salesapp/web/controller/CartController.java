package com.sia.salesapp.web.controller;

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
}