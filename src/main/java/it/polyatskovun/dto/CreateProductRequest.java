package it.polyatskovun.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    private String category;

    @NotNull(message = "Stock is mandatory")
    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;
}