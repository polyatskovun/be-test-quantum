package it.polyatskovun.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Product(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stock,
        LocalDateTime createdDate,
        LocalDateTime lastUpdatedDate
) {

}