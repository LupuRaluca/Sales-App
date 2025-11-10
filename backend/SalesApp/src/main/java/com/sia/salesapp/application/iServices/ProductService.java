package com.sia.salesapp.application.iServices;

import com.sia.salesapp.web.dto.*;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductCreateRequest req);
    ProductResponse get(Long id);
    ProductResponse update(Long id, ProductUpdateRequest req);
    void delete(Long id);
    List<ProductResponse> list();
}
