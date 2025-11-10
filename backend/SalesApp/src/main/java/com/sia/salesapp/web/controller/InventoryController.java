package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.iServices.InventoryService;
import com.sia.salesapp.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    public InventoryResponse create(@Valid @RequestBody InventoryCreateRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public InventoryResponse update(@PathVariable Long id,
                                    @Valid @RequestBody InventoryUpdateRequest req) {
        return service.update(id, req);
    }

    @GetMapping("/{id}")
    public InventoryResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<InventoryResponse> list() {
        return service.list();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
