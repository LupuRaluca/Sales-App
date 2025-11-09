package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.services_crud.ProductService;
import com.sia.salesapp.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    public Page<ProductResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]).ascending();
        if (sort.endsWith(",desc")) s = s.descending();
        return service.search(q, brandId, categoryId, PageRequest.of(page, size, s));
    }

    @GetMapping("/{id}") public ProductResponse get(@PathVariable Long id) { return service.get(id); }

    @PostMapping public ProductResponse create(@Valid @RequestBody ProductCreateRequest req) { return service.create(req); }

    @PutMapping("/{id}") public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductCreateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
