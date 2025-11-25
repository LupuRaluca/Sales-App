package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.iServices.InvoiceService;
import com.sia.salesapp.web.dto.InvoiceRequest;
import com.sia.salesapp.web.dto.InvoiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @GetMapping
    public List<InvoiceResponse> list() { return service.list(); }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@RequestBody @Valid InvoiceRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> update(@PathVariable Long id, @RequestBody @Valid InvoiceRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}