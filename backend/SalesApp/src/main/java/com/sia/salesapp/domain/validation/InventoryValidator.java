package com.sia.salesapp.domain.validation;

import com.sia.salesapp.domain.entity.Product;
import com.sia.salesapp.domain.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

@Component
public class InventoryValidator {

    public void validateCreate(Product product, Integer quantityAvailable, boolean inventoryAlreadyExists) {
        if (product == null) throw new BusinessValidationException("Produs inexistent.");
        if (quantityAvailable == null || quantityAvailable < 0)
            throw new BusinessValidationException("Cantitatea inițială trebuie să fie ≥ 0.");
        if (inventoryAlreadyExists)
            throw new BusinessValidationException("Există deja inventory pentru acest produs.");
    }

    public void validateAdjust(Product product, int delta, int currentQty) {
        if (product == null) throw new BusinessValidationException("Produs inexistent.");
        // dacă scădem, să nu ajungem sub 0
        if (delta < 0 && currentQty + delta < 0)
            throw new BusinessValidationException("Stoc insuficient pentru scădere.");
    }
}
