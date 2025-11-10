package com.sia.salesapp.application.services;

import com.sia.salesapp.application.iServices.OrderService;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repo;

    @Override @Transactional
    public OrderResponse create(OrderRequest req) {
        Order o = repo.save(Order.builder()
                .orderDate(req.orderDate())
                .status(req.status())
                .totalAmount(req.totalAmount())
                .build());
        return new OrderResponse(o.getId(), o.getOrderDate(), o.getStatus(), o.getTotalAmount());
    }

    @Override @Transactional
    public OrderResponse update(Long id, OrderRequest req) {
        Order o = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
        o.setOrderDate(req.orderDate());
        o.setStatus(req.status());
        o.setTotalAmount(req.totalAmount());
        o = repo.save(o);
        return new OrderResponse(o.getId(), o.getOrderDate(), o.getStatus(), o.getTotalAmount());
    }

    @Override @Transactional
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public OrderResponse get(Long id) {
        Order o = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Order inexistent"));
        return new OrderResponse(o.getId(), o.getOrderDate(), o.getStatus(), o.getTotalAmount());
    }

    @Override
    public List<OrderResponse> list() {
        return repo.findAll().stream()
                .map(o -> new OrderResponse(o.getId(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()))
                .toList();
    }
}
