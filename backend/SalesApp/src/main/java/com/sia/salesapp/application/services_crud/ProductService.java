package com.sia.salesapp.application.services_crud;

import com.sia.salesapp.web.dto.*;
import org.springframework.data.domain.*;

public interface ProductService {
    ProductResponse create(ProductCreateRequest req);
    ProductResponse get(Long id);
    ProductResponse update(Long id, ProductCreateRequest req);
    void delete(Long id);

    Page<ProductResponse> search(String q, Long brandId, Long categoryId,
                                 Pageable pageable);
}
