package it.polyatskovun.service;

import it.polyatskovun.entity.ProductEntity;
import it.polyatskovun.exception.BadRequestException;
import it.polyatskovun.exception.ProductNotFoundException;
import it.polyatskovun.mapper.ProductMapper;
import it.polyatskovun.model.Product;
import it.polyatskovun.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Transactional
    @CacheEvict(value = "products", key = "#product.category")
    public Product create(Product product) {
        if (product.id() != null) {
            log.info("Product id must be null - {}", product);
            throw new BadRequestException();
        }
        log.info("Start creating product - {}", product);
        ProductEntity entity = mapper.toEntity(product);
        Product saved = save(entity);
        log.info("Product created successfully - {}", saved);
        return saved;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#product.id"),
            @CacheEvict(value = "products", key = "#product.category")
    })
    public Product update(Product product) {
        ProductEntity productEntity = Optional.ofNullable(product.id())
                .flatMap(repository::findById)
                .map(entity -> updateEntity(product, entity))
                .orElseThrow(ProductNotFoundException::new);
        log.info("Start updating product - {}", productEntity);
        Product saved = save(productEntity);
        log.info("Product updated successfully - {}", saved);
        return saved;
    }

    public Page<Product> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toModel);
    }

    @Cacheable("product")
    public Optional<Product> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#product.id"),
            @CacheEvict(value = "products", allEntries = true)
    })
    public void delete(Long id) {
        log.info("Start deleting product by id - {}", id);
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException();
        }
        repository.deleteById(id);
        log.info("Deleted product by id successfully - {}", id);
    }

    @Cacheable("products")
    public List<Product> findByCategory(String category) {
        return repository.findAllByCategory(category)
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    private Product save(ProductEntity entity) {
        ProductEntity saved = repository.save(entity);
        return mapper.toModel(saved);
    }

    private ProductEntity updateEntity(Product product, ProductEntity entity) {
        return mapper.update(product, entity);
    }
}