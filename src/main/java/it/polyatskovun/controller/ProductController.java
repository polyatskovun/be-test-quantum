package it.polyatskovun.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polyatskovun.aop.LogMethod;
import it.polyatskovun.dto.CreateProductRequest;
import it.polyatskovun.dto.ProductResponse;
import it.polyatskovun.dto.UpdateProductRequest;
import it.polyatskovun.exception.ProductNotFoundException;
import it.polyatskovun.mapper.ProductMapper;
import it.polyatskovun.model.Product;
import it.polyatskovun.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Operations about products")
public class ProductController {

    private final ProductService service;
    private final ProductMapper mapper;

    @Operation(summary = "List products", description = "Get paginated list of products")
    @GetMapping
    @LogMethod
    public Page<ProductResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.findAll(pageRequest)
                .map(mapper::toResponse);
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its ID")
    @GetMapping("/{id}")
    @LogMethod
    public ProductResponse getById(@PathVariable Long id) {
        return service.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(ProductNotFoundException::new);
    }

    @Operation(summary = "Create product")
    @PostMapping
    @LogMethod
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        Product model = mapper.toModel(request);
        Product saved = service.create(model);
        return mapper.toResponse(saved);
    }

    @Operation(summary = "Update product")
    @PutMapping("/{id}")
    @LogMethod
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        Product model = mapper.toModel(request);
        Product saved = service.update(model);
        return mapper.toResponse(saved);
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    @LogMethod
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List products by category")
    @GetMapping("/category/{category}")
    @LogMethod
    public List<ProductResponse> getByCategory(@PathVariable String category) {
        return service.findByCategory(category)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}