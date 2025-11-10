package com.sia.salesapp.application.iServices;

import com.sia.salesapp.web.dto.*;
import java.util.List;

public interface BrandService {
    BrandResponse create(BrandRequest req);
    BrandResponse update(Long id, BrandRequest req);
    void delete(Long id);
    BrandResponse get(Long id);
    List<BrandResponse> list();
}
