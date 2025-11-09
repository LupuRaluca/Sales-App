package com.sia.salesapp.application.services;

import com.sia.salesapp.application.services_crud.ProductService;
import com.sia.salesapp.domain.entity.*;
import com.sia.salesapp.infrastructure.repository.*;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;
    private final CategoryRepository categoryRepo;

    @Override @Transactional
    public ProductResponse create(ProductCreateRequest req) {
        if (productRepo.existsBySkuIgnoreCase(req.sku()))
            throw new IllegalArgumentException("SKU deja folosit");

        Product p = new Product();
        apply(p, req);
        return toDto(productRepo.save(p));
    }

    @Override
    public ProductResponse get(Long id) {
        return toDto(productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produs inexistent")));
    }

    @Override @Transactional
    public ProductResponse update(Long id, ProductCreateRequest req) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produs inexistent"));
        // dacă vrei să validezi SKU duplicat la update:
        if (!p.getSku().equalsIgnoreCase(req.sku()) && productRepo.existsBySkuIgnoreCase(req.sku()))
            throw new IllegalArgumentException("SKU deja folosit");
        apply(p, req);
        return toDto(productRepo.save(p));
    }

    @Override @Transactional
    public void delete(Long id) {
        productRepo.deleteById(id);
    }

    @Override
    public Page<ProductResponse> search(String q, Long brandId, Long categoryId, Pageable pageable) {
        String term = (q == null || q.isBlank()) ? null : "%" + q.trim() + "%";
        return productRepo.search(term, brandId, categoryId, pageable).map(this::toDto);
    }



    private void apply(Product p, ProductCreateRequest req) {
        p.setSku(req.sku());
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setCurrency(req.currency());
        p.setVatRate(req.vatRate());

        p.setBrand(req.brandId() == null ? null :
                brandRepo.findById(req.brandId()).orElseThrow(() -> new EntityNotFoundException("Brand inexistent")));
        p.setCategory(req.categoryId() == null ? null :
                categoryRepo.findById(req.categoryId()).orElseThrow(() -> new EntityNotFoundException("Categorie inexistentă")));
    }

    private ProductResponse toDto(Product p) {
        return new ProductResponse(
                p.getId(), p.getSku(), p.getName(), p.getDescription(),
                p.getBrand() != null ? p.getBrand().getId() : null,
                p.getBrand() != null ? p.getBrand().getName() : null,
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getPrice(), p.getCurrency(), p.getVatRate()
        );
    }
}
