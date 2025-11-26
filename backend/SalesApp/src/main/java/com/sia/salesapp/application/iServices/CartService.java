package com.sia.salesapp.application.iServices;
import com.sia.salesapp.domain.entity.Cart;
import com.sia.salesapp.web.dto.*;
import java.util.List;
public interface CartService {
    CartResponse create(CartRequest req);
    CartResponse update(Long id, CartRequest req);
    void delete(Long id);
    CartResponse get(Long id);
    List<CartResponse> list();
    void addItem(Long userId, Long productId, int quantity);
    CartResponse getByUserId(Long userId);
}
