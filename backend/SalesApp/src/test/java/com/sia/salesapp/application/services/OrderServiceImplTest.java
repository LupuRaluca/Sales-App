package com.sia.salesapp.application.services;

import com.sia.salesapp.application.extendedServices.OrderComputationService;
import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.domain.entity.Product;
import com.sia.salesapp.domain.enums.OrderStatus;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.infrastructure.repository.ProductRepository;
import com.sia.salesapp.web.dto.OrderItemRequest;
import com.sia.salesapp.web.dto.OrderRequest;
import com.sia.salesapp.web.dto.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderComputationService computationService; // Noua dependență

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_ShouldReturnResponse() {
        OrderRequest request = new OrderRequest(
                "PENDING",                      // status
                "RON",                          // currency
                BigDecimal.valueOf(25.00),      // shippingFee
                "John Does",                     // shippingFullName
                "0700123456",                   // shippingPhone
                "Strada Test 1",                // shippingAddress
                List.of(new OrderItemRequest(1L, 2)) // items: Produs ID 1, Cantitate 2
        );

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setPrice(BigDecimal.valueOf(100));
        mockProduct.setVatRate(BigDecimal.valueOf(19));

        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setShippingFee(BigDecimal.valueOf(25.00));
        mockOrder.setSubtotal(BigDecimal.valueOf(200));
        mockOrder.setGrandTotal(BigDecimal.valueOf(263)); // 200 + TVA + 25
        mockOrder.setCreatedAt(java.time.Instant.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);


        // Act
        OrderResponse response = orderService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("PENDING", response.status());

        verify(productRepository).findById(1L);
        verify(computationService).computeTotals(any(Order.class)); // Verificam ca s-a facut calculul
        verify(orderRepository).save(any(Order.class));
    }
}