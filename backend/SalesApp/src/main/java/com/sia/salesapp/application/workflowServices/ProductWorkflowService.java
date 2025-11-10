package com.sia.salesapp.application.workflowServices;

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
    private final InventoryRepository inventoryRepo;

    private final ProductValidator productValidator;
    private final InventoryValidator inventoryValidator;

    /** Create product + init inventory (un singur pas, invariant asigurat) */
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

        p = productRepo.save(p); // cascade salvează și inventory
        return toDto(p);
    }

    /** Ajustare simplă de stoc (+/-), cu regulă de non-negativ */
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
        p.getInventory().setQuantityAvailable(current + delta);

        productRepo.save(p);
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
