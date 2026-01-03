package com.sia.salesapp.application.iServices;
import com.sia.salesapp.web.dto.*;
import java.util.List;
public interface OrderService {
    OrderResponse create(OrderRequest req);
    OrderResponse update(Long id, OrderRequest req);
    void delete(Long id);
    OrderResponse get(Long id);
    List<OrderResponse> list();
    List<OrderResponse> getUserOrders(Long userId);
}
