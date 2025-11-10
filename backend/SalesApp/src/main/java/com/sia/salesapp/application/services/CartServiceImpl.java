package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.CartService;
import com.sia.salesapp.domain.entity.Cart;
import com.sia.salesapp.domain.entity.User;
import com.sia.salesapp.infrastructure.repository.CartRepository;
import com.sia.salesapp.infrastructure.repository.UserRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepo;
    private final UserRepository userRepo;

    @Override @Transactional
    public CartResponse create(CartRequest req) {
        User user = userRepo.findById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("User inexistent"));
        Cart c = cartRepo.save(Cart.builder()
                .user(user)
                .createdAt(req.createdAt())
                .updatedAt(req.updatedAt())
                .build());
        return new CartResponse(c.getId(), c.getUser().getId(), c.getCreatedAt(), c.getUpdatedAt());
    }

    @Override @Transactional
    public CartResponse update(Long id, CartRequest req) {
        Cart c = cartRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart inexistent"));
        if (req.userId() != null) {
            User user = userRepo.findById(req.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User inexistent"));
            c.setUser(user);
        }
        c.setCreatedAt(req.createdAt());
        c.setUpdatedAt(req.updatedAt());
        c = cartRepo.save(c);
        return new CartResponse(c.getId(), c.getUser().getId(), c.getCreatedAt(), c.getUpdatedAt());
    }

    @Override @Transactional
    public void delete(Long id) { cartRepo.deleteById(id); }

    @Override
    public CartResponse get(Long id) {
        Cart c = cartRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart inexistent"));
        return new CartResponse(c.getId(), c.getUser().getId(), c.getCreatedAt(), c.getUpdatedAt());
    }

    @Override
    public List<CartResponse> list() {
        return cartRepo.findAll().stream()
                .map(c -> new CartResponse(c.getId(),
                        c.getUser() != null ? c.getUser().getId() : null,
                        c.getCreatedAt(),
                        c.getUpdatedAt()))
                .toList();
    }
}
