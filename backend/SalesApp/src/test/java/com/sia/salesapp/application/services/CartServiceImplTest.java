package com.sia.salesapp.application.services;

import com.sia.salesapp.domain.entity.Cart;
import com.sia.salesapp.domain.entity.User;
import com.sia.salesapp.infrastructure.repository.CartRepository;
import com.sia.salesapp.infrastructure.repository.UserRepository;
import com.sia.salesapp.web.dto.CartRequest;
import com.sia.salesapp.web.dto.CartResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // permite folosirea mock-urilor
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository; // repo mock pentru Cart

    @Mock
    private UserRepository userRepository; // repo mock pentru User

    @InjectMocks
    private CartServiceImpl cartService; // injecteaza mock-urile in serviciu

    private User user;
    private Cart cart;
    private CartRequest cartRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now(); // timp curent

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        cartRequest = new CartRequest(1L, now, now); // cerere de test

        cart = Cart.builder()
                .id(10L)
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // --- create() ---

    @Test
    void create_ShouldReturnCartResponse_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // user gasit
        when(cartRepository.save(any(Cart.class))).thenReturn(cart); // simuleaza salvarea

        CartResponse response = cartService.create(cartRequest); // apelam metoda testata

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(1L, response.userId());
        assertEquals(now, response.createdAt());

        verify(userRepository, times(1)).findById(1L); // verificam ca s-a apelat o data
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void create_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty()); // user lipsa

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.create(cartRequest)
        );

        assertEquals("User inexistent", exception.getMessage());
        verify(cartRepository, never()).save(any()); // nu se salveaza nimic
    }

    // --- update() ---

    @Test
    void update_ShouldReturnUpdatedCartResponse_WhenCartAndUserExist() {
        Long cartId = 10L;
        User newUser = User.builder().id(2L).username("newuser").build();
        LocalDateTime newDate = now.plusSeconds(10); // timp nou

        CartRequest updateRequest = new CartRequest(2L, newDate, newDate);
        Cart updatedCart = Cart.builder()
                .id(cartId)
                .user(newUser)
                .createdAt(newDate)
                .updatedAt(newDate)
                .build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart)); // gasim cosul
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser)); // gasim noul user
        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart); // salvam cosul updatat

        CartResponse response = cartService.update(cartId, updateRequest);

        assertNotNull(response);
        assertEquals(cartId, response.id());
        assertEquals(2L, response.userId());
        assertEquals(newDate, response.updatedAt());
    }

    @Test
    void update_ShouldUpdateTimestamps_WhenUserIdIsNullInRequest() {
        Long cartId = 10L;
        LocalDateTime newDate = now.plusMinutes(5);

        CartRequest updateRequest = new CartRequest(null, newDate, newDate); // fara user nou
        Cart updatedCart = Cart.builder()
                .id(cartId)
                .user(user) // ramane userul vechi
                .createdAt(newDate)
                .updatedAt(newDate)
                .build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);

        CartResponse response = cartService.update(cartId, updateRequest);

        assertNotNull(response);
        assertEquals(1L, response.userId()); // acelasi user
        assertEquals(newDate, response.updatedAt());
        verify(userRepository, never()).findById(anyLong()); // nu se cauta user
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenCartNotFound() {
        Long invalidCartId = 99L;
        when(cartRepository.findById(invalidCartId)).thenReturn(Optional.empty()); // cos lipsa

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.update(invalidCartId, cartRequest)
        );

        assertEquals("Cart inexistent", exception.getMessage());
        verify(userRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenNewUserNotFound() {
        Long cartId = 10L;
        Long invalidUserId = 99L;
        CartRequest updateRequest = new CartRequest(invalidUserId, now, now);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart)); // cos gasit
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty()); // user lipsa

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.update(cartId, updateRequest)
        );

        assertEquals("User inexistent", exception.getMessage());
    }

    // --- delete() ---

    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        Long cartId = 10L;
        doNothing().when(cartRepository).deleteById(cartId); // nu face nimic real

        cartService.delete(cartId); // apel testat

        verify(cartRepository, times(1)).deleteById(cartId); // verificam apelul
    }

    // --- get() ---

    @Test
    void get_ShouldReturnCartResponse_WhenCartExists() {
        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.get(10L); // apel metoda get()

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(1L, response.userId());
    }

    @Test
    void get_ShouldThrowEntityNotFoundException_WhenCartNotFound() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty()); // cos lipsa

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.get(99L)
        );

        assertEquals("Cart inexistent", exception.getMessage());
    }

    // --- list() ---

    @Test
    void list_ShouldReturnListOfCartResponses() {
        User user2 = User.builder().id(2L).build();
        Cart cart2 = Cart.builder().id(11L).user(user2).build();
        Cart cart3_NoUser = Cart.builder().id(12L).user(null).build(); // fara user

        List<Cart> carts = List.of(cart, cart2, cart3_NoUser);
        when(cartRepository.findAll()).thenReturn(carts); // returneaza 3 cosuri

        List<CartResponse> responseList = cartService.list(); // apelam list()

        assertNotNull(responseList);
        assertEquals(3, responseList.size());
        assertEquals(1L, responseList.get(0).userId());
        assertEquals(2L, responseList.get(1).userId());
        assertNull(responseList.get(2).userId()); // verifica user null
    }

    @Test
    void list_ShouldReturnEmptyList_WhenNoCartsExist() {
        when(cartRepository.findAll()).thenReturn(List.of()); // lista goala

        List<CartResponse> responseList = cartService.list();

        assertNotNull(responseList);
        assertTrue(responseList.isEmpty()); // nu exista cosuri
    }
}
