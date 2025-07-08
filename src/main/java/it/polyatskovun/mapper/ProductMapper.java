package it.polyatskovun.mapper;

import it.polyatskovun.dto.CreateProductRequest;
import it.polyatskovun.dto.ProductResponse;
import it.polyatskovun.dto.UpdateProductRequest;
import it.polyatskovun.entity.ProductEntity;
import it.polyatskovun.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(Product product);

    Product toModel(ProductEntity entity);

    Product toModel(CreateProductRequest request);

    Product toModel(UpdateProductRequest request);

    ProductResponse toResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    ProductEntity update(Product product, @MappingTarget ProductEntity entity);
}