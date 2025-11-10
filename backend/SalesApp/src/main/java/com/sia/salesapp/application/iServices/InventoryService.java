package com.sia.salesapp.application.iServices;

import com.sia.salesapp.web.dto.*;
import java.util.List;

public interface InventoryService {
    InventoryResponse create(InventoryCreateRequest req);
    InventoryResponse update(Long id, InventoryUpdateRequest req);
    void delete(Long id);
    InventoryResponse get(Long id);
    List<InventoryResponse> list();
}
