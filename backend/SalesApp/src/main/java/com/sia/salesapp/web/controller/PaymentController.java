package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.iServices.PaymentService;
import com.sia.salesapp.web.dto.PaymentRequest;
import com.sia.salesapp.web.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @GetMapping
    public List<PaymentResponse> list() { return service.list(); }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody @Valid PaymentRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(@PathVariable Long id, @RequestBody @Valid PaymentRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}