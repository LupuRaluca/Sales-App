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

/**
 * Clasă de test unitar pentru BrandServiceImpl.
 * Folosește Mockito pentru a izola dependența de BrandRepository.
 */
@ExtendWith(MockitoExtension.class) // Inițializează mock-urile definite cu @Mock
class BrandServiceImplTest {

    // 1. Definim Mock-ul
    // Acesta este obiectul pe care îl vom controla (dependința)
    @Mock
    private BrandRepository brandRepository;

    // 2. Definim Clasa Sub Test
    // Mock-ul de mai sus (brandRepository) va fi injectat automat în această instanță
    @InjectMocks
    private BrandServiceImpl brandService;

    // Vom folosi aceste obiecte ca date de test standard
    private Brand brand;
    private BrandRequest brandRequest;

    @BeforeEach
    void setUp() {
        // Se execută înainte de fiecare @Test
        // Resetăm datele de test
        brandRequest = new BrandRequest("TestBrand", "Test Description");

        brand = Brand.builder()
                .id(1L)
                .name("TestBrand")
                .description("Test Description")
                .build();
    }

    // --- Teste pentru metoda create()  src/test/java/com/sia/salesapp/application/services/BrandServiceImplTest.java ---

    @Test
    void create_ShouldReturnBrandResponse_WhenNameIsUnique() {
        // Arrange (Pregătirea)
        // 1. Când se caută după nume, spunem că nu există (returnează false)
        when(brandRepository.existsByNameIgnoreCase("TestBrand")).thenReturn(false);
        // 2. Când se salvează (indiferent ce obiect Brand), returnează brand-ul nostru de test
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        // Act (Execuția)
        BrandResponse response = brandService.create(brandRequest);

        // Assert (Verificarea)
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("TestBrand", response.name());
        assertEquals("Test Description", response.description());

        // Verifică dacă metoda save a fost apelată exact o dată
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenNameExists() {
        // Arrange
        // Spunem că brand-ul DEJA EXISTĂ
        when(brandRepository.existsByNameIgnoreCase("TestBrand")).thenReturn(true);

        // Act & Assert
        // Verificăm că aruncă excepția așteptată
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.create(brandRequest) // Executăm metoda
        );

        // Verificăm mesajul excepției
        assertEquals("Brand deja existent", exception.getMessage());

        // Verificăm că metoda save NU a fost apelată niciodată
        verify(brandRepository, never()).save(any());
    }

    // --- Teste pentru metoda update() ---

    @Test
    void update_ShouldReturnUpdatedBrandResponse_WhenBrandExistsAndNameIsUnique() {
        // Arrange
        Long brandId = 1L;
        BrandRequest updateRequest = new BrandRequest("UpdatedBrand", "Updated Desc");

        // Brand-ul actualizat care ne așteptăm să fie returnat de save()
        Brand updatedBrand = Brand.builder()
                .id(brandId)
                .name("UpdatedBrand")
                .description("Updated Desc")
                .build();

        // 1. La findById, returnăm brand-ul original
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        // 2. La verificarea noului nume, spunem că e unic
        when(brandRepository.existsByNameIgnoreCase("UpdatedBrand")).thenReturn(false);
        // 3. La salvare, returnăm brand-ul actualizat
        when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);

        // Act
        BrandResponse response = brandService.update(brandId, updateRequest);

        // Assert
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

        // 1. Găsim brand-ul original ("TestBrand")
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        // 2. Noul nume ("ExistingName") e diferit de cel vechi ("TestBrand")
        //    și spunem că acesta DEJA EXISTĂ
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
        // Test pentru cazul când schimbăm doar descrierea, nu și numele.
        // Ar trebui să funcționeze și să NU verifice existența numelui.

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

        // CRUCIAL: Verificăm că NU s-a apelat `existsByNameIgnoreCase`
        // deoarece numele era același, deci logica de business e corectă
        verify(brandRepository, never()).existsByNameIgnoreCase(anyString());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    // --- Teste pentru metoda delete() ---

    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        // Arrange
        Long brandId = 1L;
        // Nu trebuie să facem "when" pentru o metodă void,
        // dar putem folosi doNothing() dacă e necesar.
        // doNothing().when(brandRepository).deleteById(brandId);

        // Act
        brandService.delete(brandId);

        // Assert
        // Verificăm doar că metoda corectă a fost apelată pe repository
        verify(brandRepository, times(1)).deleteById(brandId);
    }

    // --- Teste pentru metoda get() ---

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

    // --- Teste pentru metoda list() ---

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