package com.sia.salesapp.application.services;

import com.sia.salesapp.domain.entity.Brand;
import com.sia.salesapp.infrastructure.repository.BrandRepository;
import com.sia.salesapp.web.dto.BrandRequest;
import com.sia.salesapp.web.dto.BrandResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Inițializează mock-urile definite cu @Mock
class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;


    private Brand brand;
    private BrandRequest brandRequest;

    @BeforeEach
    void setUp() {

        brandRequest = new BrandRequest("TestBrand", "Test Description");

        brand = Brand.builder()
                .id(1L)
                .name("TestBrand")
                .description("Test Description")
                .build();
    }


    @Test
    void create_ShouldReturnBrandResponse_WhenNameIsUnique() {

        when(brandRepository.existsByNameIgnoreCase("TestBrand")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandResponse response = brandService.create(brandRequest);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("TestBrand", response.name());
        assertEquals("Test Description", response.description());

        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenNameExists() {

        when(brandRepository.existsByNameIgnoreCase("TestBrand")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.create(brandRequest) // Executăm metoda
        );

        assertEquals("Brand deja existent", exception.getMessage());

        verify(brandRepository, never()).save(any());
    }

    // --- Teste pentru metoda update() ---

    @Test
    void update_ShouldReturnUpdatedBrandResponse_WhenBrandExistsAndNameIsUnique() {
        Long brandId = 1L;
        BrandRequest updateRequest = new BrandRequest("UpdatedBrand", "Updated Desc");

        Brand updatedBrand = Brand.builder()
                .id(brandId)
                .name("UpdatedBrand")
                .description("Updated Desc")
                .build();

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByNameIgnoreCase("UpdatedBrand")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);

        BrandResponse response = brandService.update(brandId, updateRequest);

        assertNotNull(response);
        assertEquals(brandId, response.id());
        assertEquals("UpdatedBrand", response.name());
        assertEquals("Updated Desc", response.description());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenBrandNotFound() {
        // Arrange
        Long invalidId = 99L;
        // Spunem că nu s-a găsit nimic (Optional.empty())
        when(brandRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> brandService.update(invalidId, brandRequest)
        );

        assertEquals("Brand inexistent", exception.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenNewNameAlreadyExists() {
        // Arrange
        Long brandId = 1L;
        BrandRequest updateRequest = new BrandRequest("ExistingName", "New Desc");

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByNameIgnoreCase("ExistingName")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.update(brandId, updateRequest)
        );

        assertEquals("Brand deja existent", exception.getMessage());
        verify(brandRepository, never()).save(any());
    }

    @Test
    void update_ShouldSucceed_WhenNameIsUnchanged() {

        // Arrange
        Long brandId = 1L;
        BrandRequest updateRequest = new BrandRequest("TestBrand", "New Description Only"); // Nume identic

        Brand updatedBrand = Brand.builder()
                .id(brandId)
                .name("TestBrand")
                .description("New Description Only")
                .build();

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);

        // Act
        BrandResponse response = brandService.update(brandId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("New Description Only", response.description());

        verify(brandRepository, never()).existsByNameIgnoreCase(anyString());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }


    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        // Arrange
        Long brandId = 1L;

        // Act
        brandService.delete(brandId);

        verify(brandRepository, times(1)).deleteById(brandId);
    }


    @Test
    void get_ShouldReturnBrandResponse_WhenBrandExists() {
        // Arrange
        Long brandId = 1L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        // Act
        BrandResponse response = brandService.get(brandId);

        // Assert
        assertNotNull(response);
        assertEquals(brandId, response.id());
        assertEquals("TestBrand", response.name());
    }

    @Test
    void get_ShouldThrowEntityNotFoundException_WhenBrandNotFound() {
        // Arrange
        Long invalidId = 99L;
        when(brandRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> brandService.get(invalidId)
        );
        assertEquals("Brand inexistent", exception.getMessage());
    }


    @Test
    void list_ShouldReturnListOfBrandResponses() {
        // Arrange
        Brand brand2 = Brand.builder().id(2L).name("Brand2").description("Desc2").build();
        List<Brand> brands = List.of(brand, brand2);
        when(brandRepository.findAll()).thenReturn(brands);

        // Act
        List<BrandResponse> responseList = brandService.list();

        // Assert
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals("TestBrand", responseList.get(0).name());
        assertEquals("Brand2", responseList.get(1).name());
    }

    @Test
    void list_ShouldReturnEmptyList_WhenNoBrandsExist() {
        // Arrange
        when(brandRepository.findAll()).thenReturn(List.of()); // sau Collections.emptyList()

        // Act
        List<BrandResponse> responseList = brandService.list();

        // Assert
        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());
    }
}