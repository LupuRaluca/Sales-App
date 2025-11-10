package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.iServices.ProductService;
import com.sia.salesapp.application.workflowServices.ProductWorkflowService;
import com.sia.salesapp.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;
    private final ProductWorkflowService workflowService; // <-- nou

    // -------- CRUD --------
    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest req) {
        return productService.create(req);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProductUpdateRequest req) {
        return productService.update(id, req);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping
    public List<ProductResponse> list() {
        return productService.list();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    // -------- WORKFLOW (use-cases cross-entity) --------

    /**
     * Creează produsul și setează stocul inițial într-o singură tranzacție.
     * Exemplu: POST /api/products/workflow/create-with-inventory?qty=10
     */
    @PostMapping("/workflow/create-with-inventory")
    public ProductResponse createWithInventory(@Valid @RequestBody ProductCreateRequest req,
                                               @RequestParam(name = "qty", defaultValue = "0") int qty) {
        return workflowService.createProductWithInventory(req, qty);
    }

    /**
     * Ajustează stocul (pozitiv sau negativ). Nu permite stoc negativ.
     * Exemplu: POST /api/products/{id}/workflow/adjust-stock?delta=-3
     */
    @PostMapping("/{id}/workflow/adjust-stock")
    public void adjustStock(@PathVariable Long id,
                            @RequestParam(name = "delta") int delta) {
        workflowService.adjustStock(id, delta);
    }
}
