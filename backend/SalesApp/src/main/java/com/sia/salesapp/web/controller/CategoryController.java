package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.services_crud.CategoryService;
import com.sia.salesapp.web.dto.CategoryRequest;
import com.sia.salesapp.web.dto.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    public java.util.List<CategoryResponse> list() { return service.list(); }
    @GetMapping("/{id}") public CategoryResponse get(@PathVariable Long id) { return service.get(id); }
    @PostMapping
    public CategoryResponse create(@Valid @RequestBody CategoryRequest req) { return service.create(req); }
    @PutMapping("/{id}") public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) { return service.update(id, req); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
}
