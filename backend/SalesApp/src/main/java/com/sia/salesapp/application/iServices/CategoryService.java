package com.sia.salesapp.application.iServices;

import com.sia.salesapp.web.dto.*;
import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest req);
    CategoryResponse update(Long id, CategoryRequest req);
    void delete(Long id);
    CategoryResponse get(Long id);
    List<CategoryResponse> list();
}
