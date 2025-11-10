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

    @EntityGraph(attributePaths = {"brand", "category", "inventory"})
    @Query("""
        select p from Product p
        left join p.brand b
        left join p.category c
        where (:term is null 
               or lower(p.name) like lower(concat('%', :term, '%'))
               or lower(p.description) like lower(concat('%', :term, '%'))
               or lower(p.sku) like lower(concat('%', :term, '%')))
          and (:brandId is null or b.id = :brandId)
          and (:categoryId is null or c.id = :categoryId)
        """)
    Page<Product> search(@Param("term") String term,
                         @Param("brandId") Long brandId,
                         @Param("categoryId") Long categoryId,
                         Pageable pageable);
}
