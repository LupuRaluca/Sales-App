package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.ProductService;
import com.sia.salesapp.domain.entity.Brand;
import com.sia.salesapp.domain.entity.Category;
import com.sia.salesapp.domain.entity.Product;
import com.sia.salesapp.infrastructure.repository.BrandRepository;
import com.sia.salesapp.infrastructure.repository.CategoryRepository;
import com.sia.salesapp.infrastructure.repository.ProductRepository;
import com.sia.salesapp.web.dto.ProductCreateRequest;
import com.sia.salesapp.web.dto.ProductResponse;
import com.sia.salesapp.web.dto.ProductUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;
    private final CategoryRepository categoryRepo;

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest req) {
        if (productRepo.existsBySkuIgnoreCase(req.sku())) {
            throw new IllegalArgumentException("SKU deja folosit");
        }
        Product p = new Product();
        p.setSku(req.sku());
        applyFromCreate(p, req);
        p = productRepo.save(p);
        return toDto(p);
    }

    @Override
    public ProductResponse get(Long id) {
        Product p = productRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Produs inexistent"));
        return toDto(p);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest req) {
        Product p = productRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Produs inexistent"));

        applyFromUpdate(p, req);
        p = productRepo.save(p);
        return toDto(p);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepo.deleteById(id);
    }

    @Override
    public List<ProductResponse> list() {
        return productRepo.findAllWithRelations(Pageable.unpaged())
                .getContent().stream()
                .map(this::toDto)
                .toList();
    }

    /* ===================== Helpers ===================== */

    private void applyFromCreate(Product p, ProductCreateRequest req) {
        // câmpuri comune
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setCurrency(req.currency());
        p.setVatRate(req.vatRate());

        // relații
        Brand brand = brandRepo.findById(req.brandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand inexistent"));
        Category cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categorie inexistentă"));
        p.setBrand(brand);
        p.setCategory(cat);
    }

    private void applyFromUpdate(Product p, ProductUpdateRequest req) {
        // câmpuri comune
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setCurrency(req.currency());
        p.setVatRate(req.vatRate());

        // relații
        Brand brand = brandRepo.findById(req.brandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand inexistent"));
        Category cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categorie inexistentă"));
        p.setBrand(brand);
        p.setCategory(cat);
    }

    private ProductResponse toDto(Product p) {
        Integer qty = (p.getInventory() != null) ? p.getInventory().getQuantityAvailable() : null;
        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getBrand() != null ? p.getBrand().getId() : null,
                p.getBrand() != null ? p.getBrand().getName() : null,
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getPrice(),
                p.getCurrency(),
                p.getVatRate(),
                qty
        );
    }
}
