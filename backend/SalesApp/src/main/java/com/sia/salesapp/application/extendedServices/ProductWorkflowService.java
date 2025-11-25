package com.sia.salesapp.application.extendedServices;

import com.sia.salesapp.domain.entity.*;
import com.sia.salesapp.domain.validation.InventoryValidator;
import com.sia.salesapp.domain.validation.ProductValidator;
import com.sia.salesapp.infrastructure.repository.*;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductWorkflowService {

    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;
    private final CategoryRepository categoryRepo;

    private final ProductValidator productValidator;
    private final InventoryValidator inventoryValidator;

    private final AuditService auditService;

    @Transactional
    public ProductResponse createProductWithInventory(ProductCreateRequest req, int initialQty) {
        productValidator.assertSkuUniqueOnCreate(req.sku());

        Brand brand = brandRepo.findById(req.brandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand inexistent"));
        Category category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categorie inexistentă"));

        productValidator.validateOnCreate(
                req.sku(), req.name(), req.description(),
                brand, category, req.price(), req.currency(), req.vatRate()
        );

        Product p = Product.builder()
                .sku(req.sku())
                .name(req.name())
                .description(req.description())
                .brand(brand)
                .category(category)
                .price(req.price())
                .currency(req.currency())
                .vatRate(req.vatRate())
                .build();

        Inventory inv = Inventory.builder()
                .product(p)
                .quantityAvailable(initialQty)
                .build();
        p.setInventory(inv);

        inventoryValidator.validateCreate(p, initialQty, false);

        p = productRepo.save(p); // cascade salvează si inventory
        auditService.logAction("CREATE_PRODUCT", "Product", p.getId(), "Created with qty: " + initialQty);
        return toDto(p);
    }


    @Transactional
    public void adjustStock(Long productId, int delta) {
        Product p = productRepo.findByIdWithRelations(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produs inexistent"));

        int current = (p.getInventory() == null) ? 0 : p.getInventory().getQuantityAvailable();
        inventoryValidator.validateAdjust(p, delta, current);

        if (p.getInventory() == null) {
            Inventory inv = Inventory.builder().product(p).quantityAvailable(0).build();
            p.setInventory(inv);
        }
        int newQuantity = current + delta;
        p.getInventory().setQuantityAvailable(newQuantity);

        productRepo.save(p);
        auditService.logAction("ADJUST_STOCK", "Product", p.getId(), "Delta: " + delta + ", New Qty: " + newQuantity);
    }

    private ProductResponse toDto(Product p) {
        Integer qty = p.getInventory() != null ? p.getInventory().getQuantityAvailable() : null;
        return new ProductResponse(
                p.getId(), p.getSku(), p.getName(), p.getDescription(),
                p.getBrand() != null ? p.getBrand().getId() : null,
                p.getBrand() != null ? p.getBrand().getName() : null,
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getPrice(), p.getCurrency(), p.getVatRate(),
                qty
        );
    }
}
