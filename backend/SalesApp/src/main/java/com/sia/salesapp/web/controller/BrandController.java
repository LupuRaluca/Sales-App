package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.services_crud.BrandService;
import com.sia.salesapp.web.dto.BrandRequest;
import com.sia.salesapp.web.dto.BrandResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService service;

    @GetMapping
    public java.util.List<BrandResponse> list() { return service.list(); }
    @GetMapping("/{id}") public BrandResponse get(@PathVariable Long id) { return service.get(id); }
    @PostMapping
    public BrandResponse create(@Valid @RequestBody BrandRequest req) { return service.create(req); }
    @PutMapping("/{id}") public BrandResponse update(@PathVariable Long id, @Valid @RequestBody BrandRequest req) { return service.update(id, req); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
