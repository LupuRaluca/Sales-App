package com.sia.salesapp.infrastructure.repository;

import com.sia.salesapp.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySkuIgnoreCase(String sku);

    @EntityGraph(attributePaths = {"brand", "category", "inventory"})
    @Query("select p from Product p")
    Page<Product> findAllWithRelations(Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "category", "inventory"})
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithRelations(@Param("id") Long id);
}
