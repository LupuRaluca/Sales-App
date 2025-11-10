package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.iServices.BrandService;
import com.sia.salesapp.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Validated
public class BrandController {

    private final BrandService service;

    @PostMapping
    public BrandResponse create(@Valid @RequestBody BrandRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public BrandResponse update(@PathVariable Long id,
                                @Valid @RequestBody BrandRequest req) {
        return service.update(id, req);
    }

    @GetMapping("/{id}")
    public BrandResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<BrandResponse> list() {
        return service.list();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
