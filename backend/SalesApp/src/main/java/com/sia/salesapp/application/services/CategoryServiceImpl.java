package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.CategoryService;
import com.sia.salesapp.domain.entity.Category;
import com.sia.salesapp.infrastructure.repository.CategoryRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;

    @Override @Transactional
    public CategoryResponse create(CategoryRequest req) {
        if (repo.existsByNameIgnoreCase(req.name()))
            throw new IllegalArgumentException("Categorie deja existentă");
        Category c = repo.save(Category.builder()
                .name(req.name())
                .description(req.description())
                .build());
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    @Override @Transactional
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category c = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Categorie inexistentă"));
        if (!c.getName().equalsIgnoreCase(req.name()) && repo.existsByNameIgnoreCase(req.name()))
            throw new IllegalArgumentException("Categorie deja existentă");
        c.setName(req.name());
        c.setDescription(req.description());
        c = repo.save(c);
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    @Override @Transactional
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public CategoryResponse get(Long id) {
        Category c = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Categorie inexistentă"));
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    @Override
    public List<CategoryResponse> list() {
        return repo.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription()))
                .toList();
    }
}
