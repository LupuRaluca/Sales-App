package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.BrandService;
import com.sia.salesapp.domain.entity.Brand;
import com.sia.salesapp.infrastructure.repository.BrandRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository repo;

    @Override @Transactional
    public BrandResponse create(BrandRequest req) {
        if (repo.existsByNameIgnoreCase(req.name()))
            throw new IllegalArgumentException("Brand deja existent");
        Brand b = repo.save(Brand.builder()
                .name(req.name())
                .description(req.description())
                .build());
        return new BrandResponse(b.getId(), b.getName(), b.getDescription());
    }

    @Override @Transactional
    public BrandResponse update(Long id, BrandRequest req) {
        Brand b = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Brand inexistent"));
        if (!b.getName().equalsIgnoreCase(req.name()) && repo.existsByNameIgnoreCase(req.name()))
            throw new IllegalArgumentException("Brand deja existent");
        b.setName(req.name());
        b.setDescription(req.description());
        b = repo.save(b);
        return new BrandResponse(b.getId(), b.getName(), b.getDescription());
    }

    @Override @Transactional
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public BrandResponse get(Long id) {
        Brand b = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Brand inexistent"));
        return new BrandResponse(b.getId(), b.getName(), b.getDescription());
    }

    @Override
    public List<BrandResponse> list() {
        return repo.findAll().stream()
                .map(b -> new BrandResponse(b.getId(), b.getName(), b.getDescription()))
                .toList();
    }
}
