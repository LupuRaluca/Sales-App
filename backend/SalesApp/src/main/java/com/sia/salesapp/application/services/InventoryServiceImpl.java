package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.InventoryService;
import com.sia.salesapp.domain.entity.Inventory;
import com.sia.salesapp.domain.entity.Product;
import com.sia.salesapp.infrastructure.repository.InventoryRepository;
import com.sia.salesapp.infrastructure.repository.ProductRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;

    @Override @Transactional
    public InventoryResponse create(InventoryCreateRequest req) {
        Product p = productRepo.findById(req.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produs inexistent"));

        if (inventoryRepo.existsByProductId(p.getId()))
            throw new IllegalArgumentException("Există deja inventory pentru acest produs");

        Inventory inv = Inventory.builder()
                .product(p)
                .quantityAvailable(req.quantityAvailable())
                .build();

        inv = inventoryRepo.save(inv);
        return toDto(inv);
    }

    @Override @Transactional
    public InventoryResponse update(Long id, InventoryUpdateRequest req) {
        Inventory inv = inventoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory inexistent"));
        inv.setQuantityAvailable(req.quantityAvailable());
        inv = inventoryRepo.save(inv);
        return toDto(inv);
    }

    @Override @Transactional
    public void delete(Long id) {
        inventoryRepo.deleteById(id);
    }

    @Override
    public InventoryResponse get(Long id) {
        Inventory inv = inventoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory inexistent"));
        return toDto(inv);
    }

    @Override
    public List<InventoryResponse> list() {
        return inventoryRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private InventoryResponse toDto(Inventory inv) {
        return new InventoryResponse(
                inv.getId(),
                inv.getProduct() != null ? inv.getProduct().getId() : null,
                inv.getQuantityAvailable()
        );
    }
}
