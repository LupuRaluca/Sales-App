package com.sia.salesapp.application.services;

import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.infrastructure.repository.OrderRepository;
import com.sia.salesapp.web.dto.OrderRequest;
import com.sia.salesapp.web.dto.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // folosim mock-uri cu Mockito
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository; // mock pentru repo

    @InjectMocks
    private OrderServiceImpl orderService; // serviciul testat

    private Order order;
    private OrderRequest orderRequest;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now(); // data curenta

        // cerere standard
        orderRequest = new OrderRequest(today, "NEW", BigDecimal.valueOf(150.0));

        // obiect Order pentru test
        order = Order.builder()
                .id(1L)
                .orderDate(today)
                .status("NEW")
                .totalAmount(BigDecimal.valueOf(150.0))
                .build();
    }

    // --- create() ---

    @Test
    void create_ShouldReturnOrderResponse_WhenSaved() {
        when(orderRepository.save(any(Order.class))).thenReturn(order); // simuleaza salvarea

        OrderResponse response = orderService.create(orderRequest); // apel metoda

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("NEW", response.status());
        assertEquals(BigDecimal.valueOf(150.0), response.totalAmount());

        verify(orderRepository, times(1)).save(any(Order.class)); // verificam apelul
    }

    // --- update() ---

    @Test
    void update_ShouldReturnUpdatedOrderResponse_WhenOrderExists() {
        Long orderId = 1L;
        LocalDate newDate = today.plusDays(1);
        OrderRequest updateRequest = new OrderRequest(newDate, "PAID", BigDecimal.valueOf(200.0));

        Order updatedOrder = Order.builder()
                .id(orderId)
                .orderDate(newDate)
                .status("PAID")
                .totalAmount(BigDecimal.valueOf(200.0))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order)); // gasim comanda
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder); // salvam modificata

        OrderResponse response = orderService.update(orderId, updateRequest);

        assertNotNull(response);
        assertEquals("PAID", response.status());
        assertEquals(BigDecimal.valueOf(200.0), response.totalAmount());
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty()); // nu gasim comanda

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.update(99L, orderRequest)
        );

        assertEquals("Order inexistent", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }

    // --- delete() ---

    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        doNothing().when(orderRepository).deleteById(1L); // nu face nimic real

        orderService.delete(1L); // apelam metoda

        verify(orderRepository, times(1)).deleteById(1L); // verificam apelul
    }

    // --- get() ---

    @Test
    void get_ShouldReturnOrderResponse_WhenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order)); // gasim comanda

        OrderResponse response = orderService.get(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("NEW", response.status());
    }

    @Test
    void get_ShouldThrowEntityNotFoundException_WhenNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty()); // lipsa

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.get(1L)
        );

        assertEquals("Order inexistent", ex.getMessage());
    }

    // --- list() ---

    @Test
    void list_ShouldReturnListOfOrderResponses() {
        Order order2 = Order.builder()
                .id(2L)
                .orderDate(today.plusDays(1))
                .status("PAID")
                .totalAmount(BigDecimal.valueOf(300.0))
                .build();

        when(orderRepository.findAll()).thenReturn(List.of(order, order2)); // doua comenzi

        List<OrderResponse> list = orderService.list();

        assertEquals(2, list.size());
        assertEquals("NEW", list.get(0).status());
        assertEquals("PAID", list.get(1).status());
    }

    @Test
    void list_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderRepository.findAll()).thenReturn(List.of()); // lista goala

        List<OrderResponse> list = orderService.list();

        assertTrue(list.isEmpty());
    }
}
