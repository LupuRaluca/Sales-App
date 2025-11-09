package com.sia.salesapp.application.services;

import com.sia.salesapp.application.services_crud.CategoryService;
import com.sia.salesapp.domain.entity.Category;
import com.sia.salesapp.infrastructure.repository.CategoryRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;

    @Override public CategoryResponse create(CategoryRequest req) {
        if (repo.existsByNameIgnoreCase(req.name()))
            throw new IllegalArgumentException("Category deja existent");
        Category b = repo.save(Category.builder().name(req.name()).description(req.description()).build());
        return new CategoryResponse(b.getId(), b.getName(), b.getDescription());
    }
    @Override public CategoryResponse update(Long id, CategoryRequest req) {
        Category b = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Category inexistent"));
        b.setName(req.name()); b.setDescription(req.description());
        b = repo.save(b);
        return new CategoryResponse(b.getId(), b.getName(), b.getDescription());
    }
    @Override public void delete(Long id) { repo.deleteById(id); }
    @Override public CategoryResponse get(Long id) {
        Category b = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Category inexistent"));
        return new CategoryResponse(b.getId(), b.getName(), b.getDescription());
    }
    @Override public List<CategoryResponse> list() {
        return repo.findAll().stream().map(b -> new CategoryResponse(b.getId(), b.getName(), b.getDescription())).toList();
    }
}
