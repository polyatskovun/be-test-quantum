package it.polyatskovun.service;

import it.polyatskovun.entity.ProductEntity;
import it.polyatskovun.exception.BadRequestException;
import it.polyatskovun.exception.ProductNotFoundException;
import it.polyatskovun.mapper.ProductMapper;
import it.polyatskovun.model.Product;
import it.polyatskovun.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService service;

    private ProductEntity entity;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(
                1L, "Name", "Desc", BigDecimal.TEN,
                "Cat", 5, LocalDateTime.now(), LocalDateTime.now()
        );
        entity = new ProductEntity();
        entity.setName("Name");
        entity.setDescription("Desc");
        entity.setPrice(BigDecimal.TEN);
        entity.setCategory("Cat");
        entity.setStock(5);
    }

    @Test
    void create_withNullId_savesAndReturns() {
        Product newProduct = new Product(
                null, "Name", "Desc", BigDecimal.TEN,
                "Cat", 5, LocalDateTime.now(), LocalDateTime.now()
        );

        when(mapper.toEntity(newProduct)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toModel(entity)).thenReturn(newProduct);

        Product result = service.create(newProduct);

        assertEquals(newProduct, result);
        verify(repository).save(entity);
    }

    @Test
    void create_withNonNullId_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> service.create(product));
        verifyNoInteractions(repository, mapper);
    }

    @Test
    void update_existing_updatesAndReturns() {
        ProductEntity existingEntity = entity;
        existingEntity.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(mapper.update(product, existingEntity)).thenReturn(existingEntity);
        when(repository.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toModel(existingEntity)).thenReturn(product);

        Product result = service.update(product);

        assertEquals(product, result);
        verify(repository).findById(1L);
        verify(repository).save(existingEntity);
    }

    @Test
    void update_nonExisting_throwsNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.update(product));
    }

    @Test
    void findAll_returnsPageContent() {
        List<ProductEntity> entities = Collections.singletonList(entity);
        Page<ProductEntity> page = new PageImpl<>(entities);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(mapper.toModel(entity)).thenReturn(product);

        Page<Product> result = service.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(product, result.getContent().get(0));
    }

    @Test
    void findById_found_returnsOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toModel(entity)).thenReturn(product);

        Optional<Product> result = service.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(service.findById(1L).isEmpty());
    }

    @Test
    void delete_existing_callsRepository() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_nonExisting_throwsNotFound() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(ProductNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void findByCategory_filtersByCategory() {
        ProductEntity e2 = new ProductEntity();
        e2.setCategory("Other");
        when(repository.findAllByCategory("Cat")).thenReturn(List.of(entity));
        when(mapper.toModel(entity)).thenReturn(product);

        List<Product> result = service.findByCategory("Cat");
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }
}