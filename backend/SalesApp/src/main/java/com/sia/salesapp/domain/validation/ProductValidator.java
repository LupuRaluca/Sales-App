package com.sia.salesapp.domain.validation;

import com.sia.salesapp.domain.entity.Brand;
import com.sia.salesapp.domain.entity.Category;
import com.sia.salesapp.domain.exception.BusinessValidationException;
import com.sia.salesapp.infrastructure.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductValidator{

    private final ProductRepository productRepo;

    public ProductValidator(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public void validateOnCreate(String sku, String name, String description,
                                 Brand brand, Category category,
                                 BigDecimal price, String currency, BigDecimal vatRate) {
        if (sku == null || sku.isBlank()) throw new BusinessValidationException("SKU este obligatoriu.");
        validateCommon(name, description, brand, category, price, currency, vatRate);
    }

    public void validateOnUpdate(String name, String description,
                                 Brand brand, Category category,
                                 BigDecimal price, String currency, BigDecimal vatRate) {
        validateCommon(name, description, brand, category, price, currency, vatRate);
    }

    private void validateCommon(String name, String description,
                                Brand brand, Category category,
                                BigDecimal price, String currency, BigDecimal vatRate) {
        if (name == null || name.isBlank()) throw new BusinessValidationException("Numele este obligatoriu.");
        if (description == null || description.isBlank()) throw new BusinessValidationException("Descrierea este obligatorie.");
        if (brand == null) throw new BusinessValidationException("Brand inexistent.");
        if (category == null) throw new BusinessValidationException("Categorie inexistentă.");
        if (price == null || price.signum() < 0) throw new BusinessValidationException("Preț invalid.");
        if (currency == null || currency.length() != 3) throw new BusinessValidationException("Currency trebuie să aibă 3 litere.");
        if (vatRate == null) throw new BusinessValidationException("VAT rate este obligatoriu.");
        if (vatRate.compareTo(new BigDecimal("0.00")) < 0 || vatRate.compareTo(new BigDecimal("99.99")) > 0)
            throw new BusinessValidationException("VAT rate în afara intervalului [0, 99.99].");
    }

    public void assertSkuUniqueOnCreate(String sku) {
        if (productRepo.existsBySkuIgnoreCase(sku))
            throw new BusinessValidationException("SKU deja folosit.");
    }

    public void assertSkuUniqueOnChange(String oldSku, String newSku) {
        if (!oldSku.equalsIgnoreCase(newSku) && productRepo.existsBySkuIgnoreCase(newSku))
            throw new BusinessValidationException("SKU deja folosit.");
    }
}
