package com.sia.salesapp.infrastructure.repository;

import com.sia.salesapp.domain.entity.Product;
import com.sia.salesapp.domain.entity.Brand;
import com.sia.salesapp.domain.entity.Category;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
select p from Product p
left join p.brand b
left join p.category c
where (:term is null or
       p.name ilike :term or
       p.description ilike :term or
       p.sku ilike :term)
  and (:brandId is null or b.id = :brandId)
  and (:categoryId is null or c.id = :categoryId)
""")
    Page<Product> search(@Param("term") String term,
                         @Param("brandId") Long brandId,
                         @Param("categoryId") Long categoryId,
                         Pageable pageable);



    boolean existsBySkuIgnoreCase(String sku);
}
